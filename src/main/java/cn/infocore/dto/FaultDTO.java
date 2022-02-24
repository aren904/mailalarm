package cn.infocore.dto;

import java.util.List;

import cn.infocore.protobuf.StmAlarmManage;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 异常整合：包含对应StreamerClouddrmanage.FaultType集合
 * 某个客户端或关系的异常：可能包含很多异常
 */
@ToString
@Accessors(chain = true)
@Data
public class FaultDTO {
    
    private String dataArkUuid;
    
    private String dataArkIp;
    
    private String dataArkName;
    
    private String targetUuid;
    
    private String targetName;
    
    //客户端类型
    private StmAlarmManage.ClientType clientType; 
    
    //异常类型
    private List<StmAlarmManage.FaultType> faultTypes;
    
    //拥有该异常集合的所有用户UUIDS
    private List<String> userUuids;
    
    private Long timestamp;
    
}
