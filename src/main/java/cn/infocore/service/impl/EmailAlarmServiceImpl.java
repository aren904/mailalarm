package cn.infocore.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import cn.infocore.manager.AlarmLogManager;
import cn.infocore.manager.ClientManager;
import cn.infocore.manager.DataArkManager;
import cn.infocore.manager.EmailAlarmManager;
import cn.infocore.manager.QuotaManager;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.utils.MailSender;

@Service
public class EmailAlarmServiceImpl implements EmailAlarmService {
	
    private static final Logger logger = Logger.getLogger(EmailAlarmServiceImpl.class);
    
    @Autowired
    private EmailAlarmManager mailManager;
    
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
            	//对于数据方舟Data_ark就是target就是client，对于client，target就是client
                logger.info("-----------Useruuid:" + fault.getUser_uuid() + ",data_ark ip:" + fault.getData_ark_ip() + ",targetInfo:" 
                		+ fault.getTarget_name()+"|"+fault.getTarget_uuid()  + ",client:"+fault.getClient_id()+"|"+fault.getClient_type()
                		+ ",faultType:" + fault.getType());

                if (fault.getType() == FaultEnum.NORMAL.getCode()) {
                	//当前异常类型是NORMAL，即现在正常，则自动确认历史异常（除异常信息不是虚拟机快照点创建失败的，离线建立快照点，VMWARE同步数据失败不能自动确认）
                	//???以上三种类型是文档里定义的不需要确认
                	alarmLogManager.updateConfirm(fault.getData_ark_uuid(), fault.getClient_id(),fault.getUser_uuid());
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
                    logger.info("Useruuid:" + fault.getUser_uuid() + ",data_ark ip:" + fault.getData_ark_ip() +",targetInfo:" 
                    		+fault.getTarget_uuid()+"|"+fault.getTarget_name()+",Current error:" + currentErrors.toString());

                    // 获取指定用户的目标对象的未确认异常
                    List<Integer> dbErrors=alarmLogManager.findUnconfirmByDataArkUuidAndTargetUuidAndTargetNameAndUserUuid(fault.getData_ark_uuid(), 
                    		fault.getClient_id(),fault.getTarget_name(),fault.getUser_uuid());
                    logger.info("Useruuid:" + fault.getUser_uuid() + ",data_ark ip:" + fault.getData_ark_ip() +",targetInfo:" 
                    		+fault.getTarget_uuid()+"|"+fault.getTarget_name()+",DB error:" + dbErrors.toString());

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
                            	alarmLogManager.autoConfirmLog(fault.getData_ark_uuid(), fault.getClient_id(), fault.getUser_uuid(),type);
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
                	//查找当前异常对应的用户的邮件配置
                	EmailAlarmDTO emailAlarmDTO=mailManager.findByUserId(fault.getUser_id());
                	logger.info(fault.getUser_uuid() + "," + fault.getData_ark_ip() +","+fault.getTarget_uuid()
                		+ " to send email for fault:"+fault.getType()+", emailAlarmDTO userId:"+emailAlarmDTO.getUserId()+"|"+emailAlarmDTO.getEnabled());
                    
                	if (emailAlarmDTO==null||emailAlarmDTO.getEnabled() == (byte) 0) {
                		//未启用则不需要邮件告警
                		logger.debug(fault.getUser_uuid()+" Email_alarm is not enabled.");
                	}else {
                		MailSender mailSender = new MailSender(emailAlarmDTO);
                		Long userId=fault.getUser_id();
                		
                		// 判断是否属于管理员用户，管理员配置则直接触发告警邮件，普通用户需要判断是否拥有该异常
                        EmailAlarmDTO conf = mailSender.getConfig();
                        if (conf.getRole() == 0 || conf.getRole() == 1) {
                            mailSender.judge(fault, userId);
                            logger.info("admin or root user:"+userId+" start judge...");
                        } else {
                            Long dataArkId = dataArkManager.getDataArkByUuid(fault.getData_ark_uuid()).getId();
                            //获取数据方舟拥有的配额信息：一个数据方舟可能被多个用户拥有，因此可能存在多种配额
                            List<Quota> quotas=quotaManager.listByDataArkId(dataArkId);
                            if (!quotas.isEmpty()) {
                                if (fault.getClient_type() == 1 || fault.getClient_type() == 2
                                        || fault.getClient_type() == 3) {
                                    // 针对客户端，VC，虚拟机：查询该对象是否是该用户添加过，添加过则给该用户发送报警邮件
                                	Integer count = findArkIdAndUserIdAndId(fault, dataArkId,userId);
                                    if (count.intValue() > 0) {
                                        mailSender.judge(fault, userId);
                                    }else {
                                    	logger.debug("UserId:"+userId+",dataArkId:"+dataArkId+" does not has the fault:"+fault);
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
     * 解析心跳异常(ecs之类的客户端)：需要邮件告警的发送告警
     */
    @Override
    public void sendFaults(List<FaultDTO> faultDtos) {
        for (FaultDTO faultDto : faultDtos) {
            // 所有对象将DTO对象解析为异常集合
            List<Fault> faults = mailManager.convertFaultWithUsers(faultDto);
            
            for (Fault fault : faults) {
            	EmailAlarmDTO emailAlarmDTO=mailManager.findByUserId(fault.getUser_id());
            	logger.info("Other "+fault.getUser_uuid() + "," + fault.getData_ark_ip() +","+fault.getTarget_uuid()
            		+ " to send email for fault:"+fault.getType()+", emailAlarmDTO userId:"+emailAlarmDTO.getUserId()+"|"+emailAlarmDTO.getEnabled());
                
            	if (emailAlarmDTO==null||emailAlarmDTO.getEnabled() == (byte) 0) {
            		//未启用则不需要邮件告警
            		logger.debug(fault.getUser_uuid()+" Email_alarm is not enabled.");
            	}else {
            		logger.debug("Send mail user faults:" + fault.toString());
            		
            		Long userId=fault.getUser_id();
            		try {
            			MailSender mailSender = new MailSender(emailAlarmDTO);
                    	mailSender.judge(fault, userId);
					} catch (Exception e) {
						logger.error("Failed to send nirmal mail to" + userId + " failed", e);
					}
            	}
            }
        }

    }

}
