package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName("mdb_backup")
@Data
public class MdbBackupDO {

    @TableField("id")
    Integer id;
    @TableField("backup_id")
    String backupId;
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
    @TableField("dr_size")
    Long drSize;
    @TableField("preoccupation_dr_size")
    Long preoccupationDrSize;
    @TableField("data_ark_dr_id")
    Long dataArkDrId;

}
