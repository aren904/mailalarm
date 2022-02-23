package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 数据方舟
 */
@Accessors(chain = true)
@Data
@ToString
@TableName("data_ark")
public class DataArk {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    
    @TableField(value = "name")
    private String name;
    
    @TableField(value = "ip")
    private String ip;

    @TableField(value = "uuid")
    private String uuid;
    
    @TableField(value = "limit_client_count")
    private Integer limitClientCount;

    @TableField(value = "limit_vcenter_vm_count")
    private Integer limitVcenterVmCount;

    @TableField(value = "total_pool_size")
    private Long totalPoolSize;
    
    @TableField(value = "used_pool_size")
    private Long usedPoolSize;
    
    @TableField(value = "total_oracle_space_size")
    private Long totalOracleSpaceSize;

    @TableField(value = "used_oracle_space_size")
    private Long usedOracleSpaceSize;

    @TableField(value = "total_cloud_space_size")
    private Long totalCloudSpaceSize;

    @TableField(value = "used_cloud_space_size")
    private Long usedCloudSpaceSize;
    
    @TableField(value = "used_rds_space_size")
    private Long usedRdsSpaceSize;

    @TableField(value = "used_oss_space_size")
    private Long usedOssSpaceSize;
    
    @TableField(value = "used_ecs_space_size")
    private Long usedEcsSpaceSize;
    
    @TableField(value="used_mdb_space_size")
    private Long usedMdbSpaceSize;
    
    @TableField(value = "user_uuid")
    private String userUuid;
    
    @TableField(value = "user_password")
    private byte[] userPassword;
    
    @TableField(value = "exceptions")
    private String exceptions;

    @TableField(value = "data_ark_group_id")
    private Long dataArkGroupId;
    
    @TableField(value = "snmp_updated_version")
    private Integer snmpUpdatedVersion;

    @TableField(value = "rds_endpoint_updated_version")
    private Integer rdsEndpointUpdatedVersion;
    
    @TableField(value = "oss_endpoint_updated_version")
    private Integer ossEndpointUpdatedVersion;

    @TableField(value = "ecs_endpoint_updated_version")
    private Integer ecsEndpointUpdatedVersion;

}
