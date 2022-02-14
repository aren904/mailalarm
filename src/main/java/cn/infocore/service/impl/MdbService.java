package cn.infocore.service.impl;


import java.util.LinkedList;
import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.*;
import cn.infocore.manager.*;
//import cn.infocore.manager.MetaManager;
//import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MdbService {
    //    @Autowired
//    MetaBackupManager metaBackupManager;
    @Autowired
    MdbDeviceManager mdbDeviceManager;

//    @Autowired
//    MetaManager metaManager;

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

    private static final Logger logger = Logger.getLogger(MdbService.class);


    public void ReUpdateMdbClient(StreamerClouddrmanage.MetaInfo metaClient) {
        String uuid = metaClient.getId();
        String name = metaClient.getName();
        StreamerClouddrmanage.ClientType type = metaClient.getType();
        List<StreamerClouddrmanage.MetaBackupInfo> backupListList = metaClient.getBackupListList();
        List<StreamerClouddrmanage.FaultType> MetaFaultList = metaClient.getStatusList();
        StringBuilder metaFaultList = new StringBuilder();
        for (StreamerClouddrmanage.FaultType metaFaultSimple : MetaFaultList) {
            int code = metaFaultSimple.getNumber();
            metaFaultList.append(code).append(";");
        }
//        CloudDo  cloudDo= new CloudDo();
//        cloudDo.setUuId(uuid);
//        cloudDo.setName(metaClient.getName());
//        cloudDo.setType(metaClient.getType().getNumber());//
//        cloudDo.setExceptions(metaFaultList.toString());
//
//        cloudClientManager.updateCloudClient(uuid, cloudDo);
//
//        if (backupListList != null) {
//            for (StreamerClouddrmanage.MetaBackupInfo backupInfo :backupListList) {
//                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetMetaBackupListCloudDevice(backupInfo);
//                String uuid1 = cloudDeviceDo.getUuid();
//                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,uuid1);
//            }
//        }

        ClientDo clientDo = new ClientDo();
        clientDo.setName(name)
                .setUuId(uuid)
                .setType(type.getNumber())
                .setExceptions(MetaFaultList.toString());
        clientManager.updateClient(uuid, clientDo);
        //更新ClientDevice
        if(backupListList!=null){
//            for (StreamerClouddrmanage.RdsInstanceInfo rdsInstanceInfo : RdsInstanceList) {
            for (StreamerClouddrmanage.MetaBackupInfo metaBackupInfo : backupListList) {
                ClientBackupDo clientBackupDo = clientBackupManager.ResetClientBackup(metaBackupInfo);
                String objectSetId = clientBackupDo.getUuid();
                clientBackupManager.updateObjectSetDo(clientBackupDo,objectSetId);
            }
        }
    }

    public List<FaultSimple> updateMetaClientList(List<StreamerClouddrmanage.MetaInfo> metaClientsList) {
        List<FaultSimple> faultSimpleList = new LinkedList<>();
        for (StreamerClouddrmanage.MetaInfo metaInfo : metaClientsList) {
            faultSimpleList.addAll(updateMetaClient(metaInfo));
        }
        return faultSimpleList;
    }

    public List<FaultSimple> updateMetaClient(StreamerClouddrmanage.MetaInfo metaInfo) {

        String uuid = metaInfo.getId();
        String name = metaInfo.getName();
        List<StreamerClouddrmanage.FaultType> faultTypes = metaInfo.getStatusList();
        List<StreamerClouddrmanage.MetaBackupInfo> backupListList = metaInfo.getBackupListList();
        List<FaultSimple> MetaBackupFaultSimpleList = mdbDeviceManager.updateList(backupListList);

        MdbDO mdbDO = new MdbDO();
        mdbDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));


        List<FaultSimple> faultsList = listFaults(faultTypes);
//        List<String> userUuIdList = cloudClientManager.getUserIdByUuid(uuid);
        List<String> userUuIdList = clientManager.getUserIdByUuid(uuid);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(uuid);
            faultSimple.setTargetName(name);

        }
        faultsList.addAll(MetaBackupFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userUuIdList);
        }

//        logger.info(faultsList);
        return faultsList;
    }


    List<FaultSimple> listFaults(List<StreamerClouddrmanage.FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(StreamerClouddrmanage.ClientType.MetaDB);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }



}