package cn.infocore.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.Fault;
import cn.infocore.entity.OssDO;
import cn.infocore.manager.OssManager;
import cn.infocore.manager.OssObjectSetManager;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.OssInfo;
import cn.infocore.protobuf.StmStreamerDrManage.OssObjectSetInfo;
import cn.infocore.service.OssService;
import cn.infocore.utils.StupidStringUtil;
@Service
public class OssServiceImpl implements OssService {

    @Autowired
    OssManager ossManager;
    
    @Autowired
    OssObjectSetManager ossObjectSetManager;
    
    @Override
    public List<FaultSimple> updateOssClientList(List<OssInfo> ossClients) {
        List<FaultSimple> faultList =  new LinkedList<FaultSimple>();
        
        for (OssInfo ossInfo : ossClients) {
            faultList.addAll(updateOssClient(ossInfo));
        }
        return faultList;
    }

    public List<FaultSimple> updateOssClient(OssInfo ossInfo) {
        
        String id = ossInfo.getUuid();
        String name = ossInfo.getName();
        //ClientType type  = ossInfo.getType();
        List<FaultType> faultTypes = ossInfo.getStatusList();
        List<OssObjectSetInfo> ossObjectSetInfos = ossInfo.getObjListList();
        List<FaultSimple> ossObjectFaultSimpleList = ossObjectSetManager.updateList(ossObjectSetInfos);
        
        OssDO ossDO = new OssDO();
        ossDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));
        
        ossManager.updateById(ossDO);
        List<FaultSimple>  faultsList = listFaults(faultTypes);
        List<String>  userIdList = ossManager.getOssUserIdsById(id);
        
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetId(id);
            faultSimple.setTargetName(name);
        }
        
        faultsList.addAll(ossObjectFaultSimpleList);
        
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserIds(userIdList);
        }
        return faultsList;
    }
    
    
    List<FaultSimple>  listFaults(List<FaultType> faultTypes){
        LinkedList<FaultSimple> faultList =  new LinkedList<FaultSimple>();
        if (faultTypes!= null) {
                FaultSimple faultSimple =  new FaultSimple();
                faultSimple.setClientType(ClientType.Oss);
                faultSimple.setFaultTypes(faultTypes);
                faultList.add(faultSimple);
        }
        return faultList;
    }
}
