package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 客户端：类型很多
 */
@Accessors(chain = true)
@TableName(value = "client")
@ToString
@Data
public class Client {
	
    @TableId(value = "id")
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
    
    /**
     *  SINGLE = 0;  // 普通客户端
	    VMWARE = 1; // vm 
	    MSCS = 2; // windows集群
	    RAC = 3;   // oracle 客户端
	    VC = 4;     // vcent
	    AIX = 5;    // 手动创建的客户端
	    FileSingle = 11; // 文件备份客户端
	    Rds = 12; // rds 客户端
	    RdsInstance = 13; // rds 实例
	    Oss = 14; // oss 客户端
	    OssObjectSet = 15; // oss 备份
	    Ecs = 16; // ecs 客户端
	    EcsInstance = 17; // ecs 实例
	    MetaDB = 18; // meta 客户端  // 元数据库？
	    MetaDBBackup = 19; // meta 客户端备份
	    StorageClient = 20; // 存储客户端
     */
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
    private byte[] sk;
    
    @TableField("oss_ak")
    private String ossAk;
    
    @TableField("oss_sk")
    private byte[] ossSk;
    
    @TableField("oss_bucket")
    private String ossBucket;
    
}
