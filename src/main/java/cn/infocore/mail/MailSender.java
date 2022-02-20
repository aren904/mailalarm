package cn.infocore.mail;

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

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.dto.Fault;
import cn.infocore.utils.TestAesGcmAe;
import cn.infocore.utils.Utils;

/**
 * 邮件发送类
 */
public class MailSender {
	
    private static final Logger logger = Logger.getLogger(MailSender.class);
    
    private static Map<String, Long> howOfen = new ConcurrentHashMap<>();// 内存维护的发送间隔时间
    
    private static TimedCache<String, String> timedCache = CacheUtil.newTimedCache(10 * 60 * 1000); //缓存时间为10min
    
    private EmailAlarmDTO config;
    
    private MimeMessage message;
    
    private Session s;

    public MailSender(EmailAlarmDTO config) {
        this.config = config;
        final Properties properties = new Properties();
        properties.put("mail.smtp.auth", config.getSmtp_auth_enabled() == (byte) 0 ? "false" : "true");
        //properties.put("mail.debug", "true");
        properties.put("mail.debug", "false");
        properties.put("mail.smtp.host", config.getSmtp_address());
        properties.put("mail.transport.protocol", "smtp");//
        //properties.put("mail.smtp.port", config.getSmtp_port());
        properties.put("mail.smtp.starttls.enable", config.getSsl_encrypt_enabled() == (byte) 0 ? "false" : "true");
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
        try {
            Address from = new InternetAddress(config.getSender_email());
            message.setFrom(from);

            // 设置收件人邮箱,这里是多个收件人
            String[] recv = config.getReceiver_emails().split(";");
            for (String r : recv) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(r));
            }
        } catch (Exception e) {
            logger.error("Failed to create MailSender.",e);
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
    public void judge(Fault fault, Long user) {
    	logger.info("----------UserId:" + user + ",exception:" + config.getExceptions() + ",fault type:" 
        		+ fault.getType() + ",enabled:" + config.getEnabled() + ",targetName:" + fault.getTarget_name() 
        		+ ",timestamp:" + fault.getTimestamp());
    	
        if (config.getEnabled() == 0) {
            logger.info(user + " doesn't need to send email,for config is not enabled.");
            return;
        }
        
        long now = System.currentTimeMillis() / 1000;
        //配置里待报警的内容
        String[] excepts = config.getExceptions().split(";");
        for (String except : excepts) {
            // 如果该用户已经添加这个异常
            if (except.equals(Integer.toString(fault.getType()))) {
                // 是否开启限制同一时间内只发送一封邮件
                String key = user + fault.getData_ark_uuid() + fault.getTarget_name() + fault.getType();
                if (config.getLimit_enabled() == 0) {
                    // 未开启,直接发送异常邮件
                    try {
                        logger.info(user + " not enabled limit,send email:" + fault.getTarget_name() + "," + fault.getType());
                        send(fault);
                    } catch (Exception e1) {
                        logger.error(user + ":" + e1);
                    }
                    howOfen.put(key, now);// 保存一下发送的时间戳
                } else {
                    // 已经开启
                    long split = config.getLimit_suppress_time();
                    logger.info(split);
                    //初始map,未添加过或者指定时间内未添加过
                    if ((howOfen.get(key) == null || howOfen.get(key) + split <= now) && timedCache.get(key, false) == null) {
                        timedCache.put(key, key);
                        try {
                            logger.info(user + " enabled limit,send email:" + fault.getTarget_name() + "," + fault.getType());
                            send(fault);
                        } catch (Exception e1) {
                            logger.error(user + ":" + e1);
                        }
                        howOfen.put(key, now);// 保存一下发送的时间戳
                    }
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

            logger.info("Start sending mail to " + config.getSmtp_user_uuid()+",smtpPassword:"+config.getSmtp_password());
            //密钥
            byte[] key = hexStringToByteArray("cff315f48817496b9f23538c2d83942e");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            String s = TestAesGcmAe.decrypt(config.getSmtp_password(), secretKey, null);
            logger.info("解密后的字段:"+s);

            transport.connect(config.getSmtp_user_uuid(), s);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            logger.info("Mail send is successful.user :" + config.getSmtp_user_uuid());
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
            //message.saveChanges();
            Transport transport = s.getTransport();

            logger.info("Start sending test mail to " + config.getSmtp_user_uuid()+",smtpPassword:"+new String(config.getSmtp_password()));
            transport.connect(config.getSmtp_user_uuid(),new String(config.getSmtp_password()));
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            logger.info("Test Mail send is successful.user :" + config.getSmtp_user_uuid());
            return true;
        } catch (Exception e) {
        	logger.error("Test mail sent failed", e);
            return false;
        }
    }
}
