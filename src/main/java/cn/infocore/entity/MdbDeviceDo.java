package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.entity
 * @ClassName: MdbDeviceDo
 * @Author: aren904
 * @Description:
 * @Date: 2021/5/11 13:48
 * @Version: 1.0
 */
@TableName("mdb_device")
@Data
public class MdbDeviceDo {
    @TableField("id")
    Integer id;
    @TableField("uuid")
    String uuid;
    @TableField("mdb_id")
    Integer mdbId;
    @TableField("type")
    Integer type;
    @TableField("name")
    String name;
    @TableField("exceptions")
    String exceptions;
    @TableField("size")
    Long size;
    @TableField("preoccupation_size")
    Long preoccupationSize;
    @TableField("is_dr_enabled")
    Integer isDrEnabled;
    @TableField("data_ark_dr_id")
    Long dataArkDrId;


//    @TableField("backup_id")
//    String backupId;
//    @TableField("preoccupation_dr_size")
//    Long preoccupationDrSize;
}
