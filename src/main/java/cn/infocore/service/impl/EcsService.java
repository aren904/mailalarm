package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.entity.EcsInstanceDO;
import cn.infocore.manager.EcsInstanceManager;
import cn.infocore.protobuf.StmStreamerDrManage.EcsInfo;
import cn.infocore.protobuf.StmStreamerDrManage.EcsInstanceInfo;

@Service
public class EcsService {

    @Autowired
    EcsInstanceManager ecsInstanceManager;

    public void updateEcsInfo(EcsInfo ecsInfo) {
        List<EcsInstanceInfo> instanceInfos = ecsInfo.getInstanceListList();

        for (EcsInstanceInfo ecsInstanceInfo : instanceInfos) {
            String id = ecsInstanceInfo.getId();
            Long size =  ecsInstanceInfo.getSize();

            EcsInstanceDO ecsInstanceDO = new EcsInstanceDO();
            ecsInstanceDO.setSize(size);
            ecsInstanceManager.updateByInstanceId(id, ecsInstanceDO);
            
        }

    }

}
