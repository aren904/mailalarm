package cn.infocore.main;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.utils.MailSender;

/**
 * 内存中维护的数据方舟的列表<userId,MailSender>
 */
public class EmailAlarmListCache {

    private static final Logger logger = Logger.getLogger(EmailAlarmListCache.class);
    
    //user_id与MailSender的键值对
	private static Map<Long, MailSender> normalSenderMap=null;// 必须线程安全
    
    private static Map<Long, MailSender> adminSenderMap=null; //暂未用到，后续考虑去掉
    
    private static volatile EmailAlarmListCache instance = null;
	
	private EmailAlarmListCache() {}
	
	public static EmailAlarmListCache getInstance(EmailAlarmService emailAlarmService) {
		if (instance==null) {
			synchronized (EmailAlarmListCache.class) {
                if (instance == null) {
                    instance = new EmailAlarmListCache();
                    
                    normalSenderMap = new ConcurrentHashMap<Long, MailSender>();
        	        adminSenderMap = new ConcurrentHashMap<Long, MailSender>();
        			
        			logger.info("-----------Init,Start get all emailalarm from database.");
        			List<EmailAlarmDTO> emailAlaramDtos=emailAlarmService.findAllWithUser();
        	        
        	        if (emailAlaramDtos.size() > 0) {
        	            logger.info("Get mail config count:" + emailAlaramDtos.size());
        	            for (EmailAlarmDTO emailAlaram : emailAlaramDtos) {
        	                if (emailAlaram.getEnabled() == (byte) 0) {
        	                    continue;
        	                }
        	                
							try {
								MailSender sender = new MailSender(emailAlaram);
								if (emailAlaram.getRole() < 2) {
	        	                    adminSenderMap.put(emailAlaram.getUserId(), sender);
	        	                }
	        	                normalSenderMap.put(emailAlaram.getUserId(), sender);
							} catch (Exception e) {
								logger.error("Failed to collect mail config:"+emailAlaram.getUserId(),e);
							}
        	            }
        	            logger.info("Collected mail config finished,normalSenderMap count:"+normalSenderMap.size());
        	        } else {
        	            logger.warn("Collected mail config failed.");
        	        }
                }
            }
		}
		return instance;
	}

	/**
	 * 添加/更新邮件配置缓存
	 * @param userId
	 * @param emailAlarmDto
	 */
	public synchronized void addEmailAlarm(Long userId,EmailAlarmDTO emailAlarmDto) {
		if (emailAlarmDto.getEnabled() == (byte) 0) {
			logger.debug("start to remove email config:"+emailAlarmDto.getUserId());
        	//对于禁用的要删除
			if (emailAlarmDto.getRole() < 2) {
                if (adminSenderMap.containsKey(userId)) {
                    adminSenderMap.remove(userId);
                }
            }
            if (normalSenderMap.containsKey(userId)) {
                normalSenderMap.remove(userId);
            }
        }else {
        	//启用的需要添加或更新
			try {
				MailSender sender = new MailSender(emailAlarmDto);
				if (emailAlarmDto.getRole() < 2) {
	                adminSenderMap.put(emailAlarmDto.getUserId(), sender);
	            }
	            normalSenderMap.put(userId, sender);
			} catch (Exception e) {
				logger.error("Failed to add mail config:"+emailAlarmDto.getUserId(),e);
			}
        }
        logger.info("add or update mail config for user:"+emailAlarmDto.getUserId()+" successfully! "+
        		"current normalSenderMap size:"+normalSenderMap.size());
	}

	//移除
	public synchronized void removeEmailAlarm(Long userId) {
		if (normalSenderMap.containsKey(userId)) {
            normalSenderMap.remove(userId);
        }
        if (adminSenderMap.containsKey(userId)) {
            adminSenderMap.remove(userId);
        }
	}

	//获取所有
	public synchronized Map<Long, MailSender> getNormalSenderMap() {
		return normalSenderMap;
	}
	
	public synchronized Map<Long, MailSender> getAdminSenderMap() {
		return adminSenderMap;
	}
	
}
