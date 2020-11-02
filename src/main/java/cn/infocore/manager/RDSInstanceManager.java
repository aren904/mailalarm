package cn.infocore.manager;

import java.util.LinkedList;
import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.MdbBackupDO;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.RDSInstanceMapper;
import cn.infocore.entity.RdsInstanceDO;

import static cn.infocore.protobuf.StmStreamerDrManage.*;

@Component
public class RDSInstanceManager extends ServiceImpl<RDSInstanceMapper, RdsInstanceDO> {

//    public void updateByUUIDBatch(List<RdsInstanceDO> rdsInstanceList) {
//
//        for (RdsInstanceDO rdsInstance : rdsInstanceList) {
//            patchInstance(rdsInstance);
//        }
//    }
//
//    public boolean patchInstance(RdsInstanceDO instance) {
//
//        String id = instance.getInstanceId();
//        boolean isDR = checkDrInstance(id);
//        LambdaUpdateWrapper<RdsInstanceDO> updateWrapper = new UpdateWrapper<RdsInstanceDO>().lambda()
//                .eq(RdsInstanceDO::getInstanceId, instance.getInstanceId())
//                .set(RdsInstanceDO::getName, instance.getName())
//                .set(RdsInstanceDO::getExceptions, instance.getExceptions())
//                .set(RdsInstanceDO::getSize, instance.getSize())
//                .set(RdsInstanceDO::getPreoccupationSize, instance.getPreoccupationSize());
//        if (isDR) {
//            updateWrapper.set(RdsInstanceDO::getDrSize, instance.getSize())
//                .set(RdsInstanceDO::getPreoccupationDrSize, instance.getPreoccupationSize());
//        }
//
//        return this.update(new RdsInstanceDO(), updateWrapper);
//    }

//    boolean checkDrInstance(String instanceId) {
//        LambdaQueryWrapper<RdsInstanceDO> queryWrapper = new LambdaQueryWrapper<RdsInstanceDO>()
//                .eq(RdsInstanceDO::getInstanceId, instanceId);
//        RdsInstanceDO rdsInstanceDO = this.getOne(queryWrapper);
//        Integer isDr = rdsInstanceDO.getIsDrEnabled();
//        return isDr != null && isDr > 0;

//    }

//    ======================================

    public List<FaultSimple> updateList(List<RdsInstanceInfo> rdsInstanceInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (RdsInstanceInfo rdsInstanceInfo : rdsInstanceInfos) {
            update(rdsInstanceInfo);
            List<FaultType> list = rdsInstanceInfo.getStatusList();
            faultList.addAll(listFaults(list));
        }
        return faultList;
    }


    public void update(RdsInstanceInfo rdsInstanceInfo) {
        RdsInstanceDO rdsInstanceDO = collectMetaBackupInfomation(rdsInstanceInfo);
        this.updateByObjestSetId(rdsInstanceDO);
        return;
    }


    public void updateByObjestSetId(RdsInstanceDO rdsInstanceDO) {

        LambdaQueryWrapper<RdsInstanceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RdsInstanceDO::getInstanceId, rdsInstanceDO.getInstanceId());
        String id = rdsInstanceDO.getInstanceId();
        boolean isDr = checkDrInstance(id);
        if (isDr) {
           rdsInstanceDO.setDrSize(rdsInstanceDO.getSize());
            rdsInstanceDO.setPreoccupationDrSize(rdsInstanceDO.getPreoccupationSize());
        }
        this.update(rdsInstanceDO, queryWrapper);
    }



    public RdsInstanceDO collectMetaBackupInfomation(RdsInstanceInfo rdsInstanceInfo) {
        String id = rdsInstanceInfo.getUuid();
        String name = rdsInstanceInfo.getName();
        ClientType type = rdsInstanceInfo.getType();
        List<FaultType> faultTypes = rdsInstanceInfo.getStatusList();
        Long size = rdsInstanceInfo.getSize();
        Long preoccupationSizeByte = rdsInstanceInfo.getPreoccupationSizeByte();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes);
        RdsInstanceDO rdsInstanceDO = new RdsInstanceDO();
        rdsInstanceDO.setInstanceId(id);
        rdsInstanceDO.setName(name);
        rdsInstanceDO.setSize(size);
        rdsInstanceDO.setPreoccupationSize(preoccupationSizeByte);
        rdsInstanceDO.setExceptions(exceptions);

        return rdsInstanceDO;
    }

    List<FaultSimple> listFaults(List<FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.RdsInstance);
            faultSimple.setFaultTypes(faultTypes);
        }
        return faultList;
    }


    public boolean checkDrInstance(String instanceId) {
        LambdaQueryWrapper<RdsInstanceDO> queryWrapper = new LambdaQueryWrapper<RdsInstanceDO>()
                .eq(RdsInstanceDO::getInstanceId, instanceId);
        RdsInstanceDO rdsInstanceDO = this.getOne(queryWrapper);
        if (rdsInstanceDO != null) {
            Integer isDr = rdsInstanceDO.getIsDrEnabled();
            return isDr != null && isDr > 0;
        }
        return false;

    }
}
