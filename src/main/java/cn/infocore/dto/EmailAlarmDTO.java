package cn.infocore.dto;

import java.util.Arrays;

import lombok.Data;

@Data
public class EmailAlarmDTO {
	
    private long user_id;

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
	
	//是否启用身份验证
    private byte smtp_auth_enabled;
	
	private String smtp_user_uuid;

    private byte[] smtp_password;
    
    //是否开启SSL
    private byte ssl_encrypt_enabled;
    
    //收件人
    private String receiver_emails;

    //该发件人的权限等级： 1和0是管理员级别，普通用户是2，见user表
    private int role;

    @Override
    public String toString() {
        return "Email_alarm{" +
                "user_id='" + user_id + '\'' +
                ", enabled=" + enabled +
                ", exceptions='" + exceptions + '\'' +
                ", limit_enabled=" + limit_enabled +
                ", limit_suppress_time=" + limit_suppress_time +
                ", sender_email='" + sender_email + '\'' +
                ", smtp_address='" + smtp_address + '\'' +
                ", smtp_port=" + smtp_port +
                ", ssl_encrypt_enabled=" + ssl_encrypt_enabled +
                ", receiver_emails='" + receiver_emails + '\'' +
                ", role=" + role +
                ", smtp_auth_enabled=" + smtp_auth_enabled +
                ", smtp_user_uuid='" + smtp_user_uuid + '\'' +
                ", smtp_password=" + Arrays.toString(smtp_password) +
                '}';
    }
}
