package cn.infocore.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import cn.infocore.entity.Email_alarm;
import cn.infocore.entity.Fault;
import cn.infocore.utils.Utils;

public class MailSender {
	private static final Logger logger=Logger.getLogger(MailSender.class);
	private static Map<String, Long> howOfen=new ConcurrentHashMap<String, Long>();// 内存维护的发送间隔时间
	private Email_alarm config;
	private MimeMessage message;
	private Session s;

	public MailSender(Email_alarm config) {
		this.config=config;
		final Properties properties = new Properties();
		properties.put("mail.smtp.auth", config.getSmtp_authentication() == (byte)0 ? "false" : "true");//
		properties.put("mail.debug", "true");
		properties.put("mail.smtp.host", config.getSmtp_address());
		properties.put("mail.transport.protocol", "smtp");//
		//properties.put("mail.smtp.port", config.getSmtp_port());
		properties.put("mail.smtp.starttls.enable", config.getSsl_encrypt() == (byte)0 ? "false" : "true");
		/*properties.put("mail.user", config.getSmtp_user_id());
		properties.put("mail.password", config.getStmp_password());
		s = Session.getDefaultInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(properties.getProperty("mail.user"),
						properties.getProperty("mail.password"));
			}
		});*/
		s=Session.getInstance(properties);
		message = new MimeMessage(s);

	}

	public Email_alarm getConfig() {
		return this.config;
	}

	// 逻辑处理
	public void judge(Fault fault) {

		long now = System.currentTimeMillis() / 1000;
		String[] e = config.getExcept().split(";");
		for (String string : e) {
			// 如果该用户已经添加这个异常
			if (string.equals(Integer.toString(fault.getType()))) {
				// 是否开启限制
				String key = fault.getData_ark_id() + fault.getTarget() + fault.getType();
				if (config.getEnabled() == 0) {
					// 未开启,直接发送异常邮件
					send(fault);
					howOfen.put(key, now);// 保存一下发送的时间戳
				} else {
					// 已经开启
					long split = config.getLimit_suppress_time();
					//初始map
					if (howOfen.get(key)==null||howOfen.get(key) + split >= now) {
						send(fault);
						howOfen.put(key, now);// 保存一下发送的时间戳
					}
				}
			}
		}
	}

	/**
	 * 发送邮件
	 */
	public void send(Fault fault) {
		try {
			// 发件人
			Address from = new InternetAddress(config.getSender_email());
			message.setFrom(from);

			// 设置收件人邮箱,这里是多个收件人
			String[] recv = config.getReceiver_emails().split(";");
			for (String r : recv) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(r));
			}
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
			if (fault!=null) {
				builder.append("尊敬的用户,您好:\n");
				builder.append("\t数据方舟统一管理平台发现告警信息:");
				builder.append(fault.getData_ark_name());
				builder.append("(" + fault.getData_ark_ip() + ")"+fault.getTarget()+Utils.getAlarmInformationType(fault.getType())+"\n");
				builder.append("\t告警等级:" + Utils.getAlarmInformationClass(fault.getType())+"\n");
				String time = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(new Date(fault.getTimestamp()));
				builder.append("\t告警时间:" + time+"\n");
				builder.append("\t对应数据方舟:" + fault.getData_ark_name()+"\n");
				builder.append("\t对应告警对象:" + fault.getTarget()+"\n\n");
				builder.append("\t此致\n\t敬礼!\n\n");
			}else {
				builder.append("这是一封来自数据方舟统一管理平台的测试邮件!");
			}
			message.setText(builder.toString());
			message.setSentDate(new Date());
			//message.saveChanges();
			Transport transport = s.getTransport();
			transport.connect(config.getSmtp_user_id(), config.getStmp_password());
			// 发送
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("Mail send successed...");
		} catch (AddressException e) {
			logger.error(e);
		} catch (MessagingException e) {
			logger.error(e);
		}
	}
}
