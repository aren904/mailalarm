package cn.infocore.manager;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import cn.infocore.utils.StupidStringUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.infocore.dao.EcsInstanceMapper;
import cn.infocore.entity.EcsInstanceDO;


import java.util.LinkedList;
import java.util.List;

//import static cn.infocore.protobuf.StmStreamerDrManage.*;

@Service
public class EcsInstanceManager extends ServiceImpl<EcsInstanceMapper, EcsInstanceDO> {

//    public void updateByInstanceId(String id, EcsInstanceDO ecsInstanceDO) {
//        LambdaQueryWrapper<EcsInstanceDO> queryWrapper = new LambdaQueryWrapper<>();
//
//        queryWrapper.eq(EcsInstanceDO::getInstanceId, id);
//
//        this.baseMapper.update(ecsInstanceDO, queryWrapper);
//    }

    public List<FaultSimple> updateList(List<StreamerClouddrmanage.EcsInstanceInfo> ecsInstanceInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (StreamerClouddrmanage.EcsInstanceInfo ecsInstanceInfo : ecsInstanceInfos) {
            List<StreamerClouddrmanage.FaultType> list = ecsInstanceInfo.getStatusList();
            faultList.addAll(listFaults(list,ecsInstanceInfo));
        }
        return faultList;
    }



    List<FaultSimple> listFaults(List<StreamerClouddrmanage.FaultType> faultTypes, StreamerClouddrmanage.EcsInstanceInfo ecsInstanceInfo) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(StreamerClouddrmanage.ClientType.EcsInstance);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetUuid(ecsInstanceInfo.getId());
            faultSimple.setDataArkName(ecsInstanceInfo.getName());
            faultList.add(faultSimple);
        }
        return faultList;
    }
}

