package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
//@TableName("oss_object_set")
public class OssObjectSetDO {

//    @TableField(value = "id")
    Integer id;

//    @TableField(value = "object_set_id")
    String objectSetId;
//    String Uuid;

//    @TableField(value = "oss_id")
    Integer ossId;

//    @TableField(value = "type")
    Integer type;

//    @TableField(value = "name")
    String name;

//    @TableField(value = "exceptions")
    String exceptions;

//    @TableField(value = "size")
    Long size;

//    @TableField(value = "preoccupation_size")
    Long preoccupationSize;

//    @TableField(value = "is_dr_enabled")
    Integer drEnabled;

//    @TableField(value = "dr_size")
    Long drSize;

//    @TableField(value = "preoccupation_dr_size")
    Long preoccupationDrSize;

//    @TableField(value = "data_ark_dr_id")
    Long dataArkDrId;


}
