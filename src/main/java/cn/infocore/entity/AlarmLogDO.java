package cn.infocore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;
//方便链式编程，set返回对象
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
    
    @TableField("data_ark_uuid")
    String dataArkUuid;
    
    @TableField("data_ark_name")
    String dataArkName;
    
    @TableField("data_ark_ip")
    String dataArkIp;
    
    @TableField("target_uuid")
    String targetUuid;
    
    @TableField("target_name")
    String targetName;
    
    @TableField("last_alarm_timestamp")
    Long lastAlarmTimestamp;
    
    @TableField("user_uuid")
    String userUuid;
    
}
