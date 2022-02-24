package cn.infocore.utils;

import static cn.infocore.utils.TestAesGcmAe.hexStringToByteArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.dto.Fault;

/**
 * 邮件发送类
 */
public class MailSender {
	
    private static final Logger logger = Logger.getLogger(MailSender.class);
    
    //内存维护的键值对<某异常唯一标识，告警时间>
    private static Map<String, Long> howOfen = new ConcurrentHashMap<>();
    
    //缓存时间为10min
    //private static TimedCache<String, String> timedCache = CacheUtil.newTimedCache(10 * 60 * 1000); 
    
    private EmailAlarmDTO config;
    
    private MimeMessage message;
    
    private Session s;

    public MailSender(EmailAlarmDTO config) throws Exception {
    	this.config = config;
    	logger.debug("MailSender config:"+config.toString());
    	
        final Properties properties = new Properties();
        properties.put("mail.smtp.auth", config.getSmtpAuthEnabled() == (byte) 0 ? "false" : "true");
        //properties.put("mail.debug", "true");
        properties.put("mail.debug", "false");
        properties.put("mail.smtp.host", config.getSmtpAddress());
        properties.put("mail.transport.protocol", "smtp");//
        //properties.put("mail.smtp.port", config.getSmtp_port());
        properties.put("mail.smtp.starttls.enable", config.getSslEncryptEnabled() == (byte) 0 ? "false" : "true");
		/*properties.put("mail.user", config.getSmtp_user_id());
		properties.put("mail.password", config.getStmp_password());
		s = Session.getDefaultInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(properties.getProperty("mail.user"),
						properties.getProperty("mail.password"));
			}
		});*/
        s = Session.getInstance(properties);
        message = new MimeMessage(s);
        
        Address from = new InternetAddress(config.getSenderEmail());
        message.setFrom(from);

        // 设置收件人邮箱,这里是多个收件人
        String[] recv = config.getReceiverEmails().split(";");
        for (String r : recv) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(r));
        }
    }

    public EmailAlarmDTO getConfig() {
        return this.config;
    }

    /**
     * 邮件报警
     * @param fault
     * @param user
     */
    public void judge(Fault fault, Long userId) {
    	logger.info("----------UserId:" + userId + ",exception:" + config.getExceptions() + ",fault type:" 
        		+ fault.getType() + ",enabled:" + config.getEnabled() + ",target:" + fault.getTarget_name()+"|"+fault.getTarget_uuid()
        		+ ",timestamp:" + fault.getTimestamp()+",limitSuppressTime:"+config.getLimitSuppressTime());
    	
    	//未启用过滤
        if (config.getEnabled() == 0) {
            logger.info(userId + " doesn't need to send email,for config is not enabled.");
            return;
        }
        
        long now = System.currentTimeMillis() / 1000;
        //配置里待报警的范围
        String[] excepts = config.getExceptions().split(";");
        for (String except : excepts) {
            // 如果该范围包含当前异常
            if (Integer.parseInt(except)==fault.getType()) {
                // 是否开启限制同一时间内只发送一封邮件
                String key = userId + fault.getData_ark_uuid() + fault.getTarget_name() + fault.getType();
                
                // 未开启,默认一分钟
                long split = config.getLimitEnabled() == 0?60:config.getLimitSuppressTime();
                if ((howOfen.get(key) == null || howOfen.get(key) + split <= now)) {
            		try {
                        logger.info(userId + " send email:" + fault.getTarget_name() + "," + fault.getType()+",per split:"+split);
                        send(fault);
                    } catch (Exception e) {
                        logger.error(userId + " filed to send email.",e);
                    }
                    howOfen.put(key, now);// 保存一下发送的时间戳
            	}
            }
        }
    }

    /**
     * 发送邮件
     */
    public boolean send(Fault fault) throws Exception {
        try {
            // 发件人
            // 邮件标题
            /*
             * 格式 主题:云容灾管理平台告警信息
             * 尊敬的用户，您好:
             * 		数据方舟统一管理平台发现告警信息:xxxxxxxxx
             * 		告警等级:
             * 		告警时间:
             * 		对应数据方舟:
             * 		对应告警对象:
             *
             * 		此致 敬礼!
             */
            StringBuilder builder = new StringBuilder();
            message.setSubject("数据方舟统一管理平台告警信息","utf-8");
            if (fault != null) {
                builder.append("尊敬的用户,您好:\n");
                builder.append("\t数据方舟统一管理平台发现告警信息:");
                builder.append(fault.getData_ark_name());
                builder.append("(" + fault.getData_ark_ip() + ")" + fault.getTarget_name() + " " + Utils.getAlarmInformationType(fault.getType()) + "\n");
                builder.append("\t告警等级:" + Utils.getAlarmInformationClass(fault.getType()) + "\n");
                String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(fault.getTimestamp() * 1000);
                builder.append("\t告警时间:" + time + "\n");
                builder.append("\t对应数据方舟:" + fault.getData_ark_name() + "\n");
                builder.append("\t对应告警对象:" + fault.getTarget_name() + "\n");
            } else {
                builder.append("这是一封来自数据方舟统一管理平台的测试邮件!");
            }
            message.setText(builder.toString(), "utf8");
            message.setSentDate(new Date());

            Transport transport = s.getTransport();

            logger.info("Start sending mail to " + config.getSmtpUserUuid()+",smtpPassword:"+config.getSmtpPassword());
            //密钥
            byte[] key = hexStringToByteArray("cff315f48817496b9f23538c2d83942e");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            String s = TestAesGcmAe.decrypt(config.getSmtpPassword(), secretKey, null);
            logger.info("解密后的字段:"+s);

            transport.connect(config.getSmtpUserUuid(), s);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            logger.info("Mail send is successful.user :" + config.getSmtpUserUuid());
            return true;
        } catch (Exception e) {
        	logger.error("mail sent failed", e);
            return false;
        }
    }

    //测试邮件不加密码
    public boolean sendTest(Fault fault) throws Exception {
        try {
            // 发件人
            // 邮件标题
            /*
             * 格式 主题:云容灾管理平台告警信息
             * 尊敬的用户，您好:
             * 		数据方舟统一管理平台发现告警信息:xxxxxxxxx
             * 		告警等级:
             * 		告警时间:
             * 		对应数据方舟:
             * 		对应告警对象:
             *
             * 		此致 敬礼!
             */
            StringBuilder builder = new StringBuilder();
            message.setSubject("数据方舟统一管理平台告警信息");
            if (fault != null) {
                builder.append("尊敬的用户,您好:\n");
                builder.append("\t数据方舟统一管理平台发现告警信息:");
                builder.append(fault.getData_ark_name());
                builder.append("(" + fault.getData_ark_ip() + ")" + fault.getTarget_name() + " " + Utils.getAlarmInformationType(fault.getType()) + "\n");
                builder.append("\t告警等级:" + Utils.getAlarmInformationClass(fault.getType()) + "\n");
                String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(fault.getTimestamp() * 1000);
                builder.append("\t告警时间:" + time + "\n");
                builder.append("\t对应数据方舟:" + fault.getData_ark_name() + "\n");
                builder.append("\t对应告警对象:" + fault.getTarget_name() + "\n");
            } else {
                builder.append("这是一封来自数据方舟统一管理平台的测试邮件!");
            }
            message.setText(builder.toString(), "utf8");
            message.setSentDate(new Date());
            Transport transport = s.getTransport();

            logger.info("Start sending test mail to " + config.getSmtpUserUuid()+",smtpPassword:"+new String(config.getSmtpPassword()));
            transport.connect(config.getSmtpUserUuid(),new String(config.getSmtpPassword()));
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            logger.info("Test Mail send is successful.user :" + config.getSmtpUserUuid());
            return true;
        } catch (Exception e) {
        	logger.error("Test mail sent failed", e);
            return false;
        }
    }
}
