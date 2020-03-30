package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@TableName("alarm_log")
public class AlarmLogDO {

    @TableField("id")
    Long id;

    @TableField("timestamp")
    Long timestamp;
    
    @TableField("processed")
    Integer processed;
    
    @TableField("exception")
    Integer exception;
    
    @TableField("data_ark_id")
    String dataArkId;
    
    @TableField("data_ark_name")
    String dataArkName;
    
    @TableField("data_ark_ip")
    String dataArkIp;
    
    @TableField("target_id")
    String targetId;
    
    @TableField("target")
    String target;
    
    @TableField("last_alarm_timestamp")
    Long lastAlarmTimestamp;
    
    @TableField("user_id")
    String userId;
    
}
