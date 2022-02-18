package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 客户端：包括有代理和无代理
 */
@Accessors(chain = true)
@TableName(value = "client")
@Data
public class Client {
	
    @TableField("id")
    private Long id;
    
    @TableField("uuid")
    private String uuId;
    
    @TableField("user_id")
    private Long userId;
    
    //对应Data_ark中的id字段，是外健
    @TableField("data_ark_id")
    private Long dataArkId;
    
    @TableField("is_dr")
    private Integer isDr;
    
    //客户端类型 SINGLE = 0;VMWARE = 1;MSCS = 2;RAC = 3;VC = 4; AIX=5;
    @TableField("type")
    private Integer type;
    
    @TableField("exceptions")
    private String exceptions;
    
    @TableField("client_group_id")
    private Long clientGroupId;
    
    @TableField("name")
    private String name;
    
    @TableField("ips")
    private String ips;
    
    @TableField("operating_system")
    private String operationSystem;
    
    @TableField("ak")
    private String ak;
    
    @TableField("sk")
    private String sk;
    
    @TableField("oss_ak")
    private String ossAk;
    
    @TableField("oss_sk")
    private String ossSk;
    
    @TableField("oss_bucket")
    private String ossBucket;
    
}
