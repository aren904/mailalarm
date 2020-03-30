package cn.infocore.bo;

import java.util.Collection;
import java.util.List;

import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class FaultSimple {
    
    volatile boolean done = false;
    
    String dataArkId;
    
    String dataArkIp;
    
    String dataArkName;
    
    String targetId;
    
    String targetName;
    
    ClientType clientType;
    
    Collection<FaultType> faultTypes;
    
    List<String> userIds;
    
    
}
