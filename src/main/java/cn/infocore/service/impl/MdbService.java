package cn.infocore.service.impl;


import java.util.LinkedList;
import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.CloudDeviceDo;
import cn.infocore.entity.CloudDo;
import cn.infocore.entity.MdbDeviceDo;
import cn.infocore.main.InfoProcessData;
import cn.infocore.manager.CloudClientDeviceManager;
import cn.infocore.manager.CloudClientManager;
import cn.infocore.manager.MdbDeviceManager;
//import cn.infocore.manager.MetaManager;
//import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.infocore.entity.MdbDO;


@Service
public class MdbService {
    //    @Autowired
//    MetaBackupManager metaBackupManager;
    @Autowired
    MdbDeviceManager mdbDeviceManager;

//    @Autowired
//    MetaManager metaManager;

    @Autowired
    CloudClientManager cloudClientManager;

    @Autowired
    CloudClientDeviceManager cloudClientDeviceManager;

    private static final Logger logger = Logger.getLogger(MdbService.class);


    public void ReUpdateMdbClient(StreamerClouddrmanage.MetaInfo metaClient) {
        String uuid = metaClient.getId();
        List<StreamerClouddrmanage.MetaBackupInfo> backupListList = metaClient.getBackupListList();
        List<StreamerClouddrmanage.FaultType> MetaFaultList = metaClient.getStatusList();
        StringBuilder metaFaultList = new StringBuilder();
        for (StreamerClouddrmanage.FaultType metaFaultSimple : MetaFaultList) {
            int code = metaFaultSimple.getNumber();
            metaFaultList.append(code).append(";");
        }
        CloudDo  cloudDo= new CloudDo();
        cloudDo.setUuId(uuid);
        cloudDo.setName(metaClient.getName());
        cloudDo.setType(metaClient.getType().getNumber());//
        cloudDo.setExceptions(metaFaultList.toString());

        cloudClientManager.updateCloudClient(uuid, cloudDo);

        if (backupListList != null) {
            for (StreamerClouddrmanage.MetaBackupInfo backupInfo :backupListList) {
                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetMetaBackupListCloudDevice(backupInfo);
                cloudDeviceDo.setSize(backupInfo.getSize());
//                cloudDeviceDo.setType(19);
                cloudDeviceDo.setType(backupInfo.getType().getNumber());
                long preoccupationSizeByte = backupInfo.getPreoccupationSizeByte();
                int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
                cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
                String uuid1 = cloudDeviceDo.getUuid();
                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,uuid1);
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
        List<String> userUuIdList = cloudClientManager.getUserIdByUuid(uuid);
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