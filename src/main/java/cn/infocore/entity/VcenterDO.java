package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@TableName("vcenter")
public class VcenterDO {
    
    @TableField("id")
    Integer id;
    @TableField("vcenter_id")
    String vcenterId;
    @TableField("user_id")
    String userId;
    @TableField("data_ark_id")
    String dataArkId;
    @TableField("type")
    Integer type;
    @TableField("name")
    String name;
    @TableField("ips")
    String ips;
    @TableField("vcenter_user_id")
    String vCenterUserId;
    @TableField("vcenter_user_password")
    String vCenterPassword;
    @TableField("exceptions")
    String exceptions;

}
