package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.entity.EcsDO;
import cn.infocore.entity.EcsInstanceDO;
import cn.infocore.manager.EcsInstanceManager;
import cn.infocore.manager.EcsManager;
import cn.infocore.protobuf.StmStreamerDrManage.EcsInfo;
import cn.infocore.protobuf.StmStreamerDrManage.EcsInstanceInfo;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

@Service
public class EcsService {

    @Autowired
    EcsInstanceManager ecsInstanceManager;
    @Autowired
    EcsManager ecsManager;

    public void updateEcsInfo(EcsInfo ecsInfo) {
        List<EcsInstanceInfo> instanceInfos = ecsInfo.getInstanceListList();
        
        String ecsId = ecsInfo.getId();
        List<FaultType> faultsEcs =  ecsInfo.getStatusList();
        StringBuilder faultsEcsSB = new StringBuilder();
        for (FaultType fault : faultsEcs) {
           int code =  fault.getNumber();
           faultsEcsSB.append(code).append(";");
        }
        
        EcsDO ecsDO =new EcsDO();
        ecsDO.setEcsId(ecsId);
        ecsDO.setExceptions(faultsEcsSB.toString());
        ecsManager.updateByEcsId(ecsId,ecsDO);

        for (EcsInstanceInfo ecsInstanceInfo : instanceInfos) {
            String id = ecsInstanceInfo.getId();
            Long size =  ecsInstanceInfo.getSize();
            List<FaultType> faults =  ecsInstanceInfo.getStatusList();
            StringBuilder sb = new StringBuilder();
            for (FaultType fault : faults) {
               int code =  fault.getNumber();
               sb.append(code).append(";");
            }
            EcsInstanceDO ecsInstanceDO = new EcsInstanceDO();
            ecsInstanceDO.setSize(size);
            ecsInstanceDO.setExceptions(sb.toString());
            ecsInstanceManager.updateByInstanceId(id, ecsInstanceDO);
            
        }

    }

}
