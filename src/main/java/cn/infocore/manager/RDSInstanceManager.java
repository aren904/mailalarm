package cn.infocore.manager;

import java.util.LinkedList;
import java.util.List;

import cn.infocore.bo.FaultSimple;

import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.RDSInstanceMapper;
import cn.infocore.entity.RdsInstanceDO;
import org.springframework.stereotype.Service;

import static cn.infocore.protobuf.StmStreamerDrManage.*;

@Service
public class RDSInstanceManager  {


    public List<FaultSimple> updateList(List<RdsInstanceInfo> rdsInstanceInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (RdsInstanceInfo rdsInstanceInfo : rdsInstanceInfos) {
            List<FaultType> list = rdsInstanceInfo.getStatusList();
            faultList.addAll(listFaults(list,rdsInstanceInfo));
        }
        return faultList;
    }

    List<FaultSimple> listFaults(List<FaultType> faultTypes,RdsInstanceInfo rdsInstanceInfo) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.RdsInstance);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(rdsInstanceInfo.getName());
            faultSimple.setTargetUuid(rdsInstanceInfo.getUuid());
            faultList.add(faultSimple);
        }
        return faultList;
    }

}
