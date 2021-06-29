package cn.infocore.service.impl;

import java.util.LinkedList;
import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.*;
import cn.infocore.manager.CloudClientDeviceManager;
import cn.infocore.manager.CloudClientManager;
import cn.infocore.utils.StupidStringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.infocore.manager.EcsInstanceManager;


@Service
public class EcsService {

    @Autowired
    EcsInstanceManager ecsInstanceManager;

    @Autowired
    CloudClientManager cloudClientManager;

    @Autowired
    CloudClientDeviceManager cloudClientDeviceManager;

    private static final org.apache.log4j.Logger logger = Logger.getLogger(EcsService.class);

    public void ReUpdateEcsInfo(StreamerClouddrmanage.EcsInfo ecsInfo) {

        String uuid = ecsInfo.getId();
        String name = ecsInfo.getName();
        StreamerClouddrmanage.ClientType type = ecsInfo.getType();
        List<StreamerClouddrmanage.EcsInstanceInfo> EcsInstanceList = ecsInfo.getInstanceListList();
        List<StreamerClouddrmanage.FaultType> ecsFaultList = ecsInfo.getStatusList();
        StringBuffer EcsFaultLists = new StringBuffer();
        for (StreamerClouddrmanage.FaultType fault : ecsFaultList) {
            int code = fault.getNumber();
            EcsFaultLists.append(code).append(";");
        }
        CloudDo cloudDo = new CloudDo();

        cloudDo.setName(name);
        cloudDo.setUuId(uuid);
        cloudDo.setType(type.getNumber());
        cloudDo.setExceptions( EcsFaultLists.toString());

        //更新CloudDevice
        cloudClientManager.updateCloudClient(uuid, cloudDo);
        if (EcsInstanceList != null) {
            for (StreamerClouddrmanage.EcsInstanceInfo ecsInstanceInfo :EcsInstanceList) {
                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetEcsCloudDevice(ecsInstanceInfo);
                cloudDeviceDo.setSize(ecsInstanceInfo.getSize());
                cloudDeviceDo.setType(ecsInstanceInfo.getType().getNumber());
                long preoccupationSizeByte = ecsInstanceInfo.getPreoccupationSizeByte();
                int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
                cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
                String objectSetId = cloudDeviceDo.getUuid();
                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,objectSetId);
            }
        }
    }

    public List<FaultSimple> updateEcsClientList(List<StreamerClouddrmanage.EcsInfo> ecsClientsList) {
        List<FaultSimple> faultSimpleList = new LinkedList<>();
        for (StreamerClouddrmanage.EcsInfo ecsInfo : ecsClientsList) {
            faultSimpleList.addAll(updateEcsClient(ecsInfo));
        }
        return faultSimpleList;
    }

    public List<FaultSimple> updateEcsClient(StreamerClouddrmanage.EcsInfo ecsInfo) {

        String id = ecsInfo.getId();
        String name = ecsInfo.getName();
        List<StreamerClouddrmanage.FaultType> faultTypes = ecsInfo.getStatusList();
        List<StreamerClouddrmanage.EcsInstanceInfo> instanceListList = ecsInfo.getInstanceListList();
        List<FaultSimple> EcsInstanceFaultSimpleList = ecsInstanceManager.updateList(instanceListList);
        logger.info(EcsInstanceFaultSimpleList);
        EcsDO ecsDO = new EcsDO();
        ecsDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));

//        ecsManager.updateById(ecsDO);
        List<FaultSimple> faultsList = listFaults(faultTypes);
        List<String> userUuIdList = cloudClientManager.getUserIdByUuid(id);
//        faultsList.addAll(EcsInstanceFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(id);
            faultSimple.setTargetName(name);
        }

        faultsList.addAll(EcsInstanceFaultSimpleList);

        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userUuIdList);
        }
        return faultsList;
    }


    List<FaultSimple> listFaults(List<StreamerClouddrmanage.FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(StreamerClouddrmanage.ClientType.Ecs);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }



}
