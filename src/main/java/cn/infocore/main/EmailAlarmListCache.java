package cn.infocore.main;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.manager.EmailAlarmManager;
import cn.infocore.utils.MailSender;

/**
 * 内存中维护的数据方舟的列表<userId,MailSender>
 */
public class EmailAlarmListCache {

    private static final Logger logger = Logger.getLogger(EmailAlarmListCache.class);
    
    //user_id与MailSender的键值对
	private static Map<Long, MailSender> normalSenderMap=null;// 必须线程安全
    
    private static Map<Long, MailSender> adminSenderMap=null;
    
    private static volatile EmailAlarmListCache instance = null;
	
	private EmailAlarmListCache() {}
	
	public static EmailAlarmListCache getInstance(EmailAlarmManager mailManager) {
		if (instance==null) {
			synchronized (EmailAlarmListCache.class) {
                if (instance == null) {
                    instance = new EmailAlarmListCache();
                    
                    normalSenderMap = new ConcurrentHashMap<Long, MailSender>();
        	        adminSenderMap = new ConcurrentHashMap<Long, MailSender>();
        			
        			logger.info("Init,Start get all emailalarm from database.");
        			logger.info("mailManager:"+mailManager);
        			List<EmailAlarmDTO> emailAlaramDtos=mailManager.findAllWithUser();
        	        
        	        if (emailAlaramDtos.size() > 0) {
        	            logger.info("Get mail config count:" + emailAlaramDtos.size());
        	            for (EmailAlarmDTO emailAlaram : emailAlaramDtos) {
        	                if (emailAlaram.getEnabled() == (byte) 0) {
        	                    continue;
        	                }
        	                MailSender sender = new MailSender(emailAlaram);
        	                if (emailAlaram.getRole() < 2) {
        	                    adminSenderMap.put(emailAlaram.getUser_id(), sender);
        	                }
        	                normalSenderMap.put(emailAlaram.getUser_id(), sender);
        	            }
        	            logger.info("Collected mail config finished.");
        	        } else {
        	            logger.warn("Collected mail config failed.");
        	        }
                }
            }
		}
		return instance;
	}

	//添加
	public synchronized void addEmailAlarm(Long userId,EmailAlarmDTO emailAlarmDto) {
		if (emailAlarmDto.getEnabled() == (byte) 0) {
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
        	//对于添加，则收集到数据库
            MailSender sender = new MailSender(emailAlarmDto);
            if (emailAlarmDto.getRole() < 2) {
                adminSenderMap.put(emailAlarmDto.getUser_id(), sender);
            }
            normalSenderMap.put(userId, sender);
        }
        logger.info("add or update mail config for user:"+emailAlarmDto.getUser_id()+" successfully! "+
        		"current normalSenderMap size:"+normalSenderMap.size()+",adminSenderMap size:"+adminSenderMap.size());
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
	
	public synchronized void addAllEmailAlarm(List<EmailAlarmDTO> emailAlarmDTos) {
        for (EmailAlarmDTO emailAlarmDTo : emailAlarmDTos) {
            MailSender sender = new MailSender(emailAlarmDTo);
            if (emailAlarmDTo.getRole() < 2) {
                adminSenderMap.put(emailAlarmDTo.getUser_id(), sender);
            }
            normalSenderMap.put(emailAlarmDTo.getUser_id(), sender);
        }
    }

}
