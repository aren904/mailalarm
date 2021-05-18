package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
@Data
@TableName("mdb")
public class MdbDO {
    @TableField("id")
    Integer id;
    @TableField("uuid")
    String uuid;
    @TableField("user_id")
    String userId;
    @TableField("data_ark_id")
    String dataArkId;
    @TableField("type")
    Integer type;
    @TableField("name")
    String name;
    @TableField("is_dr")
    Integer isDr;
    @TableField("client_group_id")
    String clientGroupId;
    @TableField("ips")
    String ips;
    @TableField("operating_system")
    String operateSystem;
    @TableField("exceptions")
    String exceptions;

}
