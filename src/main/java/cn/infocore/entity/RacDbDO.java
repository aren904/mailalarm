package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@TableName("rac_db")
public class RacDbDO {

    @TableField(value = "id")
    Integer id;
    @TableField(value = "db_id")
    String dbId;
    @TableField(value = "rac_id")
    String racId;
    @TableField(value = "size")
    Long size;
    @TableField(value = "is_dr_enabled")
    Integer isDrEnabled;
    @TableField(value = "dr_size")
    Long drSize;
    @TableField(value = "data_ark_dr_id")
    Integer dataArkDrId;

}
