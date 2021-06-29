package cn.infocore.entity;

public class Email_alarm {
	//自增的id
//	private int id;



	private String user_id;

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
	//smtp服务器地址
	private String smtp_address;
	//smtp端口
	private int smtp_port;
	//是否开启SSL
	private byte ssl_encrypt_enabled;
	//收件人
	private String receiver_emails;
	
	//该发件人的权限等级： 1和0是管理员级别，普通用户是2，见user表
//	private int privilege_level;
	private int role;

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	//是否启用身份验证
	private byte smtp_auth_enadled;
	
	//smtp user
	private String smtp_user_uuid;
	
	//smtp password
	private String smtp_password;


	public byte getEnabled() {
		return enabled;
	}

	public void setEnabled(byte enabled) {
		this.enabled = enabled;
	}

	public String getExceptions() {
		return exceptions;
	}

	public void setExceptions(String exceptions) {
		this.exceptions = exceptions;
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

	public byte getSsl_encrypt_enabled() {
		return ssl_encrypt_enabled;
	}

	public void setSsl_encrypt_enabled(byte ssl_encrypt_enabled) {
		this.ssl_encrypt_enabled = ssl_encrypt_enabled;
	}

	public String getReceiver_emails() {
		return receiver_emails;
	}

	public void setReceiver_emails(String receiver_emails) {
		this.receiver_emails = receiver_emails;
	}

	public byte getSmtp_auth_enadled() {
		return smtp_auth_enadled;
	}

	public void setSmtp_auth_enadled(byte smtp_auth_enadled) {
		this.smtp_auth_enadled = smtp_auth_enadled;
	}

	public String getSmtp_user_uuid() {
		return smtp_user_uuid;
	}

	public void setSmtp_user_uuid(String smtp_user_uuid) {
		this.smtp_user_uuid = smtp_user_uuid;
	}

	public String getSmtp_password() {
		return smtp_password;
	}

	public void setSmtp_password(String smtp_password) {
		this.smtp_password = smtp_password;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
