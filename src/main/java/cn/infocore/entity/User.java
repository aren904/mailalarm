package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.entity
 * @ClassName: User
 * @Author: aren904
 * @Description:
 * @Date: 2021/6/9 14:39
 * @Version: 1.0
 */
@TableName("user")
@Data
public class User {
    @TableField(value = "id")
    private  String id;
    @TableField(value = "uuid")
    private String uuid;
    @TableField(value = "role")
    private int role;
    @TableField(value = "name")
    private String name;
    @TableField(value = "password")
    private String password;
    @TableField(value = "phone")
    private String phone;
    @TableField(value = "email")
    private String email;
    @TableField(value = "note")
    private String note;
    @TableField(value = "last_success_timestamp")
    private int lastSuccessTimestamp;
    @TableField(value = "last_success_ip")
    private String lastSuccessIp;
    @TableField(value = "user_group_id")
    private int userGroupId;
}
