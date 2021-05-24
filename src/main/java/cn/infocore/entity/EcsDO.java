package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

//@TableName("ecs")
@Data
public class EcsDO {
//    @TableField("id")
    Integer id;
//    @TableField("ecs_id")
    String ecsId;
//    @TableField("user_id")
    String userId;
//    @TableField("data_ark_id")
    String dataArkId;
//    @TableField("type")
    Integer type;
//    @TableField("name")
    String name;
//    @TableField("exceptions")
    String exceptions;
//    @TableField("ak")
    String ak;
//    @TableField("sk")
    String sk;
//    @TableField("oss_ak")
    String ossAk;
//    @TableField("oss_sk")
    String ossSk;
//    @TableField("oss_bucket")
    String ossBucket;


}
