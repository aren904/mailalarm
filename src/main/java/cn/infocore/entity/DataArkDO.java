package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("data_ark")
public class DataArkDO {

//    @TableId
    @TableId(value = "id",type = IdType.AUTO)
    String id;
    
    @TableField(value = "name")
    String name;
    
    @TableField(value = "ip")
    String ip;

    @TableField(value = "uuid")
    String uuid;

    
    @TableField(value = "limit_client_count")
    Integer limitClientCount;

    @TableField(value = "limit_vcenter_vm_count")
    Integer limitVcenterVmCount;

//    @TableField(value = "is_zombie")
//    Boolean isZombie;
    
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
    
    @TableField(value="used_mdb_space_size")
    Long usedMdbSpaceSize;
    
    @TableField(value = "user_uuid")
    String userUuid;
    
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

    @TableField(value = "ecs_endpoint_updated_version")
    Integer ecsEndpointUpdatedVersion;
}
