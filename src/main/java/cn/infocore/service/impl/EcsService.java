package cn.infocore.service.impl;

import java.util.LinkedList;
import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.*;
import cn.infocore.manager.CloudClientDeviceManager;
import cn.infocore.manager.CloudClientManager;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    CloudClientManager cloudClientManager;

    @Autowired
    CloudClientDeviceManager cloudClientDeviceManager;

    private static final org.apache.log4j.Logger logger = Logger.getLogger(EcsService.class);

    public void ReUpdateEcsInfo(EcsInfo ecsInfo) {

        String uuid = ecsInfo.getId();
        String name = ecsInfo.getName();
        ClientType type = ecsInfo.getType();
        List<EcsInstanceInfo> EcsInstanceList = ecsInfo.getInstanceListList();
        List<FaultType> ossFaultList = ecsInfo.getStatusList();
        StringBuffer EcsFaultLists = new StringBuffer();
        for (FaultType fault : ossFaultList) {
            int code = fault.getNumber();
            EcsFaultLists.append(code).append(";");
        }
        CloudDo cloudDo = new CloudDo();
        Boolean isDr = CheckCloudIsDr(cloudDo);
        if(isDr) {
            cloudDo.setIsDr(1);
        }else {
            cloudDo.setIsDr(0);
        }
        cloudDo.setName(name);
        cloudDo.setUuId(uuid);
        cloudDo.setType(type.getNumber());
        cloudDo.setExceptions( EcsFaultLists.toString());

        //更新CloudDevice
        cloudClientManager.updateCloudClient(uuid, cloudDo);
        if (EcsInstanceList != null) {
            for (EcsInstanceInfo ecsInstanceInfo :EcsInstanceList) {
                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetEcsCloudDevice(ecsInstanceInfo);
                cloudDeviceDo.setSize(ecsInstanceInfo.getSize());
                cloudDeviceDo.setType(ecsInstanceInfo.getType().getNumber());
                long preoccupationSizeByte = ecsInstanceInfo.getPreoccupationSizeByte();
                int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
                cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
                String objectSetId = cloudDeviceDo.getUuid();
                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,objectSetId,ecsInstanceInfo.getType());
            }
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
        logger.info(EcsInstanceFaultSimpleList);
        EcsDO ecsDO = new EcsDO();
        ecsDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));

        ecsManager.updateById(ecsDO);
        List<FaultSimple> faultsList = listFaults(faultTypes);
        List<String> userIdList = cloudClientManager.getUserIdByUuid(id);
//        faultsList.addAll(EcsInstanceFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(id);
            faultSimple.setTargetName(name);
        }

        faultsList.addAll(EcsInstanceFaultSimpleList);

        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userIdList);
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

    public Boolean CheckCloudIsDr(CloudDo cloudDo){
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
