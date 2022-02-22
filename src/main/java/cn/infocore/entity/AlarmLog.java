package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 实体类：告警日志
 */
@Data
@TableName("alarm_log")
public class AlarmLog {

    @TableId(value = "id",type = IdType.AUTO) //自增主键
    private Long id;

    @TableField("timestamp")
    private Long timestamp;
    
    @TableField("processed")
    private Integer processed;
    
    @TableField("exception")
    private Integer exception;
    
    @TableField("data_ark_uuid")
    private String dataArkUuid;
    
    @TableField("data_ark_name")
    private String dataArkName;
    
    @TableField("data_ark_ip")
    private String dataArkIp;
    
    @TableField("target_uuid")
    private String targetUuid;
    
    @TableField("target_name")
    private String targetName;
    
    @TableField("last_alarm_timestamp")
    private Long lastAlarmTimestamp;
    
    @TableField("user_uuid")
    private String userUuid;
    
}
