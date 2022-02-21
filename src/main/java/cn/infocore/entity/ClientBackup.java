package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 备份关系/对象
 */
@Accessors(chain = true)
@Data
@ToString
@TableName("client_backup")
public class ClientBackup {
	
    @TableField(value = "id")
    private Integer id;
    
    @TableField(value = "uuid")
    private String uuid;
    
    @TableField(value = "client_id")
    private Integer ClientId;
    
    @TableField(value = "data_ark_dr_id")
    private Integer dataArkDrId;
    
    @TableField(value = "type")
    private Integer type;
    
    @TableField(value = "exceptions")
    private String exceptions;
    
    @TableField(value = "size")
    private Long size;
    
    @TableField(value = "preoccupation_size")
    private Long preoccupationSize;
    
    @TableField(value = "name")
    private String name;
    
    @TableField(value = "db_type")
    private String dbType;
    
    @TableField(value = "operating_system")
    private String operatingSystem;
    
    @TableField(value="path")
    private String path;
    
    @TableField(value = "database_type")
    private String databaseType;
}
