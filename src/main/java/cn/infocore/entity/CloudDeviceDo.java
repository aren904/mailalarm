package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.entity
 * @ClassName: CloudDeviceDo
 * @Author: aren904
 * @Description:
 * @Date: 2021/5/11 11:30
 * @Version: 1.0
 */
@Accessors(chain = true)
@Data
@TableName("cloud_client_device")
public class CloudDeviceDo {
    @TableField(value = "id")
    Integer id;
    @TableField(value = "uuid")
    String uuid;
    @TableField(value = "cloud_client_id")
    Integer cloudClientId;
    @TableField(value = "data_ark_dr_id")
    Integer dataArkDrId;
    @TableField(value = "type")
    Integer type;
    @TableField(value = "exceptions")
    String exceptions;
    @TableField(value = "size")
    Long size;
    @TableField(value = "preoccupation_size")
    Integer preoccupationSize;
    @TableField(value = "name")
    String name;
    @TableField(value = "db_type")
    String dbType;
    @TableField(value = "operating_system")
    String operatingSystem;

}
