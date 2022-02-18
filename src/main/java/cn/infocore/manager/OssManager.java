package cn.infocore.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

@Component
public class OssManager  {
	
    @Autowired
    private ClientManager clientManager;
    
    /**
     * 获取OSS客户端的异常集合并转换为FaultDTO
     * @param ossInfo
     * @return
     */
    public List<FaultDTO> findFaultFromOssClient(StmAlarmManage.OssInfo ossInfo) {
    	//获取客户端异常并转换为FaultDTO集合
    	List<StmAlarmManage.FaultType> faultTypes = ossInfo.getStatusList();
    	List<FaultDTO> faults = convertOSSClientFaults(faultTypes);
    	
    	//获取备份关系的异常并转换为FaultDTO集合
        List<StmAlarmManage.OssObjectSetInfo> ossObjectSetInfos = ossInfo.getObjListList();
        List<FaultDTO> ossObjectFaults = findFaultFromOssObjectSetInfo(ossObjectSetInfos);
        faults.addAll(ossObjectFaults);
        
        //补充信息
    	String uuid = ossInfo.getUuid();
    	String name = ossInfo.getName();
        List<String> userUuids = clientManager.getUserUuidsByUuid(uuid);
        for (FaultDTO fault : faults) {
        	fault.setUserUuids(userUuids);
        	fault.setTargetUuid(uuid);
    		fault.setTargetName(name);
        }
        return faults;
    }

    /**
     * 获取备份关系中的异常集合并转换为FaultDTO
     * @param ossObjectSetInfos
     * @return
     */
    public List<FaultDTO> findFaultFromOssObjectSetInfo(List<StmAlarmManage.OssObjectSetInfo> ossObjectSetInfos) {
        List<FaultDTO> faults = new ArrayList<>();
        for (StmAlarmManage.OssObjectSetInfo ossObjectSetInfo : ossObjectSetInfos) {
        	//每个关系的异常集合
            List<StmAlarmManage.FaultType> faultTypes = ossObjectSetInfo.getStatusList();
            faults.addAll(convertOSSObjectSetFaults(faultTypes, ossObjectSetInfo));
        }
        return faults;
    }
    
    /**
     * FaultTypes转OSSObjectSet FaultDTO
     * @param faultTypes
     * @return
     */
    public List<FaultDTO> convertOSSObjectSetFaults(List<StmAlarmManage.FaultType> faultTypes, StmAlarmManage.OssObjectSetInfo ossObjectSetInfo) {
    	List<FaultDTO> faultList = new ArrayList<>();
        if (faultTypes != null) {
            FaultDTO faultSimple = new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.OssObjectSet);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(ossObjectSetInfo.getName());
            faultSimple.setTargetUuid(ossObjectSetInfo.getId());
            faultList.add(faultSimple);
        }
        return faultList;
    }
    
    /**
     * FaultTypes转OSS FaultDTO
     * @param faultTypes
     * @return
     */
    public List<FaultDTO> convertOSSClientFaults(List<StmAlarmManage.FaultType> faultTypes){
        LinkedList<FaultDTO> faultList =  new LinkedList<FaultDTO>();
        if (faultTypes!= null) {
            FaultDTO faultSimple =  new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.Oss);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }
}
