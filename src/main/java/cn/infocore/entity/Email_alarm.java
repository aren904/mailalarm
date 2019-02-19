package cn.infocore.entity;

public class Email_alarm {
	//自增的id
	private int id;
	//是否开启邮件报警
	private byte enabled;
	//已经添加的异常 ; 分隔
	private String exceptions;
	//是否启用限制：同一告警事件在 limit_suppress_time  
	private byte limit_enabled;
	//时间范围内只发送一封邮件
	private long limit_suppress_time;
	//发件人邮箱
	private String sender_email;
	//授权密码
	private String sender_password;
	//smtp服务器地址
	private String smtp_address;
	//smtp端口
	private int smtp_port;
	//是否开启SSL
	private byte ssl_encrypt;
	//收件人
	private String receiver_emails;
	
	//是否启用身份验证
	private byte smtp_authentication;
	
	//smtp user
	private String smtp_user_id;
	
	//smtp password
	private String smtp_password;
	
	
	//对应User的id字段，是外健
	private String user_id;


	public String getExceptions() {
		return exceptions;
	}


	public void setExceptions(String exceptions) {
		this.exceptions = exceptions;
	}


	public String getSmtp_password() {
		return smtp_password;
	}


	public void setSmtp_password(String smtp_password) {
		this.smtp_password = smtp_password;
	}


	public byte getSmtp_authentication() {
		return smtp_authentication;
	}


	public void setSmtp_authentication(byte smtp_authentication) {
		this.smtp_authentication = smtp_authentication;
	}


	public String getSmtp_user_id() {
		return smtp_user_id;
	}


	public void setSmtp_user_id(String smtp_user_id) {
		this.smtp_user_id = smtp_user_id;
	}


	


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public byte getEnabled() {
		return enabled;
	}


	public void setEnabled(byte enabled) {
		this.enabled = enabled;
	}


	


	public byte getLimit_enabled() {
		return limit_enabled;
	}


	public void setLimit_enabled(byte limit_enabled) {
		this.limit_enabled = limit_enabled;
	}


	public long getLimit_suppress_time() {
		return limit_suppress_time;
	}


	public void setLimit_suppress_time(long limit_suppress_time) {
		this.limit_suppress_time = limit_suppress_time;
	}


	public String getSender_email() {
		return sender_email;
	}


	public void setSender_email(String sender_email) {
		this.sender_email = sender_email;
	}


	public String getSender_password() {
		return sender_password;
	}


	public void setSender_password(String sender_password) {
		this.sender_password = sender_password;
	}


	public String getSmtp_address() {
		return smtp_address;
	}


	public void setSmtp_address(String smtp_address) {
		this.smtp_address = smtp_address;
	}


	public int getSmtp_port() {
		return smtp_port;
	}


	public void setSmtp_port(int smtp_port) {
		this.smtp_port = smtp_port;
	}


	public byte getSsl_encrypt() {
		return ssl_encrypt;
	}


	public void setSsl_encrypt(byte ssl_encrypt) {
		this.ssl_encrypt = ssl_encrypt;
	}


	public String getReceiver_emails() {
		return receiver_emails;
	}


	public void setReceiver_emails(String receiver_emails) {
		this.receiver_emails = receiver_emails;
	}


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	
	
	
	
	
	
}
