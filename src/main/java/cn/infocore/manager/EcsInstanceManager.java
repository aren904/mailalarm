package cn.infocore.manager;

import cn.infocore.bo.FaultSimple;
import cn.infocore.utils.StupidStringUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.infocore.dao.EcsInstanceMapper;
import cn.infocore.entity.EcsInstanceDO;


import java.util.LinkedList;
import java.util.List;

import static cn.infocore.protobuf.StmStreamerDrManage.*;

@Service
public class EcsInstanceManager extends ServiceImpl<EcsInstanceMapper, EcsInstanceDO> {

    public void updateByInstanceId(String id, EcsInstanceDO ecsInstanceDO) {
        LambdaQueryWrapper<EcsInstanceDO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(EcsInstanceDO::getInstanceId, id);

        this.baseMapper.update(ecsInstanceDO, queryWrapper);
    }

    public List<FaultSimple> updateList(List<EcsInstanceInfo> ecsInstanceInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (EcsInstanceInfo ecsInstanceInfo : ecsInstanceInfos) {
            List<FaultType> list = ecsInstanceInfo.getStatusList();
            faultList.addAll(listFaults(list,ecsInstanceInfo));
        }
        return faultList;
    }



    List<FaultSimple> listFaults(List<FaultType> faultTypes,EcsInstanceInfo ecsInstanceInfo) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.EcsInstance);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetUuid(ecsInstanceInfo.getId());
            faultSimple.setDataArkName(ecsInstanceInfo.getName());
            faultList.add(faultSimple);
        }
        return faultList;
    }


    public boolean checkDrInstance(String instanceId) {
        LambdaQueryWrapper<EcsInstanceDO> queryWrapper = new LambdaQueryWrapper<EcsInstanceDO>()
                .eq(EcsInstanceDO::getInstanceId, instanceId);
        EcsInstanceDO ecsInstanceDO = this.getOne(queryWrapper);
        if (ecsInstanceDO != null) {
            Integer isDr = ecsInstanceDO.getIsDrEnabled();
            return isDr != null && isDr > 0;
        }
        return false;

    }

}

