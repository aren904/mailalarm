package cn.infocore.manager;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dao.MdbDeviceMapper;
import cn.infocore.entity.MdbDeviceDo;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.manager
 * @ClassName: MdbDeviceManager
 * @Author: aren904
 * @Description:
 * @Date: 2021/5/13 10:40
 * @Version: 1.0
 */
@Service
public class MdbDeviceManager  {

//    public void updateByRealId(String id, MdbDeviceDo mdbBackupDO) {
//        LambdaQueryWrapper<MdbDeviceDo> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(MdbDeviceDo::getUuid, id);
//        this.baseMapper.update(mdbBackupDO, queryWrapper);
//    }

    public List<FaultSimple> updateList(List<StmStreamerDrManage.MetaBackupInfo> metaBackupInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (StmStreamerDrManage.MetaBackupInfo metaBackupInfo : metaBackupInfos) {
//            update(metaBackupInfo);
            List<StmStreamerDrManage.FaultType> list = metaBackupInfo.getStatusList();
            faultList.addAll(listFaults(list,metaBackupInfo));
        }
        return faultList;
    }


//    public void update(StmStreamerDrManage.MetaBackupInfo metaBackupInfo) {
//        MdbDeviceDo mdbDeviceDo = collectMetaBackupInfomation(metaBackupInfo);
//        this.updateByObjestSetId(mdbDeviceDo);
//
//    }


//    public MdbDeviceDo collectMetaBackupInfomation(StmStreamerDrManage.MetaBackupInfo metaBackupInfo) {
//        String id = metaBackupInfo.getId();
//        String name = metaBackupInfo.getName();
//        StmStreamerDrManage.ClientType type = metaBackupInfo.getType();
//        List<StmStreamerDrManage.FaultType> faultTypes = metaBackupInfo.getStatusList();
//        Long size = metaBackupInfo.getSize();
//        Long preoccupationSizeByte = metaBackupInfo.getPreoccupationSizeByte();
//        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes);
//        MdbDeviceDo mdbDeviceDo = new MdbDeviceDo();
//        mdbDeviceDo.setUuid(id);
//        mdbDeviceDo.setSize(size);
//        mdbDeviceDo.setType(type.getNumber());
//        mdbDeviceDo.setName(name);
//        mdbDeviceDo.setPreoccupationSize(preoccupationSizeByte);
//        mdbDeviceDo.setExceptions(exceptions);
//
//        return mdbDeviceDo;
//    }


//    public void updateByObjestSetId(MdbDeviceDo mdbDeviceDo) {
//
//        LambdaQueryWrapper<MdbDeviceDo> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(MdbDeviceDo::getUuid, mdbDeviceDo.getUuid());
//        String setid = mdbDeviceDo.getUuid();
//        boolean isDr = checkDrInstance(setid);
//        if (isDr) {
//            mdbDeviceDo.setSize(mdbDeviceDo.getSize());
//            mdbDeviceDo.setPreoccupationSize(mdbDeviceDo.getPreoccupationSize());
//        }
//
//        this.update(mdbDeviceDo,queryWrapper);
//    }


    List<FaultSimple> listFaults(List<StmStreamerDrManage.FaultType> faultTypes, StmStreamerDrManage.MetaBackupInfo metaBackupInfo) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(StmStreamerDrManage.ClientType.MetaDBBackup);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(metaBackupInfo.getName());
            faultSimple.setTargetUuid(metaBackupInfo.getId());
            faultList.add(faultSimple);
        }
        return faultList;
    }


//    public boolean checkDrInstance(String instanceId) {
//        LambdaQueryWrapper<MdbDeviceDo> queryWrapper = new LambdaQueryWrapper<MdbDeviceDo>()
//                .eq(MdbDeviceDo::getUuid, instanceId);
//        MdbDeviceDo mdbDeviceDo = this.getOne(queryWrapper);
//        if (mdbDeviceDo != null) {
//            Integer isDr = mdbDeviceDo.getIsDrEnabled();
//            return isDr != null && isDr > 0;
//        }
//        return false;
//
//    }

}
