package cn.infocore.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

@Service
public class MetaManager  {
	
	@Autowired
    private ClientManager clientManager;
    
    /**
     * 获取元数据客户端的异常集合并转换为FaultDTO
     * @param metaInfo
     * @return
     */
    public List<FaultDTO> findFaultFromMetaClient(StmAlarmManage.MetaInfo metaInfo) {
    	//获取客户端异常并转换为FaultDTO集合
        List<StmAlarmManage.FaultType> faultTypes = metaInfo.getStatusList();
        List<FaultDTO> faults = convertMetaClientFaults(faultTypes);
        
        //获取备份关系的异常并转换为FaultDTO集合
        List<StmAlarmManage.MetaBackupInfo> backups = metaInfo.getBackupListList();
        List<FaultDTO> backupFaults = findFaultFromMetaBackup(backups);
        faults.addAll(backupFaults);

        String uuid = metaInfo.getId();
    	String name = metaInfo.getName();
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
     * @param metaBackupInfos
     * @return
     */
    public List<FaultDTO> findFaultFromMetaBackup(List<StmAlarmManage.MetaBackupInfo> metaBackupInfos) {
    	List<FaultDTO> faults = new ArrayList<>();
        for (StmAlarmManage.MetaBackupInfo metaBackupInfo : metaBackupInfos) {
            List<StmAlarmManage.FaultType> faultTypes = metaBackupInfo.getStatusList();
            faults.addAll(convertMetaBackupFaults(faultTypes,metaBackupInfo));
        }
        return faults;
    }

    /**
     * FaultTypes转OSSObjectSet FaultDTO
     * @param faultTypes
     * @return
     */
    public List<FaultDTO> convertMetaBackupFaults(List<StmAlarmManage.FaultType> faultTypes, StmAlarmManage.MetaBackupInfo metaBackupInfo) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        if (faultTypes != null) {
            FaultDTO faultSimple = new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.MetaDBBackup);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(metaBackupInfo.getName());
            faultSimple.setTargetUuid(metaBackupInfo.getId());
            faultList.add(faultSimple);
        }
        return faultList;
    }
    
    /**
     * FaultTypes转Meta FaultDTO
     * @param faultTypes
     * @return
     */
    public List<FaultDTO> convertMetaClientFaults(List<StmAlarmManage.FaultType> faultTypes) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        if (faultTypes != null) {
            FaultDTO faultSimple = new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.MetaDB);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }

}
