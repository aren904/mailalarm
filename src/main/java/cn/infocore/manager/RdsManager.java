package cn.infocore.manager;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

@Service
public class RdsManager  {
	
	@Autowired
    private ClientManager clientManager;
	
	/**
     * 获取RDS客户端的异常集合并转换为FaultDTO
     * @param ossInfo
     * @return
     */
	public List<FaultDTO> findFaultFromRdsClient(StmAlarmManage.RdsInfo rdsInfo) {
		//获取客户端异常并转换为FaultDTO集合
        List<StmAlarmManage.FaultType> faultTypes = rdsInfo.getStatusList();
        List<FaultDTO> faults = convertRdsClientFaults(faultTypes);
        
        //获取实例的异常并转换为FaultDTO集合
        List<StmAlarmManage.RdsInstanceInfo> instances = rdsInfo.getInstanceListList();
        List<FaultDTO> rdsInstanceFaults = findFaultFromRdsIntance(instances);
        faults.addAll(rdsInstanceFaults);

        //补充信息
        String uuid = rdsInfo.getUuid();
        String name = rdsInfo.getName();
        List<String> userUuids = clientManager.getUserUuidsByUuid(uuid);
        for (FaultDTO faultSimple : faults) {
            faultSimple.setTargetUuid(uuid);
            faultSimple.setTargetName(name);
            faultSimple.setUserUuids(userUuids);
        }
        return faults;
	}

	/**
     * 获取实例的异常集合并转换为FaultDTO
     * @param rdsInstanceInfos
     * @return
     */
    public List<FaultDTO> findFaultFromRdsIntance(List<StmAlarmManage.RdsInstanceInfo> rdsInstanceInfos) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        for (StmAlarmManage.RdsInstanceInfo rdsInstanceInfo : rdsInstanceInfos) {
            List<StmAlarmManage.FaultType> list = rdsInstanceInfo.getStatusList();
            faultList.addAll(convertRdsInstanceFaults(list,rdsInstanceInfo));
        }
        return faultList;
    }
    
    /**
     * FaultTypes转RdsInstanceInfo FaultDTO
     * @param faultTypes
     * @return
     */
    public List<FaultDTO> convertRdsInstanceFaults(List<StmAlarmManage.FaultType> faultTypes, StmAlarmManage.RdsInstanceInfo rdsInstanceInfo) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        if (faultTypes != null) {
            FaultDTO faultSimple = new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.RdsInstance);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(rdsInstanceInfo.getName());
            faultSimple.setTargetUuid(rdsInstanceInfo.getUuid());
            faultList.add(faultSimple);
        }
        return faultList;
    }
    
    /**
     * FaultTypes转RDS FaultDTO
     * @param faultTypes
     * @return
     */
    public List<FaultDTO> convertRdsClientFaults(List<StmAlarmManage.FaultType> faultTypes) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        if (faultTypes != null) {
            FaultDTO faultSimple = new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.Rds);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }

}
