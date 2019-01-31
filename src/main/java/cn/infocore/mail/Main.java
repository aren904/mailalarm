package cn.infocore.mail;


import cn.infocore.entity.Email_alarm;

//简单测试邮件发送
public class Main {

	public static void main(String[] args) {
		Email_alarm email_alarm=new Email_alarm();
		email_alarm.setId(0);
		email_alarm.setEnabled((byte)0);
		email_alarm.setExcept("1;2;3");
		email_alarm.setLimit_enabled((byte)0);
		email_alarm.setSender_email("924590757@qq.com");
		//email_alarm.setSender_password("zcuwybymdmqkbbia");
		email_alarm.setSmtp_address("smtp.qq.com");
		email_alarm.setSmtp_port(465);
		email_alarm.setSsl_encrypt((byte)1);
		email_alarm.setReceiver_emails("ke.zheng@infocore.cn;zhengke19931015@163.com");
		email_alarm.setSmtp_authentication((byte)1);
		email_alarm.setSmtp_user_id("924590757@qq.com");
		email_alarm.setStmp_password("zcuwrbymdmqkbbia");
		MailSender sender=new MailSender(email_alarm);
		/*Fault fault=new Fault();
		fault.setTimestamp(System.currentTimeMillis()/1000);
		fault.setType(FaultType.CLIENT_OFFLINE);
		fault.setData_ark_id(UUID.randomUUID().toString());
		fault.setData_ark_name("数据方舟-test");
		fault.setData_ark_ip("192.168.11.31");
		fault.setTarget("Client--test");*/
		
		sender.send(null);
		System.out.println("发送成功。。。。");
		
	}
}
