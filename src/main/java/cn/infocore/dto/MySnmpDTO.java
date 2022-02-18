package cn.infocore.dto;

import lombok.Data;

@Data
public class MySnmpDTO {
	
    private String station_ip; //管理站IP
    
    private int station_port; //维护端口
    
    private int version; //snmp通信版本 0:V1,1:V2c,2:V3
    
    private String station_name;
    
    private String read_comm_name;//读团体名
    
    private String write_comm_name;//写团体名
    
    private long timeout_ms;//超时时间
    
    private int update_version;//最后一次更新版本
    
    private int enabled;//0表示禁用
    
    private String security_username;
    
    //Todo 下个版本需要将snmp.auth_password这些改成byte[]类型 加密字段需要用TestAesGcmAe去解密
    private int authentication_protocol;
    
    private int authentication_password_enabled;
    
    private String authentication_password;
    
    private int privacy_protocol;
    
    private int privacy_password_enabled;
    
    private String privacy_password;

}
