package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

//@TableName("ecs_instance")
@Data
public class EcsInstanceDO {
//    @TableField("id")
    Integer id;
//    @TableField("instance_id")
    String instanceId;
//    @TableField("ecs_id")
    Integer ecsId;
//    @TableField("type")
    Integer type;
//    @TableField("name")
    String name;
//    @TableField("operating_system")
    String operateSystem;
//    @TableField("exceptions")
    String exceptions;
//    @TableField("size")
    Long size;
//    @TableField("preoccupation_size")
    Long preoccupationSize;
//    @TableField("is_dr_enabled")
    Integer isDrEnabled;
//    @TableField("dr_size")
    Long drSize;
//    @TableField("preoccupation_dr_size")
    Long preoccupationDrSize;
//    @TableField("data_ark_dr_id")
    Long dataArkDrId;



}
