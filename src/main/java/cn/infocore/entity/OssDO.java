package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
//@TableName("oss")
public class OssDO {

//    @TableField(value = "id")
    Integer id;

//    @TableField(value = "oss_id")
    String ossId;

//    @TableField(value = "user_id")
    String userId;

//    @TableField(value = "data_ark_id")
    String dataArkId;

//    @TableField(value = "type")
    Integer type;

//    @TableField(value = "name")
    String name;

//    @TableField(value = "exceptions")
    String exceptions;

//    @TableField(value = "ak")
    String ak;

//    @TableField(value = "sk")
    String sk;

}
