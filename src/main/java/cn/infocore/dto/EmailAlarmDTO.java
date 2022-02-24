package cn.infocore.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmailAlarmDTO {
	
    private Long userId;

    //是否开启邮件报警
    private byte enabled;
    
    //已经添加的异常 ; 分隔
    private String exceptions;
    
    //是否启用限制：同一告警事件在 limit_suppress_time
    private byte limitEnabled;
    
    //时间范围内只发送一封邮件
    private Long limitSuppressTime;
    
    //发件人邮箱
    private String senderEmail;
    
    //授权密码
    //smtp服务器地址
    private String smtpAddress;
    
    //smtp端口
    private Integer smtpPort;
	
	//是否启用身份验证
    private byte smtpAuthEnabled;
	
	private String smtpUserUuid;

    private byte[] smtpPassword;
    
    //是否开启SSL
    private byte sslEncryptEnabled;
    
    //收件人
    private String receiverEmails;

    //该发件人的权限等级： 1和0是管理员级别，普通用户是2，见user表
    private int role;
    
}
