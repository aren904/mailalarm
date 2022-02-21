package cn.infocore.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dto.EcsDTO;
import cn.infocore.dto.FaultDTO;
import cn.infocore.mapper.EcsMapper;
import cn.infocore.protobuf.StmAlarmManage;

@Service
public class EcsManager extends ServiceImpl<EcsMapper, EcsDTO>{
	
    @Autowired
    private ClientManager clientManager;
    
    /**
     * 获取客户端的异常集合并转换为FaultDTO
     * @param ecsInfo
     * @return
     */
    public List<FaultDTO> findFaultFromEcsClient(StmAlarmManage.EcsInfo ecsInfo) {
    	//获取客户端异常并转换为FaultDTO集合
        List<StmAlarmManage.FaultType> faultTypes = ecsInfo.getStatusList();
        List<FaultDTO> faults = convertEcsClientFaults(faultTypes);
        
        //获取备份关系的异常并转换为FaultDTO集合
        List<StmAlarmManage.EcsInstanceInfo> ecsInstances = ecsInfo.getInstanceListList();
        List<FaultDTO> ecsInstanceFaults = findFaultFromEcsInstance(ecsInstances);
        faults.addAll(ecsInstanceFaults);

        String uuid = ecsInfo.getId();
        String name = ecsInfo.getName();
        List<String> userUuids = clientManager.getUserUuidsByUuid(uuid);
        for (FaultDTO fault : faults) {
        	fault.setTargetUuid(uuid);
        	fault.setTargetName(name);
        	fault.setUserUuids(userUuids);
        }
        return faults;
    }
    
    /**
     * 获取备份关系中的异常集合并转换为FaultDTO
     * @param ecsInstanceInfos
     * @return
     */
    public List<FaultDTO> findFaultFromEcsInstance(List<StmAlarmManage.EcsInstanceInfo> ecsInstanceInfos) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        for (StmAlarmManage.EcsInstanceInfo ecsInstanceInfo : ecsInstanceInfos) {
            List<StmAlarmManage.FaultType> faultTypes = ecsInstanceInfo.getStatusList();
                faultList.addAll(convertECSInstanceFaults(faultTypes, ecsInstanceInfo));
        }
        return faultList;
    }
    
    /**
     * FaultTypes转ECSInstance FaultDTO
     * @param faultTypes
     * @return
     */
    public List<FaultDTO> convertECSInstanceFaults(List<StmAlarmManage.FaultType> faultTypes, StmAlarmManage.EcsInstanceInfo ecsInstanceInfo) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        if (faultTypes != null) {
            FaultDTO faultSimple = new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.EcsInstance);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetUuid(ecsInstanceInfo.getId());
            faultSimple.setTargetName(ecsInstanceInfo.getName());
            faultList.add(faultSimple);
        }
        return faultList;
    }
	
    /**
     * FaultTypes转ECS FaultDTO
     * @param faultTypes
     * @return
     */
	public List<FaultDTO> convertEcsClientFaults(List<StmAlarmManage.FaultType> faultTypes) {
        LinkedList<FaultDTO> faultList = new LinkedList<FaultDTO>();
        if (faultTypes != null) {
            FaultDTO faultSimple = new FaultDTO();
            faultSimple.setClientType(StmAlarmManage.ClientType.Ecs);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }
	
    public List<String> getEcsUserIdsById(String id) {
        LambdaQueryWrapper<EcsDTO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EcsDTO::getId, id);
        LinkedList<String> userIdList = new LinkedList<>();

        Collection<EcsDTO> ecsDOS = this.list(lambdaQueryWrapper);
        for (EcsDTO ecsDO : ecsDOS) {
            userIdList.add(ecsDO.getUserId());
        }

        return userIdList;
    }
}
