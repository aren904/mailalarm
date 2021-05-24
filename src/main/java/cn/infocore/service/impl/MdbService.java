package cn.infocore.service.impl;


import java.util.LinkedList;
import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.CloudDeviceDo;
import cn.infocore.entity.CloudDo;
import cn.infocore.entity.MdbDeviceDo;
import cn.infocore.main.InfoProcessData;
import cn.infocore.manager.CloudClientDeviceManager;
import cn.infocore.manager.CloudClientManager;
import cn.infocore.manager.MdbDeviceManager;
//import cn.infocore.manager.MetaManager;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.infocore.entity.MdbDO;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.MetaBackupInfo;
import cn.infocore.protobuf.StmStreamerDrManage.MetaInfo;

import static cn.infocore.protobuf.StmStreamerDrManage.*;

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


    public void ReUpdateMdbClient(MetaInfo metaClient) {
        String uuid = metaClient.getId();
        List<MetaBackupInfo> backupListList = metaClient.getBackupListList();
        List<FaultType> MetaFaultList = metaClient.getStatusList();
        StringBuilder metaFaultList = new StringBuilder();
        for (FaultType metaFaultSimple : MetaFaultList) {
            int code = metaFaultSimple.getNumber();
            metaFaultList.append(code).append(";");
        }
        CloudDo  cloudDo= new CloudDo();
        cloudDo.setUuId(uuid);
        cloudDo.setName(metaClient.getName());
        cloudDo.setType(metaClient.getType().getNumber());//
        cloudDo.setExceptions(metaFaultList.toString());
        Boolean isDr = checkCloudIsDr(cloudDo);
        if (isDr) {
            cloudDo.setIsDr(1);
        } else {
            cloudDo.setIsDr(0);
        }
        cloudClientManager.updateCloudClient(uuid, cloudDo);

        if (backupListList != null) {
            for (StmStreamerDrManage.MetaBackupInfo backupInfo :backupListList) {
                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetMetaBackupListCloudDevice(backupInfo);
                cloudDeviceDo.setSize(backupInfo.getSize());
//                cloudDeviceDo.setType(19);
                cloudDeviceDo.setType(backupInfo.getType().getNumber());
                long preoccupationSizeByte = backupInfo.getPreoccupationSizeByte();
                int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
                cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
                String uuid1 = cloudDeviceDo.getUuid();
                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,uuid1,backupInfo.getType());
            }
        }
    }

    public List<FaultSimple> updateMetaClientList(List<MetaInfo> metaClientsList) {
        List<FaultSimple> faultSimpleList = new LinkedList<>();
        for (MetaInfo metaInfo : metaClientsList) {
            faultSimpleList.addAll(updateMetaClient(metaInfo));
        }
        return faultSimpleList;
    }

    public List<FaultSimple> updateMetaClient(MetaInfo metaInfo) {

        String uuid = metaInfo.getId();
        String name = metaInfo.getName();
        List<FaultType> faultTypes = metaInfo.getStatusList();
        List<MetaBackupInfo> backupListList = metaInfo.getBackupListList();
        List<FaultSimple> MetaBackupFaultSimpleList = mdbDeviceManager.updateList(backupListList);

        MdbDO mdbDO = new MdbDO();
        mdbDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));


        List<FaultSimple> faultsList = listFaults(faultTypes);
        List<String> userIdList = cloudClientManager.getUserIdByUuid(uuid);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(uuid);
            faultSimple.setTargetName(name);

        }
        faultsList.addAll(MetaBackupFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userIdList);
        }

        logger.info(faultsList);
        return faultsList;
    }


    List<FaultSimple> listFaults(List<FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.MetaDB);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }


    public Boolean checkCloudIsDr(CloudDo cloudDo){
        LambdaQueryWrapper<CloudDo> cloudDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cloudDoLambdaQueryWrapper.eq(CloudDo::getUuId, cloudDo.getUuId());
        CloudDo cloudDo1 = cloudClientManager.getOne(cloudDoLambdaQueryWrapper);
        if(cloudDo1!=null){
            Integer isDr = cloudDo.getIsDr();
            return isDr!=null&& isDr >0;
        }
        return false;
    }
}