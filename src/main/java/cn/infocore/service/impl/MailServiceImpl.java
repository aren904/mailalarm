package cn.infocore.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.entity.*;

import cn.infocore.handler.UserIdHd;
import cn.infocore.handler.dataIdHandler;

import cn.infocore.handler.email_alarmHandler;
import cn.infocore.utils.MyDataSource;
import org.apache.commons.dbutils.QueryRunner;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.handler.QuotaHandler;
import cn.infocore.mail.MailSender;

import cn.infocore.service.MailService;


//内存中维护的邮件注册列表
@Service
public class MailServiceImpl implements MailService {
    private static final Logger logger = Logger.getLogger(MailServiceImpl.class);
    private Map<String, MailSender> normalSenderMap = null;// 必须线程安全
    private Map<String, MailSender> adminSenderMap = null;

    private MailServiceImpl() {

        MailCenterRestryHolder.instance = this;

        this.normalSenderMap = new ConcurrentHashMap<String, MailSender>();
        this.adminSenderMap = new ConcurrentHashMap<String, MailSender>();
        // 初始的时候，先从数据库中获取一次
        logger.info("Start to collect mail config from database.");
        QueryRunner qr = MyDataSource.getQueryRunner();
//        String sql = "select user_uuid,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,sender_password,smtp_address,"
//                + "smtp_port,smtp_authentication,smtp_user_id,smtp_password,ssl_encrypt,receiver_emails,privilege_level "
//                + "from email_alarm,user where email_alarm.user_id=user.id";
//        String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,sender_password,smtp_address,"
//                + "smtp_port,smtp_auth_enabled,smtp_user_uuid,smtp_password,ssl_encrypt_enabled,receiver_emails,role "
//                + "from email_alarm,user where email_alarm.user_id=user.id";
//        TODO此处需要加密查询
//        String smtp_password = ""

        String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,smtp_address,"
                + "smtp_port,smtp_auth_enabled,smtp_user_uuid,smtp_password,ssl_encrypt_enabled,receiver_emails,role "
                + "from email_alarm,user where email_alarm.user_id=user.id ";
        List<Email_alarm> eList = null;
        try {
//            eList = qr.query(sql, new BeanListHandler<Email_alarm>(Email_alarm.class));
            eList = qr.query(sql, new email_alarmHandler());
            for (Email_alarm email_alarm : eList) {
                System.out.println(email_alarm);
            }

            if (eList.size() > 0) {
                logger.info("Get mail config count:" + eList.size());
                for (Email_alarm eAlarm : eList) {
                    if (eAlarm.getEnabled() == (byte) 0) {
                        continue;
                    }
                    MailSender sender = new MailSender(eAlarm);
                    if (eAlarm.getRole() < 2) {
                        this.adminSenderMap.put(eAlarm.getUser_id(), sender);
//                        logger.info("admin用户"+eAlarm.getUser_id());
                    }
                    this.normalSenderMap.put(eAlarm.getUser_id(), sender);
//                       logger.info("normal用户"+eAlarm.getUser_id());

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

    @Override
    public void addAllMailService(List<Email_alarm> l) {
        for (Email_alarm email_alarm : l) {
            MailSender sender = new MailSender(email_alarm);
            if (email_alarm.getRole() < 2) {
                this.adminSenderMap.put(email_alarm.getUser_id(), sender);
            }
            this.normalSenderMap.put(email_alarm.getUser_id(), sender);
        }

    }

    // 更新邮件配置还是使用该接口
    @Override
    public void addMailService(String name) {
        // 通过查数据库，添加到本地，自己构造MailSender对象
        // 初始的时候，先从数据库中获取一次
//        String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,sender_password,smtp_address,"
//                + "smtp_port,smtp_authentication,smtp_user_id,smtp_password,ssl_encrypt,receiver_emails,privilege_level "
//                + "from email_alarm,user where email_alarm.user_id=user.id and email_alarm.user_id=?";
//        logger.info("hello add mail");
        String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,smtp_address,"
                + "smtp_port,smtp_auth_enabled,smtp_user_uuid,smtp_password,ssl_encrypt_enabled,receiver_emails "
                + "from email_alarm,user where email_alarm.user_id=user.id and email_alarm.user_id=?";
        QueryRunner qr = MyDataSource.getQueryRunner();
//        findUserIdByUuid();
        String id = findUserIdByUuid(name);
//        logger.info("name:"+name);
//        logger.info("id:"+id);
//        Object[] para = {name};
        Object[] params = {id};
        List<Email_alarm> elList = null;
        try {
//            elList = qr.query(sql, new BeanListHandler<Email_alarm>(Email_alarm.class), para);
            elList = qr.query(sql, new BeanListHandler<Email_alarm>(Email_alarm.class), params);
            logger.info(elList);
            for (Email_alarm email_alarm : elList) {
                if (email_alarm.getEnabled() == (byte) 0) {
                    if (this.normalSenderMap.containsKey(name)) {
                        this.normalSenderMap.remove(name);
                    }
                    continue;
                }
                MailSender sender = new MailSender(email_alarm);
//                logger.info(email_alarm.getLimit_enabled());
                if (email_alarm.getRole() < 2) {
                    this.adminSenderMap.put(email_alarm.getUser_id(), sender);
                }
                this.normalSenderMap.put(name, sender);
            }
        } catch (SQLException e) {
            logger.error("addMailService.", e);
        } finally {

        }
    }

    @Override
    public void deleteMailService(String name) {
        // 查询数据库，从本地删除
        if (this.normalSenderMap.containsKey(name)) {
            this.normalSenderMap.remove(name);
        }
        if (this.adminSenderMap.containsKey(name)) {
            this.adminSenderMap.remove(name);
        }

    }

    @Override
    public void updateMailService(String name, Email_alarm sender) {

    }


    public String findUserIdByUuid(String name) {
        String sql = "select id from user where uuid = ?";
        QueryRunner qr = MyDataSource.getQueryRunner();
        Object[] params = {name};
        try {
            return qr.query(sql, new UserIdHd(), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected Long findArkIdAndUserIdAndId(Fault fault, String user) {
        QueryRunner qclent = MyDataSource.getQueryRunner();
        String sql = "";
        if (fault.getClient_type() == 1) {
//            sql = "select count(*) from client where user_id=? and data_ark_id=? and id=?";
            sql = "select count(*) from client where user_id=? and data_ark_id=?";
            logger.info("enter type1");
        } else if (fault.getClient_type() == 2) {
//            sql = "select count(*) from vcenter where user_id=? and data_ark_id=? and id=?";
            sql = "select count(*) from vcenter where user_id=? and data_ark_id=?";
            logger.info("enter type2");
        } else if (fault.getClient_type() == 3) {
            // sql = "select count(*) from virtual_machine where user_id=? and data_ark_id=?
            // and id=?";
//            sql = "select count(*) from vcenter_vm inner join vcenter on  vcenter.id= vcenter_vm.vcenter_id and vcenter.user_id=? and vcenter.data_ark_id= ? and vcenter_vm.id=?  ";
            sql = "select count(*) from vcenter_vm inner join vcenter on  vcenter.id= vcenter_vm.vcenter_id and vcenter.user_id=? and vcenter.data_ark_id= ?  ";
            logger.info("enter type3");
        }
//        Object[] param1 = {user, fault.getData_ark_uuid(), fault.getClient_id()};
        Object[] param1 = {user, fault.getData_ark_uuid()};
        try {
            Long count = qclent.query(sql, new ScalarHandler<Long>(), param1);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
        return null;
    }

//    public void updateMailService(String name, Email_alarm sender) {
//        // 同理，查询数据库，更新
//        // this.list.put(name, sender);
//    }

    @Override
    public void notifyCenter(DataArkDTO data_ark, List<Client_> clientList, List<Vcenter> vcList, List<Virtual_machine> vmList, List<Fault> list_fault) throws SQLException {
        String sql = null;
        Object[] condition = null;

        for (Fault fault : list_fault) {
            try {
                logger.info("-----------Userid:" + fault.getUser_uuid() + ",faultType:" + fault.getType() + ",targetName:"
                        + fault.getTarget_name() + ",data_ark ip:" + fault.getData_ark_ip() + ",client_id:"
                        + fault.getClient_id());
                //普通客户端
                if (fault.getClient_type() == 1) {
                    // 1.confirm all alarm log for target.
                    //更新（异常信息不是虚拟机快照点创建失败的，离线建立快照点，VMWARE同步数据失败）告警日志数据方舟id，并且设置未处理，
                    sql = "update alarm_log set processed=1 where data_ark_uuid=? and target_uuid=? and exception!=3 and exception!=25 and exception!=26";

                    condition = new Object[]{fault.getData_ark_uuid(), fault.getClient_id()};

                } else {
                    // add by wxx,for one fault to other fault and not confirm.
                    // current error
                    List<String> currentErrors = new ArrayList<String>();
                    QueryRunner qr = MyDataSource.getQueryRunner();
                    String excepts = "";

                    // 注意这里名称不一致，需要特殊处理
                    if (fault.getClient_type() == StreamerClouddrmanage.ClientType.SINGLE_VALUE) {

                        excepts = data_ark.getExcept();
                    } else if (fault.getClient_type() == StreamerClouddrmanage.ClientType.VMWARE_VALUE) {


                        for (Client_ c : clientList) {

                            if (fault.getData_ark_uuid().equals(c.getData_ark_id()) && fault.getClient_id().equals(c.getUuid())) {
                                excepts = c.getExcept();
                                break;
                            }
                        }
                    } else if (fault.getClient_type() == StreamerClouddrmanage.ClientType.MSCS_VALUE) {
                        /*
                         * sql="select exceptions from vcenter where data_ark_id=? and vcenter_id=?";
                         * excepts=qr.query(sql, new ExceptHandler(), condition);
                         */

                        for (Vcenter vc : vcList) {
//                            logger.info(vc.getExcep());
                            if (fault.getData_ark_uuid().equals(vc.getData_ark_id()) && fault.getClient_id().equals(vc.getUuid())) {
                                excepts = vc.getExcep();
                                break;
                            }
                        }


                    } else if (fault.getClient_type() == StreamerClouddrmanage.ClientType.RAC_VALUE) {
                        for (Virtual_machine vm : vmList) {

                            if (fault.getData_ark_uuid().equals(vm.getData_ark_id())
                                    && fault.getClient_id().equals(vm.getUuid())) {
                                excepts = vm.getExcept();
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

                    sql = "select * from alarm_log where data_ark_uuid=? and binary target_name=? and target_uuid=? and processed=0";
                    condition = new Object[]{fault.getData_ark_uuid(), fault.getTarget_name(), fault.getClient_id()};
                    // db error
                    qr = MyDataSource.getQueryRunner();
                    List<Integer> dbErrors = qr.query(sql, new ColumnListHandler<Integer>("exception"), condition);
                    logger.debug("DB error condition:" + condition[0] + "," + condition[1] + "DB error:"
                            + dbErrors.toString());

                    logger.info("start to compare current and db errors.");
                    for (Integer type : dbErrors) {
                        if (!currentErrors.contains(String.valueOf(type))) {
                            logger.info(fault.getUser_uuid() + "," + fault.getData_ark_ip()
                                    + " current not contains db,confirm it:" + type);
                            // 2.current not contains db,confirm it.
//                            if (type == 3 || type == 25 || type == 26 || type == 31 ) {
                            if (type == 11 || type == 12 || type == 24 || type == 25 || type == 26) {
                                logger.info("VM error don't need to confirm.");
                            } else {
                                // remove user id update TODO
                                // sql="update alarm_log set user_id=?,processed=1 where data_ark_id=? and
                                // target_id=? and exeception=?";
                                sql = "update alarm_log set processed=1 where data_ark_uuid=? and target_uuid=? and exception=?";
                                // condition= new
                                // Object[]{fault.getUser_id(),fault.getData_ark_id(),fault.getClient_id(),type};
                                condition = new Object[]{fault.getData_ark_uuid(), fault.getClient_id(), type};
                            }
                        }
                    }

                    for (String type : currentErrors) {

                        if (!dbErrors.contains(Integer.parseInt(type)) && Integer.parseInt(type) != 0) { // insert error
                            logger.info(fault.getUser_uuid() + "," + fault.getData_ark_ip() + " current is new,insert it:"
                                    + type);
                            // 3.current is new,insert/update it.

                            sql = "insert into alarm_log(timestamp,processed,exception,data_ark_uuid,data_ark_name,data_ark_ip,target_uuid,target_name,last_alarm_timestamp,user_uuid) values(?,?,?,?,?,?,?,?,?,?)";
                            condition = new Object[]{fault.getTimestamp(), 0L, fault.getType(),
                                    fault.getData_ark_uuid(), fault.getData_ark_name(), fault.getData_ark_ip(),
                                    fault.getClient_id(), fault.getTarget_name(), 0L, fault.getUser_uuid()};
                        } else if (dbErrors.contains(Integer.parseInt(type)) && (Integer.parseInt(type) == 11
                                || Integer.parseInt(type) == 12 || Integer.parseInt(type) == 24 || Integer.parseInt(type) == 25 || Integer.parseInt(type) == 26)) {
//                        } else if (dbErrors.contains(Integer.parseInt(type)) && (Integer.parseInt(type) == 3
//                                || Integer.parseInt(type) == 25 || Integer.parseInt(type) == 26)) {
                            // bug#777 ->update time for snapshot error
                            sql = "update alarm_log set timestamp=? where data_ark_uuid=? and target_uuid=? and exception=? and processed=0";
                            condition = new Object[]{fault.getTimestamp(), fault.getData_ark_uuid(),
                                    fault.getClient_id(), type};
                        }
                    }
                }

                QueryRunner qr = MyDataSource.getQueryRunner();
                qr.execute(sql, condition);

                if (fault.getType() != 0) {

                    for (Map.Entry<String, MailSender> entry : this.normalSenderMap.entrySet()) {
                        String user = entry.getKey();
                        MailSender mailSender = entry.getValue();
                        // 判断是否属于管理员用户
                        Email_alarm conf = mailSender.getConfig();
                        if (conf.getRole() == 0 || conf.getRole() == 1) {
                            mailSender.judge(fault, user);
                            logger.info(user + " admin or root user start judge...");
                        } else {
                            sql = "select * from quota where user_id=? and data_ark_id=?";
                            String id = findDataArkUUIdById(fault.getData_ark_uuid());

                            Object[] param = {user, id};
                            QueryRunner qRunner = MyDataSource.getQueryRunner();
                            List<Quota> quotas = qRunner.query(sql, new QuotaHandler(), param);
                            if (!quotas.isEmpty()) {
                                // 包括客户端，VC，虚拟机
                                if (fault.getClient_type().intValue() == 1 || fault.getClient_type().intValue() == 2
                                        || fault.getClient_type().intValue() == 3) {
//                                    // 查询该user_id是否和报警客户端存在关系，即该客户端是否是该用户添加过，添加过则给该用户发送报警邮件
//                                    Long count = findArkIdAndUserIdAndId(fault, user);
//                                    if (count.intValue() > 0) {
                                    mailSender.judge(fault, user);
                                    logger.info(user + " commom user start to judge...");
//                                        logger.info("count>0");
                                }
//                                } else {
//                                    mailSender.judge(fault, user);
//                                    logger.info("count=0");
//                                }
//                                mailSender.judge(fault, user);

                            } else {
                                logger.warn("email_alarm table doesn't have  user_id:" + user + " and data_ark_id:"
                                        + fault.getData_ark_uuid());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(fault.getUser_uuid() + ":" + e);
            }
        }
    }


    private String findDataArkUUIdById(String uuid) {
        QueryRunner q = MyDataSource.getQueryRunner();
        Object[] param = new Object[]{uuid};
        String result = "";
        String sql = "select id from data_ark where uuid=?";

        try {
            result = q.query(sql, new dataIdHandler(), param);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        return result;
    }


    @Override
    public void sentFault(Collection<FaultSimple> faultSimples) {
//        logger.warn(faultSimples);
        for (FaultSimple faultSimple : faultSimples) {
            // send to normal users
            List<Fault> faults = convertFaultSimple(faultSimple);
            for (Fault fault : faults) {
                logger.debug("==========MAIL USER_:" + fault.toString());
                String userId = fault.getUser_uuid();
                String uuid = findUserIdByUuid(userId);
//                MailSender sender = this.normalSenderMap.get(userId);
                MailSender sender = this.normalSenderMap.get(uuid);
                logger.debug("==========MAIL SENDER_:" + normalSenderMap.toString());
                for (Map.Entry<String, MailSender> map : this.normalSenderMap.entrySet()) {
                    if (uuid != null) {
                        if (sender != null) {
                            if (uuid.equals(map.getKey())){
                                sender.judge(fault, userId);
                            }
                        }
                    }
                }
            }
            // send all to admin user

            Set<Map.Entry<String, MailSender>> senderSet = this.adminSenderMap.entrySet();
            logger.debug("==========MAIL SENDER_ADMIN:" + adminSenderMap.toString());

            List<Fault> faultsWithUserIdString = convertFaultSimpleWithUserString(faultSimple);
            for (Map.Entry<String, MailSender> entry : senderSet) {
                MailSender sender = entry.getValue();
                String userId = entry.getKey();
                for (Fault fault : faultsWithUserIdString) {
                    logger.debug("==========MAIL ADMIN_:" + fault.toString());

                    try {
                        sender.judge(fault, userId);
                    } catch (Exception e) {
                        logger.error("send mail to" + userId + " failed", e);
                    }
                }
            }

        }

    }

    List<Fault> convertFaultSimpleWithUserString(FaultSimple faultSimple) {

        Collection<StreamerClouddrmanage.FaultType> faultTypes = faultSimple.getFaultTypes();

        String dataArkId = faultSimple.getDataArkUuid();
        String dataArkIp = faultSimple.getDataArkIp();
        String data_ark_name = faultSimple.getDataArkName();
        String targetId = faultSimple.getTargetUuid();
        String targetName = faultSimple.getTargetName();
        long timestamp = faultSimple.getTimestamp();
        StreamerClouddrmanage.ClientType clientType = faultSimple.getClientType();
//        List<String> userIds = faultSimple.getUserIds();

        List<Fault> faults = new ArrayList<Fault>();
        for (StreamerClouddrmanage.FaultType faultType : faultTypes) {
            Integer code = faultType.getNumber();
            Fault fault = new Fault();
            fault.setType(code);
            fault.setClient_id(targetId);
            fault.setClient_type(clientType.getNumber());
            fault.setData_ark_uuid(dataArkId);
            fault.setData_ark_ip(dataArkIp);
            fault.setData_ark_name(data_ark_name);
            fault.setTarget_name(targetName);
//            fault.setUser_id(StupidStringUtil.parseUserIdListToUserIdsString(userIds));
            fault.setTimestamp(timestamp);
            faults.add(fault);
        }
        return faults;
    }

    //将faultSimple转化成faults
    List<Fault> convertFaultSimple(FaultSimple faultSimple) {

        Collection<StreamerClouddrmanage.FaultType> faultTypes = faultSimple.getFaultTypes();

        String dataArkId = faultSimple.getDataArkUuid();
        String dataArkIp = faultSimple.getDataArkIp();
        String data_ark_name = faultSimple.getDataArkName();
        String targetId = faultSimple.getTargetUuid();
        String targetName = faultSimple.getTargetName();
        StreamerClouddrmanage.ClientType clientType = faultSimple.getClientType();
        List<String> userIds = faultSimple.getUserUuids();
        Long timestamp = faultSimple.getTimestamp();
        List<Fault> faults = new ArrayList<Fault>();
        for (StreamerClouddrmanage.FaultType faultType : faultTypes) {
            for (String userId : userIds) {
                Integer code = faultType.getNumber();
                Fault fault = new Fault();
                fault.setType(code);
                fault.setClient_id(targetId);
                fault.setClient_type(clientType.getNumber());
                fault.setData_ark_uuid(dataArkId);
                fault.setData_ark_ip(dataArkIp);
                fault.setData_ark_name(data_ark_name);
                fault.setTarget_name(targetName);
                fault.setUser_uuid(userId);
                fault.setTimestamp(timestamp);
                faults.add(fault);
            }
        }
        return faults;
    }


}
