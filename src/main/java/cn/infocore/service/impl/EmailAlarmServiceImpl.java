package cn.infocore.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.dto.Fault;
import cn.infocore.dto.FaultDTO;
import cn.infocore.dto.FaultEnum;
import cn.infocore.dto.VCenterDTO;
import cn.infocore.dto.VirtualMachineDTO;
import cn.infocore.entity.Quota;
import cn.infocore.entity.User;
import cn.infocore.main.EmailAlarmListCache;
import cn.infocore.manager.AlarmLogManager;
import cn.infocore.manager.ClientManager;
import cn.infocore.manager.DataArkManager;
import cn.infocore.manager.EmailAlarmManager;
import cn.infocore.manager.QuotaManager;
import cn.infocore.manager.UserManager;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.utils.ConvertUtils;
import cn.infocore.utils.MailSender;

@Service
public class EmailAlarmServiceImpl implements EmailAlarmService {
	
    private static final Logger logger = Logger.getLogger(EmailAlarmServiceImpl.class);
    
    @Autowired
    private EmailAlarmManager mailManager;
    
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
    
    @Override
	public List<EmailAlarmDTO> findAllWithUser() {
		return mailManager.findAllWithUser();
	}

    @Override
    public void addAllEmailAlarm(List<EmailAlarmDTO> emailAlarmDTos) {
        EmailAlarmListCache.getInstance(mailManager).addAllEmailAlarm(emailAlarmDTos);
    }

    /**
     * 为指定用户添加或更新邮件配置：一个用户有0个或者1个配置
     */
    @Override
    public void addEmailAlarm(String userUuid) {
    	User user=userManager.findUserByUuid(userUuid);
    	if(user!=null) {
    		EmailAlarmDTO emailAlarmDto=mailManager.findByUserId(user.getId());
    		EmailAlarmListCache.getInstance(mailManager).addEmailAlarm(user.getId(), emailAlarmDto);
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
        for (Fault fault : list_fault) {
            try {
                logger.info("-----------Userid:" + fault.getUser_uuid() + ",faultType:" + fault.getType() + ",target info:"
                        + fault.getTarget_name()+"|"+fault.getTarget_uuid() + ",data_ark ip:" + fault.getData_ark_ip() + ",client_uuid:"
                        + fault.getClient_id()+",ClientType:"+fault.getClient_type());

                if (fault.getType() == FaultEnum.NORMAL.getCode()) {
                	//当前异常类型是NORMAL，即现在正常，则自动确认历史异常（除异常信息不是虚拟机快照点创建失败的，离线建立快照点，VMWARE同步数据失败不能自动确认）
                	//???以上三种类型是文档里定义的不需要确认
                	alarmLogManager.updateConfirm(fault.getData_ark_uuid(), fault.getClient_id());
                } else {
                	//当前为异常，不是NORMAL
                	//excepts是由Faults转换而来
                    List<String> currentErrors = new ArrayList<String>();
                    String excepts = "";

                    // 找到目标对象的心跳异常
                    if (fault.getClient_type() == 0) {
                    	//数据方舟
                        excepts = data_ark.getExcept();
                    } else if (fault.getClient_type() == 1) {
                    	//客户端
                        for (ClientDTO c : clientList) {
                            if (fault.getData_ark_uuid().equals(c.getData_ark_id()) && fault.getClient_id().equals(c.getUuid())) {
                                excepts = c.getExcept();
                                break;
                            }
                        }
                    } else if (fault.getClient_type() == 2) {
                    	//VC
                        for (VCenterDTO vc : vcList) {
                            if (fault.getData_ark_uuid().equals(vc.getData_ark_id()) && fault.getClient_id().equals(vc.getUuid())) {
                                excepts = vc.getException();
                                break;
                            }
                        }
                    } else if (fault.getClient_type() == 3) {
                    	//VM
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
                    logger.info("DataArkId:"+fault.getData_ark_uuid()+",target:" +fault.getTarget_uuid()+"|"+fault.getTarget_name()+"|"+fault.getClient_type()
                    	+",Current error:" + currentErrors.toString());

                    // 获取目标对象的未确认异常
                    List<Integer> dbErrors=alarmLogManager.findUnconfirmByDataArkUuidAndTargetUuidAndTargetName(fault.getData_ark_uuid(), 
                    		fault.getClient_id(),fault.getTarget_name());
                    logger.info("DataArkId:"+fault.getData_ark_uuid()+",target:" +fault.getTarget_uuid()+"|"+fault.getTarget_name()+"|"+fault.getClient_type()
                		+",DB error:" + dbErrors.toString());

                    //数据库存在而当前不存在的需要确认
                    logger.info("Start to compare current and db errors...");
                    for (Integer type : dbErrors) {
                        if (!currentErrors.contains(String.valueOf(type))) {
                            logger.debug(fault.getUser_uuid() + "," + fault.getData_ark_ip()
                                    + " current not contains db,confirm it:" + type);
                            //???以下类型是文档里定义的不需要确认
                            if (type == 11 || type == 12 || type == 24 || type == 25 || type == 26) {
                                logger.info("The error:"+type+" don't need to confirm.");
                            } else {
                            	alarmLogManager.autoConfirmLog(fault.getData_ark_uuid(), fault.getClient_id(), type);
                            }
                        }
                    }

                    //当前存在但数据库不存在的需要添加
                    for (String currentError : currentErrors) {
                    	Integer type=Integer.parseInt(currentError);
                        if (!dbErrors.contains(type) && type != 0) { 
                            logger.info(fault.getUser_uuid() + "," + fault.getData_ark_ip() +","+fault.getTarget_uuid()+ " current is new,insert it:"+ type);
                            alarmLogManager.addAlarmlog(fault);
                        } else if (dbErrors.contains(type) && (type == 11
                                || type == 12 || type == 24 || type == 25 || type == 26)) {
                            // bug#777 ->update time for snapshot error
                        	// 更新未确认的异常时间
                        	alarmLogManager.updateAlarmlogTimestamp(fault,type);
                        }
                    }
                }

                if (fault.getType() != 0) {
                	//normalSenderMap包含所有邮件用户配置
                	Map<Long, MailSender> normalSenderMap=EmailAlarmListCache.getInstance(mailManager).getNormalSenderMap();
                	
                    for (Map.Entry<Long, MailSender> entry : normalSenderMap.entrySet()) {
                        Long userId = entry.getKey();
                        MailSender mailSender = entry.getValue();
                        
                        // 判断是否属于管理员用户
                        EmailAlarmDTO conf = mailSender.getConfig();
                        if (conf.getRole() == 0 || conf.getRole() == 1) {
                            mailSender.judge(fault, userId);
                            logger.info("admin or root user:"+userId+" start judge...");
                        } else {
                            Long dataArkId = dataArkManager.getDataArkByUuid(fault.getData_ark_uuid()).getId();
                            //获取数据方舟拥有的配额信息：一个数据方舟可能被多个用户拥有，因此可能存在多种配额
                            List<Quota> quotas=quotaManager.listByDataArkId(dataArkId);
                            if (!quotas.isEmpty()) {
                                if (fault.getClient_type().intValue() == 1 || fault.getClient_type().intValue() == 2
                                        || fault.getClient_type().intValue() == 3) {
                                    // 针对客户端，VC，虚拟机：查询该对象是否是该用户添加过，添加过则给该用户发送报警邮件
                                	Integer count = findArkIdAndUserIdAndId(fault, dataArkId,userId);
                                    if (count.intValue() > 0) {
                                        mailSender.judge(fault, userId);
                                    }
                                } else {
                                	// 针对数据方舟直接告警
                                    mailSender.judge(fault, userId);
                                }
                                logger.info("Commom user:"+userId+" start to judge...");
                            } else {
                                logger.warn("Email_alarm table has not user_id:" + userId + " and data_ark_id:"
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

    /**
     * 解析心跳异常：需要邮件告警的发送告警
     */
    @Override
    public void sendFaults(List<FaultDTO> faultDtos) {
        for (FaultDTO faultDto : faultDtos) {
            // 所有对象将DTO对象解析为异常集合
            List<Fault> faults = ConvertUtils.convertFaultWithUsers(faultDto);
        	Map<Long, MailSender> normalSenderMap=EmailAlarmListCache.getInstance(mailManager).getNormalSenderMap();
            logger.debug("Mail sender User:" + normalSenderMap.toString());
            
            for (Fault fault : faults) {
                logger.debug("Send mail user faults:" + fault.toString());
                String userUuid = fault.getUser_uuid();
                Long userId = userManager.findUserByUuid(userUuid).getId();
                
                //获取缓存的用户邮件配置键值对里符合要求的对象，触发告警
                MailSender sender = normalSenderMap.get(userId);
                for (Map.Entry<Long, MailSender> map : normalSenderMap.entrySet()) {
                    if (userId!=null && sender != null) {
                        if (userId.equals(map.getKey())){
                            try {
								sender.judge(fault, userId);
							} catch (Exception e) {
								logger.error("Failed to send nirmal mail to" + userId + " failed", e);
							}
                        }
                    }
                }
            }
            
            // 管理员都对象
            /*Set<Map.Entry<Long, MailSender>> senderSet = this.adminSenderMap.entrySet();
            logger.debug("==========Mail sender admin:" + adminSenderMap.toString());
            List<Fault> adminFaults = ConvertUtils.convertFault(faultDto);
            for (Map.Entry<Long, MailSender> entry : senderSet) {
                MailSender sender = entry.getValue();
                Long userId = entry.getKey();
                for (Fault fault : adminFaults) {
                	logger.debug("Send mail admin faults:" + fault.toString());
                    try {
                        sender.judge(fault, userId);
                    } catch (Exception e) {
                        logger.error("Failed to send admin mail to" + userId + " failed", e);
                    }
                }
            }*/
        }

    }

}
