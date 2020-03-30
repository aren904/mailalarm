package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@TableName("vcenter_vm")
public class VirtualMachineDO {

    @TableField("id")
    String id;
    @TableField("id")
    Integer vCenterId;
    @TableField("type")
    Integer type;
    @TableField("name")
    String name;
    @TableField("path")
    String path;
    @TableField("exceptions")
    String exceptions;
    @TableField("operating_system")
    String operatingSystem;

}
