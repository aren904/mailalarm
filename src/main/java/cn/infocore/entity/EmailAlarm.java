package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 邮件报警管理配置：一个用户一个
 */
@Accessors(chain = true)
@Data
@TableName("email_alarm")
@ToString
public class EmailAlarm {
	
	@TableId(value = "user_id")
    private Long userId;

    //是否开启邮件报警
	@TableField(value = "enabled")
    private byte enabled;
    
    //已经添加的异常 ; 分隔
	@TableField(value = "exceptions")
    private String exceptions;
    
    //是否启用限制：同一告警事件在 limit_suppress_time
	@TableField(value = "limit_enabled")
    private byte limitEnabled;
    
    //时间范围内只发送一封邮件
	@TableField(value = "limit_suppress_time")
    private Long limitSuppressTime;
    
    //发件人邮箱
	@TableField(value = "sender_email")
    private String senderEmail;
    
    //授权密码
    //smtp服务器地址
	@TableField(value = "smtp_address")
    private String smtpAddress;
    
    //smtp端口
	@TableField(value = "smtp_port")
    private Integer smtpPort;
	
	//是否启用身份验证
	@TableField(value = "smtp_auth_enabled")
    private byte smtpAuthEnabled;
	
	@TableField(value = "smtp_user_uuid")
	private String smtpUserUuid;

	@TableField(value = "smtp_password")
    private byte[] smtpPassword;
    
    //是否开启SSL
	@TableField(value = "ssl_encrypt_enabled")
    private byte sslEncryptEnabled;
    
    //收件人
	@TableField(value = "receiver_emails")
    private String receiverEmails;

}
