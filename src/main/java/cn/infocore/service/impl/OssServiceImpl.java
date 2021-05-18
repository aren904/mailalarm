package cn.infocore.service.impl;
import java.util.LinkedList;
import java.util.List;
import cn.infocore.entity.*;
import cn.infocore.manager.CloudClientDeviceManager;
import cn.infocore.manager.CloudClientManager;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.infocore.bo.FaultSimple;
import cn.infocore.manager.OssObjectSetManager;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.OssInfo;
import cn.infocore.protobuf.StmStreamerDrManage.OssObjectSetInfo;
import cn.infocore.service.OssService;
import cn.infocore.utils.StupidStringUtil;
@Service
public class OssServiceImpl implements OssService {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(OssServiceImpl.class);

    
    @Autowired
    OssObjectSetManager ossObjectSetManager;


    @Autowired
    CloudClientManager cloudClientManager;

    @Autowired
    CloudClientDeviceManager cloudClientDeviceManager;

    @Override
    public void ReUpdateOssClient(OssInfo ossClient) {


        String uuid = ossClient.getUuid();
        String name = ossClient.getName();
        ClientType type = ossClient.getType();
        List<OssObjectSetInfo> objListList = ossClient.getObjListList();
        List<FaultType> ossFaultList = ossClient.getStatusList();
        StringBuffer ossFaultLists = new StringBuffer();
        for (FaultType fault : ossFaultList) {
            int code = fault.getNumber();
            ossFaultLists.append(code).append(";");
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
        cloudDo.setExceptions(ossFaultLists.toString());
        cloudClientManager.updateCloudClient(uuid, cloudDo);
        //更新CloudDevice
        if (objListList != null) {
            for (OssObjectSetInfo ossObjectSetInfo : objListList) {
                CloudDeviceDo cloudDeviceDo = cloudClientDeviceManager.ReSetOssCloudDevice(ossObjectSetInfo);
                String objectSetId = cloudDeviceDo.getUuid();
                cloudClientDeviceManager.updateObjectSetDo(cloudDeviceDo,objectSetId);
            }
        }
    }
    
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
        ClientType type  = ossInfo.getType();
        List<FaultType> faultTypes = ossInfo.getStatusList();
        List<OssObjectSetInfo> ossObjectSetInfos = ossInfo.getObjListList();
        List<FaultSimple> ossObjectFaultSimpleList = ossObjectSetManager.updateList(ossObjectSetInfos);
        logger.info(ossObjectFaultSimpleList);
        OssDO ossDO = new OssDO();
        ossDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));
        

        List<FaultSimple>  faultsList = listFaults(faultTypes);

        List<String> userIdList = cloudClientManager.getUserIdByUuid(id);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(id);
            faultSimple.setTargetName(name);

        }
        
        faultsList.addAll(ossObjectFaultSimpleList);
        
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userIdList);
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
