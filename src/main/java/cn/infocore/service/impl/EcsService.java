package cn.infocore.service.impl;

import java.util.LinkedList;
import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.MdbDO;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.entity.EcsDO;
import cn.infocore.entity.EcsInstanceDO;
import cn.infocore.manager.EcsInstanceManager;
import cn.infocore.manager.EcsManager;
import cn.infocore.protobuf.StmStreamerDrManage.EcsInfo;
import cn.infocore.protobuf.StmStreamerDrManage.EcsInstanceInfo;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;

import static cn.infocore.protobuf.StmStreamerDrManage.*;

@Service
public class EcsService {

    @Autowired
    EcsInstanceManager ecsInstanceManager;
    @Autowired
    EcsManager ecsManager;

    public void updateEcsInfo(EcsInfo ecsInfo) {
        List<EcsInstanceInfo> instanceInfos = ecsInfo.getInstanceListList();

        String ecsId = ecsInfo.getId();
        List<FaultType> faultsEcs = ecsInfo.getStatusList();
        StringBuilder faultsEcsSB = new StringBuilder();
        for (FaultType fault : faultsEcs) {
            int code = fault.getNumber();
            faultsEcsSB.append(code).append(";");
        }

        EcsDO ecsDO = new EcsDO();
        ecsDO.setEcsId(ecsId);
        ecsDO.setExceptions(faultsEcsSB.toString());
        ecsManager.updateByEcsId(ecsId, ecsDO);

        for (EcsInstanceInfo ecsInstanceInfo : instanceInfos) {
            String id = ecsInstanceInfo.getId();
            Long size = ecsInstanceInfo.getSize();
            List<FaultType> faults = ecsInstanceInfo.getStatusList();
            StringBuilder sb = new StringBuilder();
            for (FaultType fault : faults) {
                int code = fault.getNumber();
                sb.append(code).append(";");
            }
            EcsInstanceDO ecsInstanceDO = new EcsInstanceDO();
            ecsInstanceDO.setSize(size);
            ecsInstanceDO.setExceptions(sb.toString());
            ecsInstanceManager.updateByInstanceId(id, ecsInstanceDO);

        }

    }

    public List<FaultSimple> updateEcsClientList(List<EcsInfo> ecsClientsList) {
        List<FaultSimple> faultSimpleList = new LinkedList<>();
        for (EcsInfo ecsInfo : ecsClientsList) {
            faultSimpleList.addAll(updateEcsClient(ecsInfo));
        }
        return faultSimpleList;
    }

    public List<FaultSimple> updateEcsClient(EcsInfo ecsInfo) {

        String id = ecsInfo.getId();
        String name = ecsInfo.getName();
        List<FaultType> faultTypes = ecsInfo.getStatusList();
        List<EcsInstanceInfo> instanceListList = ecsInfo.getInstanceListList();
        List<FaultSimple> EcsInstanceFaultSimpleList = ecsInstanceManager.updateList(instanceListList);

        EcsDO ecsDO = new EcsDO();
        ecsDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));

        ecsManager.updateById(ecsDO);
        List<FaultSimple> faultsList = listFaults(faultTypes);
        List<String> userIdList = ecsManager.getEcsUserIdsById(id);
        faultsList.addAll(EcsInstanceFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetId(id);
            faultSimple.setTargetName(name);
        }

        faultsList.addAll(EcsInstanceFaultSimpleList);

        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserIds(userIdList);
        }
        return faultsList;
    }


    List<FaultSimple> listFaults(List<FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.Ecs);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }


}
