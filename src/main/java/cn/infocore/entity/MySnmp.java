package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 邮件传输协议
 */
@Accessors(chain = true)
@Data
@ToString
@TableName("snmp")
public class MySnmp {
	
	@TableId(value = "id")
	private Long id;
	
	@TableField(value = "enabled")
	private byte enabled;//0表示禁用
	
	@TableField(value = "station_name")
    private String stationName;
	
	@TableField(value = "station_ip")
    private String stationIp; //管理站IP
    
	@TableField(value = "station_port")
    private short stationPort; //维护端口
    
	@TableField(value = "version")
    private Integer version; //snmp通信版本 0:V1,1:V2c,2:V3，目前只有1
    
	@TableField(value = "read_comm_name")
    private String readCommName;//读团体名
    
	@TableField(value = "write_comm_name")
    private String writeCommName;//写团体名
    
	@TableField(value = "timeout_ms")
    private Long timeoutMs;//超时时间
	
	@TableField(value = "security_username")
    private String securityUsername;
	
	 //Todo 下个版本需要将snmp.auth_password这些改成byte[]类型 加密字段需要用TestAesGcmAe去解密
	@TableField(value = "auth_protocol")
    private int authProtocol;
    
	@TableField(value = "auth_password_enabled")
    private byte authpasswordEnabled;
    
	@TableField(value = "auth_password")
    private byte[] authPassword;
    
	@TableField(value = "privacy_protocol")
    private int privacyProtocol;
    
    @TableField(value = "privacy_password_enabled")
    private byte privacyPasswordEnabled;
    
    @TableField(value = "privacy_password")
    private byte[] privacyPassword;
    
    @TableField(value = "updated_version")
    private int updatedVersion;//最后一次更新版本

}
