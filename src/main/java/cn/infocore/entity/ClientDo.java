package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@TableName(value = "client")
@Data
public class ClientDo {

    @TableField("id")
    private Integer id;
    @TableField("uuid")
    private String uuId;
    @TableField("user_id")
    private String userId;
    @TableField("data_ark_id")
    private String dataArkId;
    @TableField("is_dr")
    private Integer isDr;
    @TableField("type")
    private Integer type;
    @TableField("name")
    private String name;
    @TableField("exceptions")
    private String exceptions;
    @TableField("ips")
    private String ips;
    @TableField("client_group_id")
    private Integer clientGroupId;
    @TableField("operating_system")
    private String operationSystem;
    @TableField("ak")
    private String ak;
    @TableField("sk")
    private String sk;
    @TableField("oss_ak")
    private String ossAk;
    @TableField("oss_sk")
    private String ossSk;
    @TableField("oss_bucket")
    private String ossBucket;
//    @TableField("vcenter_user_uuid")
//    private String VcenterUserUuid;
//    @TableField("vcenter_user_password")
//    private String VcenterUserPassword;

}
