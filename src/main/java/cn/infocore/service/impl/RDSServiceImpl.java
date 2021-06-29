package cn.infocore.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.*;
import cn.infocore.manager.CloudClientDeviceManager;
import cn.infocore.manager.CloudClientManager;
import cn.infocore.operator.Header;
//import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.infocore.dao.UserDAO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.manager.RDSInstanceManager;
//import cn.infocore.manager.RdsManager;
//import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
//import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
//import cn.infocore.protobuf.StmStreamerDrManage.RdsInfo;
//import cn.infocore.protobuf.StmStreamerDrManage.RdsInstanceInfo;
import cn.infocore.service.RDSService;

@Service
public class RDSServiceImpl implements RDSService {

    @Autowired
    RDSInstanceManager rdsInstanceManager;

    @Autowired
    CloudClientDeviceManager cloudClientDeviceManager;

    @Autowired
    CloudClientManager cloudClientManager;

    private static final Logger logger = Logger.getLogger( RDSServiceImpl.class);

    @Override
    public void ReUpdateRdsClient(StreamerClouddrmanage.RdsInfo rdsClient) {

        String uuid = rdsClient.getUuid();
        String name = rdsClient.getName();
        StreamerClouddrmanage.ClientType type = rdsClient.getType();
        List<StreamerClouddrmanage.RdsInstanceInfo> RdsInstanceList = rdsClient.getInstanceListList();
        List<StreamerClouddrmanage.FaultType> rdsFaultList = rdsClient.getStatusList();
        StringBuffer RdsFaultLists = new StringBuffer();
        for (StreamerClouddrmanage.FaultType fault : rdsFaultList) {
            int code = fault.getNumber();
            RdsFaultLists.append(code).append(";");
        }
        CloudDo cloudDo = new CloudDo();
//        Boolean isDr = cloudClientManager.checkCloudIsDr(cloudDo);
//        if(isDr) {
//            cloudDo.setIsDr(1);
//        }else {
//            cloudDo.setIsDr(0);
//        }
        cloudDo.setName(name);
        cloudDo.setUuId(uuid);
        cloudDo.setType(type.getNumber());
        cloudDo.setExceptions( RdsFaultLists.toString());
        cloudClientManager.updateCloudClient(uuid, cloudDo);
        //更新CloudDevice
        if (RdsInstanceList != null) {
            for (StreamerClouddrmanage.RdsInstanceInfo rdsInstanceInfo :RdsInstanceList) {
                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetRdsCloudDevice(rdsInstanceInfo);
                cloudDeviceDo.setSize(rdsInstanceInfo.getSize());
//                cloudDeviceDo.setType(13);
                cloudDeviceDo.setType(rdsInstanceInfo.getType().getNumber());
                long preoccupationSizeByte = rdsInstanceInfo.getPreoccupationSizeByte();
                int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
                cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
                String objectSetId = cloudDeviceDo.getUuid();
                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,objectSetId);
            }
        }


    }


    @Override
    public List<FaultSimple> updateRdsInfoClientList(List<StreamerClouddrmanage.RdsInfo> rdsInfos) {
        List<FaultSimple> faultSimpleList = new LinkedList<>();
        for (StreamerClouddrmanage.RdsInfo rdsInfo : rdsInfos) {
            faultSimpleList.addAll(updateRdsClient(rdsInfo));
        }
        return faultSimpleList;
    }

    public List<FaultSimple> updateRdsClient(StreamerClouddrmanage.RdsInfo rdsInfo) {

        String uuid = rdsInfo.getUuid();
        String name = rdsInfo.getName();

        List<StreamerClouddrmanage.FaultType> faultTypes = rdsInfo.getStatusList();
        List<StreamerClouddrmanage.RdsInstanceInfo> instanceListList = rdsInfo.getInstanceListList();
        List<FaultSimple> rdsfaultSimpleList = rdsInstanceManager.updateList(instanceListList);

        RdsDO rdsDO = new RdsDO();
        rdsDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));

        List<FaultSimple> faultsList = listFaults(faultTypes);

        List<String> userUuIdList = cloudClientManager.getUserIdByUuid(uuid);


        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(uuid);
            faultSimple.setTargetName(name);
        }

        faultsList.addAll(rdsfaultSimpleList);

        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userUuIdList);
        }

        return faultsList;
    }

    List<FaultSimple> listFaults(List<StreamerClouddrmanage.FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(StreamerClouddrmanage.ClientType.Rds);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }


}
