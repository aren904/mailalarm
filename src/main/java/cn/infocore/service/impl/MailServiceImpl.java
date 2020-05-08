package cn.infocore.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.Client_;
import cn.infocore.entity.Email_alarm;
import cn.infocore.entity.Fault;
import cn.infocore.entity.Quota;
import cn.infocore.entity.RdsDO;
import cn.infocore.entity.RdsInstanceDO;
import cn.infocore.entity.Vcenter;
import cn.infocore.entity.Virtual_machine;
import cn.infocore.handler.QuotaHandler;
import cn.infocore.mail.MailSender;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.service.MailService;
import cn.infocore.utils.MyDataSource;
import cn.infocore.utils.StupidStringUtil;

//内存中维护的邮件注册列表
@Service
public class MailServiceImpl implements MailService {
    private static final Logger logger = Logger.getLogger(MailServiceImpl.class);
    private Map<String, MailSender> normalSenderMap = null;// 必须线程安全
    private Map<String, MailSender> adminSenderMap = null;

    private MailServiceImpl() {
        this.normalSenderMap = new ConcurrentHashMap<String, MailSender>();
        this.adminSenderMap = new ConcurrentHashMap<String, MailSender>();
        // 初始的时候，先从数据库中获取一次
        logger.info("Start collect mail config from database.");
        QueryRunner qr = MyDataSource.getQueryRunner();
        String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,sender_password,smtp_address,"
                + "smtp_port,smtp_authentication,smtp_user_id,smtp_password,ssl_encrypt,receiver_emails,privilege_level "
                + "from email_alarm,user where email_alarm.user_id=user.id";
        List<Email_alarm> eList = null;
        try {
            eList = qr.query(sql, new BeanListHandler<Email_alarm>(Email_alarm.class));
            if (eList.size() > 0) {
                logger.info("Get mail config count:" + eList.size());
                for (Email_alarm eAlarm : eList) {
                    if (eAlarm.getEnabled() == (byte) 0)
                        continue;
                    MailSender sender = new MailSender(eAlarm);
                    if (eAlarm.getPrivilege_level() < 2) {
                        this.adminSenderMap.put(eAlarm.getUser_id(), sender);
                    }
                    this.normalSenderMap.put(eAlarm.getUser_id(), sender);

                }
                logger.info("Collected mail config finished.");
            } else {
                logger.warn("Collected mail config failed.");
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            // MyDataSource.close(connection);
        }
    }

    private static class MailCenterRestryHolder {
        public static MailServiceImpl instance = new MailServiceImpl();
    }

    public static MailServiceImpl getInstance() {
        return MailCenterRestryHolder.instance;
    }

    public void addAllMailService(List<Email_alarm> l) {
        for (Email_alarm email_alarm : l) {
            MailSender sender = new MailSender(email_alarm);
            if (email_alarm.getPrivilege_level() < 2) {
                this.adminSenderMap.put(email_alarm.getUser_id(), sender);
            }
            this.normalSenderMap.put(email_alarm.getUser_id(), sender);
        }

    }

    // 更新邮件配置还是使用该接口
    public void addMailService(String name) {
        // 通过查数据库，添加到本地，自己构造MailSender对象
        // 初始的时候，先从数据库中获取一次
        String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,sender_password,smtp_address,"
                + "smtp_port,smtp_authentication,smtp_user_id,smtp_password,ssl_encrypt,receiver_emails,privilege_level "
                + "from email_alarm,user where email_alarm.user_id=user.id and email_alarm.user_id=?";
        QueryRunner qr = MyDataSource.getQueryRunner();
        Object[] para = { name };
        List<Email_alarm> elList = null;
        try {
            elList = qr.query(sql, new BeanListHandler<Email_alarm>(Email_alarm.class), para);
            for (Email_alarm email_alarm : elList) {
                if (email_alarm.getEnabled() == (byte) 0) {
                    if (this.normalSenderMap.containsKey(name)) {
                        this.normalSenderMap.remove(name);
                    }
                    continue;
                }
                MailSender sender = new MailSender(email_alarm);
                if (email_alarm.getPrivilege_level() < 2) {
                    this.adminSenderMap.put(email_alarm.getUser_id(), sender);
                }
                this.normalSenderMap.put(name, sender);
            }
        } catch (SQLException e) {
            logger.error("addMailService.", e);
        } finally {
            // MyDataSource.close(connection);
        }
    }

    public void deleteMailService(String name) {
        // 查询数据库，从本地删除
        if (this.normalSenderMap.containsKey(name)) {
            this.normalSenderMap.remove(name);
        }
        if (this.adminSenderMap.containsKey(name)) {
            this.adminSenderMap.remove(name);
        }

    }

    public void notifyCenter(DataArkDTO data_ark, List<Client_> clientList, List<Vcenter> vcList,
            List<Virtual_machine> vmList, List<RdsDO> rdsList, List<RdsInstanceDO> rdsInstances, List<Fault> list_fault) {
        logger.info("Start NotifyCenetr inject mailsender,size:" + list_fault.size() + ",list size:"
                + normalSenderMap.size() + ",data_ark:" + data_ark.getIp() + ",client size:" + clientList.size()
                + ",vcenter size:" + vcList.size() + ",vm size:" + vmList.size());
        String sql = null;
        Object[] condition = null;

        for (Fault fault : list_fault) {
            try {
                logger.info("-----------Userid:" + fault.getUser_id() + ",faultType:" + fault.getType() + ",target:"
                        + fault.getTarget() + ",data_ark ip:" + fault.getData_ark_ip() + ",client_id:"
                        + fault.getClient_id());
                if (fault.getType() == ClientType.SINGLE_VALUE) {
                    // 1.confirm all alarm log for target.
                    sql = "update alarm_log set processed=1 where data_ark_id=? and target_id=? and exception!=3 and exception!=25 and exception!=26";

                    condition = new Object[] { fault.getData_ark_id(), fault.getClient_id() };
                    // logger.error(fault.getUser_id()+" "+fault.getData_ark_id()+"
                    // "+fault.getTarget());
                } else {
                    // add by wxx,for one fault to other fault and not confirm.
                    // current error
                    List<String> currentErrors = new ArrayList<String>();
                    QueryRunner qr = MyDataSource.getQueryRunner();
                    String excepts = "";

                    // 注意这里名称不一致，需要特殊处理
                    if (fault.getClient_type() == ClientType.SINGLE_VALUE) {
                        /*
                         * sql="select exceptions from data_ark where id=?"; excepts=qr.query(sql, new
                         * ExceptHandler(), condition);
                         */

                        excepts = data_ark.getExcept();
                    } else if (fault.getClient_type() == ClientType.VMWARE_VALUE) {
                        /*
                         * sql="select exceptions from client where data_ark_id=? and id=?";
                         * excepts=qr.query(sql, new ExecptHandler(), condition);
                         */
                        for (Client_ c : clientList) {
                            if (fault.getData_ark_id().equals(c.getData_ark_id())
                                    && fault.getClient_id().equals(c.getId())) {
                                excepts = c.getExcept();
                                break;
                            }
                        }
                    } else if (fault.getClient_type() == ClientType.MSCS_VALUE) {
                        /*
                         * sql="select exceptions from vcenter where data_ark_id=? and vcenter_id=?";
                         * excepts=qr.query(sql, new ExceptHandler(), condition);
                         */

                        for (Vcenter vc : vcList) {
                            if (fault.getData_ark_id().equals(vc.getData_ark_id())
                                    && fault.getClient_id().equals(vc.getId())) {
                                excepts = vc.getExcep();
                                break;
                            }
                        }
                    } else if (fault.getClient_type() == ClientType.RAC_VALUE) {
                        for (Virtual_machine vm : vmList) {
                            if (fault.getData_ark_id().equals(vm.getData_ark_id())
                                    && fault.getClient_id().equals(vm.getId())) {
                                excepts = vm.getExcept();
                                break;
                            }
                        }

                        // sql="select exceptions from virtual_machine where data_ark_id=? and id=?";
                        // excepts=qr.query(sql, new ExceptHandler(), condition);
                    } else if (fault.getClient_type() == ClientType.Rds_VALUE) {
                        for (RdsDO rds : rdsList) {
                            if (fault.getData_ark_id().equals(rds.getDataArkId())
                                    && fault.getClient_id().equals(rds.getRdsId())) {
                                excepts = rds.getExceptions();

                                break;
                            }
                        }

                    } else if (fault.getClient_type() == ClientType.RdsInstance_VALUE) {
                        for (RdsInstanceDO object : rdsInstances) {
                            if (fault.getData_ark_id().equals(object.getDataArkDrId())
                                    && fault.getClient_id().equals(object.getInstanceId())) {
                                excepts = object.getExceptions();
                                break;
                            }
                        }
                    }
                    // current error
                    if (excepts != "" && excepts != null) {
                        currentErrors.addAll(Arrays.asList(excepts.split(";")));
                    }
                    logger.info("Current error size:" + currentErrors.size() + ",fault type:" + fault.getClient_type()
                            + "," + currentErrors.toString());

                    // not confirm error

                    sql = "select * from alarm_log where data_ark_id=? and binary target=? and target_id=? and processed=0";
                    condition = new Object[] { fault.getData_ark_id(), fault.getTarget(), fault.getClient_id() };
                    // db error
                    qr = MyDataSource.getQueryRunner();
                    List<Integer> dbErrors = qr.query(sql, new ColumnListHandler<Integer>("exception"), condition);
                    logger.info("DB error condition:" + condition[0] + "," + condition[1] + "DB error:"
                            + dbErrors.toString());

                    logger.info("start to compare current and db errors.");
                    for (Integer type : dbErrors) {
                        if (!currentErrors.contains(String.valueOf(type))) {
                            logger.info(fault.getUser_id() + "," + fault.getData_ark_ip()
                                    + " current not contains db,confirm it:" + type);
                            // 2.current not contains db,confirm it.
                            if (type == 3 || type == 25 || type == 26 || type == 31) {

                                logger.info("VM error not need to confirm.");
                            } else {
                                // remove user id update TODO
                                // sql="update alarm_log set user_id=?,processed=1 where data_ark_id=? and
                                // target_id=? and exeception=?";
                                sql = "update alarm_log set processed=1 where data_ark_id=? and target_id=? and exception=?";
                                // condition= new
                                // Object[]{fault.getUser_id(),fault.getData_ark_id(),fault.getClient_id(),type};
                                condition = new Object[] { fault.getData_ark_id(), fault.getClient_id(), type };
                            }
                        }
                    }

                    for (String type : currentErrors) {

                        if (!dbErrors.contains(Integer.parseInt(type)) && Integer.parseInt(type) != 0) { // insert error
                            logger.info(fault.getUser_id() + "," + fault.getData_ark_ip() + " current is new,insert it:"
                                    + type);
                            // 3.current is new,insert/update it.

                            sql = "insert into alarm_log(timestamp,processed,exception,data_ark_id,data_ark_name,data_ark_ip,target_id,target,last_alarm_timestamp,user_id) values(?,?,?,?,?,?,?,?,?,?)";
                            condition = new Object[] { fault.getTimestamp(), 0L, fault.getType(),
                                    fault.getData_ark_id(), fault.getData_ark_name(), fault.getData_ark_ip(),
                                    fault.getClient_id(), fault.getTarget(), 0L, fault.getUser_id() };
                        } else if (dbErrors.contains(Integer.parseInt(type)) && (Integer.parseInt(type) == 3
                                || Integer.parseInt(type) == 25 || Integer.parseInt(type) == 26)) {
                            // bug#777 ->update time for snapshot error
                            sql = "update alarm_log set timestamp=? where data_ark_id=? and target_id=? and exception=? and processed=0";
                            condition = new Object[] { fault.getTimestamp(), fault.getData_ark_id(),
                                    fault.getClient_id(), type };
                        }
                    }
                }

                QueryRunner qr = MyDataSource.getQueryRunner();
                qr.execute(sql, condition);

                if (fault.getType() != 0) {
                    /*
                     * if(!this.list.containsKey(fault.getUser_id())){
                     * logger.info(fault.getUser_id()+" not set email."); continue; }
                     */

                    for (Map.Entry<String, MailSender> entry : this.normalSenderMap.entrySet()) {
                        String user = entry.getKey();
                        MailSender mailSender = entry.getValue();
                        // 判断是否属于管理员用户
                        Email_alarm conf = mailSender.getConfig();
                        if (conf.getPrivilege_level() == 0 || conf.getPrivilege_level() == 1) {
                            mailSender.judge(fault, user);
                            logger.info(user + " admin or root user start judge...");
                        } else {
                            sql = "select * from quota where user_id=? and data_ark_id=?";
                            Object[] param = { user, fault.getData_ark_id() };
                            QueryRunner qRunner = MyDataSource.getQueryRunner();
                            List<Quota> quotas = qRunner.query(sql, new QuotaHandler(), param);
                            if (!quotas.isEmpty()) {
                                // 包括客户端，VC，虚拟机
                                if (fault.getClient_type().intValue() == 1 || fault.getClient_type().intValue() == 2
                                        || fault.getClient_type().intValue() == 3) {
                                    // 查询该user_id是否和报警客户端存在关系，即该客户端是否是该用户添加过，添加过则给该用户发送报警邮件
                                    Long count = findArkIdAndUserIdAndId(fault, user);
                                    if (count.intValue() > 0) {
                                        mailSender.judge(fault, user);
                                    }
                                } else {
                                    mailSender.judge(fault, user);
                                }
                                logger.info(user + " commom user start judge...");
                            } else {
                                logger.warn("email_alarm table has not user_id:" + user + " and data_ark_id:"
                                        + fault.getData_ark_id());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(fault.getUser_id() + ":" + e);
            }
        }
        // MyDataSource.close(connection);
    }

    protected Long findArkIdAndUserIdAndId(Fault fault, String user) {
        QueryRunner qclent = MyDataSource.getQueryRunner();
        String sql = "";
        if (fault.getClient_type() == 1) {
            sql = "select count(*) from client where user_id=? and data_ark_id=? and id=?";
        } else if (fault.getClient_type() == 2) {
            sql = "select count(*) from vcenter where user_id=? and data_ark_id=? and id=?";
        } else if (fault.getClient_type() == 3) {
            //sql = "select count(*) from virtual_machine where user_id=? and data_ark_id=? and id=?";
            sql = "select count(*) from vcenter_vm inner join vcenter on  vcenter.id= vcenter_vm.vcenter_id and vcenter.user_id=? and vcenter.data_ark_id= ? and vcenter_vm.id=?  ";

        }
        Object[] param1 = { user, fault.getData_ark_id(), fault.getClient_id() };
        try {
            Long count = qclent.query(sql, new ScalarHandler<Long>(), param1);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
        return null;
    }

    public void updateMailService(String name, Email_alarm sender) {
        // 同理，查询数据库，更新
        // this.list.put(name, sender);
    }

    public void sentFault(Collection<FaultSimple> faultSimples) {

        for (FaultSimple faultSimple : faultSimples) {
            // send to normal users
            List<Fault> faults = convertFaultSimple(faultSimple);
            for (Fault fault : faults) {
                String userId = fault.getUser_id();
                MailSender sender = this.normalSenderMap.get(userId);
                if (sender != null) {
                    sender.judge(fault, userId);

                }
            }
            // send all to admin user
            
            Set<Map.Entry<String, MailSender>> senderSet  = this.adminSenderMap.entrySet();
            List<Fault> faultsWithUserIdString  = convertFaultSimpleWithUserString(faultSimple);
            for (Map.Entry<String, MailSender> entry : senderSet) {
                MailSender sender = entry.getValue();
                String userId = entry.getKey();
                for (Fault fault : faultsWithUserIdString) {
                    try {
                        sender.judge(fault,userId);
                    } catch (Exception e) {
                        logger.error("send mail to"+ userId + " failed",e);
                    }
                }
            }
            
        }

    }
    
    List<Fault> convertFaultSimpleWithUserString(FaultSimple faultSimple) {

        Collection<FaultType> faultTypes = faultSimple.getFaultTypes();

        String dataArkId = faultSimple.getDataArkId();
        String dataArkIp = faultSimple.getDataArkIp();
        String data_ark_name = faultSimple.getDataArkName();
        String targetId = faultSimple.getTargetId();
        String targetName = faultSimple.getTargetName();
        ClientType clientType = faultSimple.getClientType();
        List<String> userIds = faultSimple.getUserIds();

        List<Fault> faults = new ArrayList<Fault>();
        for (FaultType faultType : faultTypes) {
                Integer code = faultType.getNumber();
                Fault fault = new Fault();
                fault.setType(code);
                fault.setClient_id(targetId);
                fault.setClient_type(clientType.getNumber());
                fault.setData_ark_id(dataArkId);
                fault.setData_ark_ip(dataArkIp);
                fault.setData_ark_name(data_ark_name);
                fault.setTarget(targetName);
                fault.setUser_id(StupidStringUtil.parseUserIdListToUserIdsString(userIds));
                faults.add(fault);
        }
        return faults;
    }

    List<Fault> convertFaultSimple(FaultSimple faultSimple) {

        Collection<FaultType> faultTypes = faultSimple.getFaultTypes();

        String dataArkId = faultSimple.getDataArkId();
        String dataArkIp = faultSimple.getDataArkIp();
        String data_ark_name = faultSimple.getDataArkName();
        String targetId = faultSimple.getTargetId();
        String targetName = faultSimple.getTargetName();
        ClientType clientType = faultSimple.getClientType();
        List<String> userIds = faultSimple.getUserIds();

        List<Fault> faults = new ArrayList<Fault>();
        for (FaultType faultType : faultTypes) {
            for (String userId : userIds) {
                Integer code = faultType.getNumber();
                Fault fault = new Fault();
                fault.setType(code);
                fault.setClient_id(targetId);
                fault.setClient_type(clientType.getNumber());
                fault.setData_ark_id(dataArkId);
                fault.setData_ark_ip(dataArkIp);
                fault.setData_ark_name(data_ark_name);
                fault.setTarget(targetName);
                fault.setUser_id(userId);
                faults.add(fault);
            }
        }
        return faults;
    }

    public void sentFault(FaultSimple faultSimple) {

        ClientType clientType = faultSimple.getClientType();

        switch (clientType) {
        case SINGLE:

            break;
        case VMWARE:

            break;
        case MSCS:

            break;
        case RAC:

            break;
        case VC:

            break;
        case AIX:

            break;
        case FileSingle:

            break;
        case Rds:

            break;
        case RdsInstance:

            break;
        case Oss:

            break;
        case OssObjectSet:

            break;
        case Ecs:

            break;
        case EcsInstance:

            break;
        case MetaDB:

            break;
        case MetaDBBackup:

            break;
        default:
            break;
        }

    }

}
