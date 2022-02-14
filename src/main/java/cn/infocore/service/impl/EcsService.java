package cn.infocore.service.impl;

import java.util.LinkedList;
import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.*;
import cn.infocore.manager.*;
import cn.infocore.utils.StupidStringUtil;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EcsService {

    @Autowired
    EcsInstanceManager ecsInstanceManager;

//    @Autowired
//    CloudClientManager cloudClientManager;
//
//    @Autowired
//    CloudClientDeviceManager cloudClientDeviceManager;

    @Autowired
    ClientManager clientManager;

//    @Autowired
//    ClientDeviceManager clientDeviceManager;


    @Autowired
    ClientBackupManager clientBackupManager;

    private static final org.apache.log4j.Logger logger = Logger.getLogger(EcsService.class);
    @Synchronized
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
//        CloudDo cloudDo = new CloudDo();
//
//        cloudDo.setName(name);
//        cloudDo.setUuId(uuid);
//        cloudDo.setType(type.getNumber());
//        cloudDo.setExceptions( EcsFaultLists.toString());
//
//        //更新CloudDevice
//        cloudClientManager.updateCloudClient(uuid, cloudDo);
//        if (EcsInstanceList != null) {
//            for (StreamerClouddrmanage.EcsInstanceInfo ecsInstanceInfo :EcsInstanceList) {
//                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetEcsCloudDevice(ecsInstanceInfo);
//                String objectSetId = cloudDeviceDo.getUuid();
//                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,objectSetId);
//            }
//        }
        ClientDo clientDo = new ClientDo();
        clientDo.setName(name)
                .setUuId(uuid)
                .setType(type.getNumber())
                .setExceptions(EcsFaultLists.toString());
        clientManager.updateClient(uuid, clientDo);
        //更新ClientDevice
        if(EcsFaultLists!=null){
            for (StreamerClouddrmanage.EcsInstanceInfo ecsInstanceInfo : EcsInstanceList) {
//                ClientDeviceDo clientDeviceDo = clientDeviceManager.ResetClientDevice(ecsInstanceInfo);
//                ClientDeviceDo clientDeviceDo1 = clientBackupManager.ResetClientBackup(ecsInstanceInfo);
                ClientBackupDo clientBackupDo = clientBackupManager.ResetClientBackup(ecsInstanceInfo);
                String objectSetId = clientBackupDo.getUuid();
                clientBackupManager.updateObjectSetDo(clientBackupDo,objectSetId);
            }
        }
    }

    public List<FaultSimple> updateEcsClientList(List<StreamerClouddrmanage.EcsInfo> ecsClientsList) {
        List<FaultSimple> faultSimpleList = new LinkedList<>();
        for (StreamerClouddrmanage.EcsInfo ecsInfo : ecsClientsList) {
            faultSimpleList.addAll(updateEcsClient(ecsInfo));
        }
//        logger.info(faultSimpleList);
        return faultSimpleList;
    }

    public List<FaultSimple> updateEcsClient(StreamerClouddrmanage.EcsInfo ecsInfo) {

        String id = ecsInfo.getId();
        String name = ecsInfo.getName();
        List<StreamerClouddrmanage.FaultType> faultTypes = ecsInfo.getStatusList();
        List<StreamerClouddrmanage.EcsInstanceInfo> instanceListList = ecsInfo.getInstanceListList();
        List<FaultSimple> EcsInstanceFaultSimpleList = ecsInstanceManager.updateList(instanceListList);

        EcsDO ecsDO = new EcsDO();
        ecsDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));

//        ecsManager.updateById(ecsDO);
        List<FaultSimple> faultsList = listFaults(faultTypes);
//        faultsList.addAll(EcsInstanceFaultSimpleList);
//        List<String> userUuIdList = cloudClientManager.getUserIdByUuid(id);
        List<String> userUuIdList = clientManager.getUserIdByUuid(id);

        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(id);
            faultSimple.setTargetName(name);
        }

        faultsList.addAll(EcsInstanceFaultSimpleList);
//
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userUuIdList);
        }
        logger.info(EcsInstanceFaultSimpleList);
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
