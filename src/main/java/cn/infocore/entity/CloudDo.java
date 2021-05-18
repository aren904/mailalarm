package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.entity
 * @ClassName: CloudDo
 * @Author: aren904
 * @Description:
 * @Date: 2021/5/10 20:13
 * @Version: 1.0
 */
@TableName("cloud_client")
@Data
public class CloudDo {
        @TableField("id")
        Integer id;
        @TableField("uuid")
        String uuId;
        @TableField("user_id")
        String userId;
        @TableField("data_ark_id")
        String dataArkId;
        @TableField("is_dr")
        Integer isDr;
        @TableField("type")
        Integer type;
        @TableField("name")
        String name;
        @TableField("exceptions")
        String exceptions;
        @TableField("client_group_id")
        Integer clientGroupId;
        @TableField("ak")
        String ak;
        @TableField("sk")
        String sk;
        @TableField("oss_ak")
        String ossAk;
        @TableField("oss_sk")
        String ossSk;
        @TableField("oss_bucket")
        String ossBucket;
    }


