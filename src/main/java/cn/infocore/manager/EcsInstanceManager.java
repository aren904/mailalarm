package cn.infocore.manager;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.MdbBackupDO;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.EcsInstanceMapper;
import cn.infocore.dao.EcsMapper;
import cn.infocore.entity.EcsDO;
import cn.infocore.entity.EcsInstanceDO;
import cn.infocore.entity.OssObjectSetDO;

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
            update(ecsInstanceInfo);
            List<FaultType> list = ecsInstanceInfo.getStatusList();
            faultList.addAll(listFaults(list));
        }
        return faultList;
    }


    public void update(EcsInstanceInfo ecsInstanceInfo) {
      EcsInstanceDO ecsInstanceDO = collectEcsInstanceInfomation(ecsInstanceInfo);
        this.updateByObjestSetId(ecsInstanceDO);
        return;
    }


    public EcsInstanceDO collectEcsInstanceInfomation(EcsInstanceInfo ecsInstanceInfo) {
        String id = ecsInstanceInfo.getId();
        String name = ecsInstanceInfo.getName();
        ClientType type =ecsInstanceInfo.getType();
        List<FaultType> faultTypes = ecsInstanceInfo.getStatusList();
        Long size = ecsInstanceInfo.getSize();
        Long preoccupationSizeByte = ecsInstanceInfo.getPreoccupationSizeByte();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes);
        EcsInstanceDO ecsInstanceDO = new EcsInstanceDO();
        ecsInstanceDO.setInstanceId(id);
        ecsInstanceDO.setSize(size);
        ecsInstanceDO.setPreoccupationSize(preoccupationSizeByte);
        ecsInstanceDO.setExceptions(exceptions);

        return ecsInstanceDO;
    }


    public void updateByObjestSetId(EcsInstanceDO ecsInstanceDO) {

        LambdaQueryWrapper<EcsInstanceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EcsInstanceDO::getInstanceId, ecsInstanceDO.getInstanceId());
        String setid = ecsInstanceDO.getInstanceId();
        boolean isDr = checkDrInstance(setid);
        if (isDr) {
            ecsInstanceDO.setDrSize(ecsInstanceDO.getSize());
            ecsInstanceDO.setPreoccupationDrSize(ecsInstanceDO.getPreoccupationSize());
        }
        this.update(ecsInstanceDO, queryWrapper);
    }


    List<FaultSimple> listFaults(List<FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.EcsInstance);
            faultSimple.setFaultTypes(faultTypes);
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

