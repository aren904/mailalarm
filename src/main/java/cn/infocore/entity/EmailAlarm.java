package cn.infocore.entity;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("email_alarm")
public class EmailAlarm {
	
	@TableField(value = "user_id")
    private long user_id;

    //是否开启邮件报警
	@TableField(value = "enabled")
    private byte enabled;
    
    //已经添加的异常 ; 分隔
	@TableField(value = "exceptions")
    private String exceptions;
    
    //是否启用限制：同一告警事件在 limit_suppress_time
	@TableField(value = "limit_enabled")
    private byte limit_enabled;
    
    //时间范围内只发送一封邮件
	@TableField(value = "limit_suppress_time")
    private long limit_suppress_time;
    
    //发件人邮箱
	@TableField(value = "sender_email")
    private String sender_email;
    
    //授权密码
    //smtp服务器地址
	@TableField(value = "smtp_address")
    private String smtp_address;
    
    //smtp端口
	@TableField(value = "smtp_port")
    private int smtp_port;
	
	//是否启用身份验证
	@TableField(value = "smtp_auth_enabled")
    private byte smtp_auth_enabled;
	
	@TableField(value = "smtp_user_uuid")
	private String smtp_user_uuid;

	@TableField(value = "smtp_password")
    private byte[] smtp_password;
    
    //是否开启SSL
	@TableField(value = "ssl_encrypt_enabled")
    private byte ssl_encrypt_enabled;
    
    //收件人
	@TableField(value = "receiver_emails")
    private String receiver_emails;

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
                ", smtp_auth_enabled=" + smtp_auth_enabled +
                ", smtp_user_uuid='" + smtp_user_uuid + '\'' +
                ", smtp_password=" + Arrays.toString(smtp_password) +
                '}';
    }
}
