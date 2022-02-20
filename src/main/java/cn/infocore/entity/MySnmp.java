package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * 网络管理协议
 */
@Data
public class MySnmp {
	
	@TableId(value = "id")
	private Long id;
	
	@TableField(value = "enabled")
	private int enabled;//0表示禁用
	
	@TableField(value = "station_name")
    private String station_name;
	
	@TableField(value = "station_ip")
    private String station_ip; //管理站IP
    
	@TableField(value = "station_port")
    private int station_port; //维护端口
    
	@TableField(value = "version")
    private int version; //snmp通信版本 0:V1,1:V2c,2:V3
    
	@TableField(value = "read_comm_name")
    private String read_comm_name;//读团体名
    
	@TableField(value = "write_comm_name")
    private String write_comm_name;//写团体名
    
	@TableField(value = "timeout_ms")
    private long timeout_ms;//超时时间
	
	@TableField(value = "security_username")
    private String security_username;
	
	 //Todo 下个版本需要将snmp.auth_password这些改成byte[]类型 加密字段需要用TestAesGcmAe去解密
	@TableField(value = "auth_protocol")
    private int auth_protocol;
    
	@TableField(value = "auth_password_enabled")
    private int auth_password_enabled;
    
	@TableField(value = "auth_password")
    private String auth_password;
    
	@TableField(value = "privacy_protocol")
    private int privacy_protocol;
    
    @TableField(value = "privacy_password_enabled")
    private int privacy_password_enabled;
    
    @TableField(value = "privacy_password")
    private String privacy_password;
    
    @TableField(value = "station_port")
    private int update_version;//最后一次更新版本

}
