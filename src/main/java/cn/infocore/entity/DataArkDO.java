package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;
@Accessors(chain = true)
@Data
@TableName("data_ark")
public class DataArkDO {

    @TableField(value = "id")
    String id;
    
    @TableField(value = "name")
    String name;
    
    @TableField(value = "ip")
    String ip;
    
    @TableField(value = "limit_client_count")
    Integer limitClientCount;
    
    @TableField(value = "total_pool_size")
    Long totalPoolSize;
    
    @TableField(value = "used_pool_size")
    Long usedPoolSize;
    
    @TableField(value = "total_oracle_space_size")
    Long totalOracleSpaceSize;

    @TableField(value = "used_oracle_space_size")
    Long usedOracleSpaceSize;

    
    @TableField(value = "total_cloud_space_size")
    Long totalCloudSpaceSize;

    @TableField(value = "used_cloud_space_size")
    Long usedCloudSpaceSize;
    
    @TableField(value = "used_rds_space_size")
    Long usedRdsSpaceSize;

    @TableField(value = "used_oss_space_size")
    Long usedOssSpaceSize;
    
    @TableField(value = "used_ecs_space_size")
    Long usedEcsSpaceSize;
    
    @TableField(value = "user_id")
    String userId;
    
    @TableField(value = "user_password")
    String userPassword;
    
    @TableField(value = "exceptions")
    String exceptions;

    
    @TableField(value = "data_ark_group_id")
    Integer dataArkGroupId;
    
    @TableField(value = "snmp_updated_version")
    Integer snmpUpdatedVersion;

    @TableField(value = "rds_endpoint_updated_version")
    Integer rdsEndpointUpdatedVersion;
    
    @TableField(value = "oss_endpoint_updated_version")
    Integer ossEndpointUpdatedVersion;

}
