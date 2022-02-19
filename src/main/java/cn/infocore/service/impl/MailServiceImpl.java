package cn.infocore.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.dto.Fault;
import cn.infocore.dto.FaultDTO;
import cn.infocore.dto.VCenterDTO;
import cn.infocore.dto.VirtualMachineDTO;
import cn.infocore.entity.Quota;
import cn.infocore.entity.User;
import cn.infocore.mail.MailSender;
import cn.infocore.manager.AlarmLogManager;
import cn.infocore.manager.ClientManager;
import cn.infocore.manager.DataArkManager;
import cn.infocore.manager.MailManager;
import cn.infocore.manager.QuotaManager;
import cn.infocore.manager.UserManager;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.MailService;
import cn.infocore.utils.ConvertUtils;

@Service
public class MailServiceImpl implements MailService {
	
    private static final Logger logger = Logger.getLogger(MailServiceImpl.class);
    
    private Map<Long, MailSender> normalSenderMap = null;// 必须线程安全
    
    private Map<Long, MailSender> adminSenderMap = null;
    
    @Autowired
    private MailManager mailManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private AlarmLogManager alarmLogManager;
    
    @Autowired
    private DataArkManager dataArkManager;
    
    @Autowired
    private ClientManager clientManager;
    
    @Autowired
    private QuotaManager quotaManager;
    
    private MailServiceImpl() {
    	logger.info("Init,start to collect mail config from database.");
        MailCenterRestryHolder.instance = this;

        //user_id与MailSender的键值对
        this.normalSenderMap = new ConcurrentHashMap<Long, MailSender>();
        this.adminSenderMap = new ConcurrentHashMap<Long, MailSender>();
        
        List<EmailAlarmDTO> emailAlaramDtos=mailManager.findAllWithUser();
        
        if (emailAlaramDtos.size() > 0) {
            logger.info("Get mail config count:" + emailAlaramDtos.size());
            for (EmailAlarmDTO emailAlaram : emailAlaramDtos) {
                if (emailAlaram.getEnabled() == (byte) 0) {
                    continue;
                }
                MailSender sender = new MailSender(emailAlaram);
                if (emailAlaram.getRole() < 2) {
                    this.adminSenderMap.put(emailAlaram.getUser_id(), sender);
                }
                this.normalSenderMap.put(emailAlaram.getUser_id(), sender);
            }
            logger.info("Collected mail config finished.");
        } else {
            logger.warn("Collected mail config failed.");
        }
    }

    private static class MailCenterRestryHolder {
        public static MailServiceImpl instance = new MailServiceImpl();
    }

    public static MailServiceImpl getInstance() {
        return MailCenterRestryHolder.instance;
    }

    @Override
    public void addAllMailService(List<EmailAlarmDTO> l) {
        for (EmailAlarmDTO email_alarm : l) {
            MailSender sender = new MailSender(email_alarm);
            if (email_alarm.getRole() < 2) {
                this.adminSenderMap.put(email_alarm.getUser_id(), sender);
            }
            this.normalSenderMap.put(email_alarm.getUser_id(), sender);
        }
    }

    /**
     * 添加或更新邮件配置
     */
    @Override
    public void addMailService(String userUuid) {
    	User user=userManager.findUserByUuid(userUuid);
    	if(user!=null) {
    		List<EmailAlarmDTO> emailAlarmDtos=mailManager.findWithUserByUserId(user.getId());
    		
    		for (EmailAlarmDTO email_alarm : emailAlarmDtos) {
    			//对于添加禁用或更新禁用的要删除
                if (email_alarm.getEnabled() == (byte) 0) {
                    if (this.normalSenderMap.containsKey(user.getId())) {
                        this.normalSenderMap.remove(user.getId());
                    }
                    continue;
                }
                
                //对于添加，则收集到数据库
                MailSender sender = new MailSender(email_alarm);
                if (email_alarm.getRole() < 2) {
                    this.adminSenderMap.put(email_alarm.getUser_id(), sender);
                }
                this.normalSenderMap.put(user.getId(), sender);
            }
    	}
    }

    @Override
    public void deleteMailService(Long userId) {
        // 查询数据库，从本地删除
        if (this.normalSenderMap.containsKey(userId)) {
            this.normalSenderMap.remove(userId);
        }
        if (this.adminSenderMap.containsKey(userId)) {
            this.adminSenderMap.remove(userId);
        }
    }

    /**
     * 查询指定用户是否添加过该客户端
     * @param fault
     * @param userId
     * @return
     */
    protected Integer findArkIdAndUserIdAndId(Fault fault, Long dataArkId,Long userId) {
        return clientManager.listCount(userId,dataArkId,fault.getClient_type());
    }

    /**
     * 处理异常
     */
    @Override
    public void notifyCenter(DataArkDTO data_ark, List<ClientDTO> clientList, List<VCenterDTO> vcList, List<VirtualMachineDTO> vmList, 
    		List<Fault> list_fault) throws SQLException {
        String sql = null;
        Object[] condition = null;

        for (Fault fault : list_fault) {
            try {
                logger.info("-----------Userid:" + fault.getUser_uuid() + ",faultType:" + fault.getType() + ",targetName:"
                        + fault.getTarget_name() + ",data_ark ip:" + fault.getData_ark_ip() + ",client_uuid:"
                        + fault.getClient_id()+",ClientType:"+fault.getClient_type());

                if (fault.getType() == StmAlarmManage.ClientType.SINGLE_VALUE) {
                	//更新（异常信息不是虚拟机快照点创建失败的，离线建立快照点，VMWARE同步数据失败），并且设置已确认
                	alarmLogManager.updateConfirm(fault.getData_ark_uuid(), fault.getClient_id());
                } else {
                	// For one fault to other fault and not confirm.
                    List<String> currentErrors = new ArrayList<String>();
                    String excepts = "";

                    // 注意这里名称不一致，需要特殊处理
                    if (fault.getClient_type() == StmAlarmManage.ClientType.SINGLE_VALUE) {
                        excepts = data_ark.getExcept();
                    } else if (fault.getClient_type() == StmAlarmManage.ClientType.VMWARE_VALUE) {
                        for (ClientDTO c : clientList) {
                            if (fault.getData_ark_uuid().equals(c.getData_ark_id()) && fault.getClient_id().equals(c.getUuid())) {
                                excepts = c.getExcept();
                                break;
                            }
                        }
                    } else if (fault.getClient_type() == StmAlarmManage.ClientType.MSCS_VALUE) {
                        for (VCenterDTO vc : vcList) {
                            if (fault.getData_ark_uuid().equals(vc.getData_ark_id()) && fault.getClient_id().equals(vc.getUuid())) {
                                excepts = vc.getException();
                                break;
                            }
                        }
                    } else if (fault.getClient_type() == StmAlarmManage.ClientType.RAC_VALUE) {
                        for (VirtualMachineDTO vm : vmList) {
                            if (fault.getData_ark_uuid().equals(vm.getData_ark_id()) && fault.getClient_id().equals(vm.getUuid())) {
                                excepts = vm.getException();
                                break;
                            }
                        }
                    }

                    if (excepts != "" && excepts != null) {
                        currentErrors.addAll(Arrays.asList(excepts.split(";")));
                    }
                    logger.info("Current error size:" + currentErrors.size() + ",fault type:" + fault.getClient_type()
                            + "," + currentErrors.toString());

                    // not confirm error
                    List<Integer> dbErrors=alarmLogManager.findUnconfirmByDataArkUuidAndTargetUuidAndTargetName(fault.getData_ark_uuid(), 
                    		fault.getClient_id(),fault.getTarget_name());
                    logger.debug("DB error condition:" + condition[0] + "," + condition[1] + "DB error:" + dbErrors.toString());

                    //数据存在而当前不存在的需要确认
                    logger.info("start to compare current and db errors.");
                    for (Integer type : dbErrors) {
                        if (!currentErrors.contains(String.valueOf(type))) {
                            logger.info(fault.getUser_uuid() + "," + fault.getData_ark_ip()
                                    + " current not contains db,confirm it:" + type);
                            //排除一些不需要确认的
                            if (type == 11 || type == 12 || type == 24 || type == 25 || type == 26) {
                                logger.info("The error:"+type+" don't need to confirm.");
                            } else {
                            	alarmLogManager.autoConfirmLog(fault.getData_ark_uuid(), fault.getClient_id(), type);
                            }
                        }
                    }

                    //当前存在但数据库不存在的需要添加
                    for (String type : currentErrors) {
                        if (!dbErrors.contains(Integer.parseInt(type)) && Integer.parseInt(type) != 0) { 
                            logger.info(fault.getUser_uuid() + "," + fault.getData_ark_ip() + " current is new,insert it:"+ type);
                            alarmLogManager.addAlarmlog(fault);
                        } else if (dbErrors.contains(Integer.parseInt(type)) && (Integer.parseInt(type) == 11
                                || Integer.parseInt(type) == 12 || Integer.parseInt(type) == 24 || Integer.parseInt(type) == 25 || Integer.parseInt(type) == 26)) {
                            // bug#777 ->update time for snapshot error
                        	alarmLogManager.updateAlarmlogTimestamp(fault,type);
                        }
                    }
                }

                if (fault.getType() != 0) {
                    for (Map.Entry<Long, MailSender> entry : this.normalSenderMap.entrySet()) {
                        Long userId = entry.getKey();
                        MailSender mailSender = entry.getValue();
                        // 判断是否属于管理员用户
                        EmailAlarmDTO conf = mailSender.getConfig();
                        if (conf.getRole() == 0 || conf.getRole() == 1) {
                            mailSender.judge(fault, userId);
                            logger.info("admin or root user:"+userId+" start judge...");
                        } else {
                            Long dataArkId = dataArkManager.getDataArkByUuid(fault.getData_ark_uuid()).getId();
                            List<Quota> quotas=quotaManager.listByDataArkId(dataArkId);
                            if (!quotas.isEmpty()) {
                                // 包括客户端，VC，虚拟机
                                if (fault.getClient_type().intValue() == 1 || fault.getClient_type().intValue() == 2
                                        || fault.getClient_type().intValue() == 3) {
                                    // 查询该user_id是否和报警客户端存在关系，即该客户端是否是该用户添加过，添加过则给该用户发送报警邮件
                                	Integer count = findArkIdAndUserIdAndId(fault, dataArkId,userId);
                                    if (count.intValue() > 0) {
                                        mailSender.judge(fault, userId);
                                    }
                                } else {
                                    mailSender.judge(fault, userId);
                                }
                                logger.info("commom user:"+userId+" start to judge...");
                            } else {
                                logger.warn("email_alarm table has not user_id:" + userId + " and data_ark_id:"
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

    @Override
    public void sendFault(List<FaultDTO> faultDtos) {
        for (FaultDTO faultDto : faultDtos) {
            // send to normal users
            List<Fault> faults = ConvertUtils.convertFault(faultDto);
            
            for (Fault fault : faults) {
                logger.debug("==========MAIL USER_:" + fault.toString());
                
                String userUuid = fault.getUser_uuid();
                Long userId = userManager.findUserByUuid(userUuid).getId();
                
                MailSender sender = this.normalSenderMap.get(userId);
                logger.debug("==========MAIL SENDER_:" + normalSenderMap.toString());
                for (Map.Entry<Long, MailSender> map : this.normalSenderMap.entrySet()) {
                    if (userId!=null && sender != null) {
                        if (userId.equals(map.getKey())){
                            sender.judge(fault, userId);
                        }
                    }
                }
            }
            
            // send all to admin user
            Set<Map.Entry<Long, MailSender>> senderSet = this.adminSenderMap.entrySet();
            logger.debug("==========MAIL SENDER_ADMIN:" + adminSenderMap.toString());

            for (Map.Entry<Long, MailSender> entry : senderSet) {
                MailSender sender = entry.getValue();
                Long userId = entry.getKey();
                for (Fault fault : faults) {
                    logger.debug("==========MAIL ADMIN_:" + fault.toString());
                    try {
                        sender.judge(fault, userId);
                    } catch (Exception e) {
                        logger.error("Send mail to" + userId + " failed", e);
                    }
                }
            }
        }

    }

}
