//package cn.infocore.manager;
//
//import cn.infocore.bo.FaultSimple;
//import cn.infocore.dao.MdbDeviceMapper;
//import cn.infocore.entity.MdbDeviceDo;
//import cn.infocore.entity.OssDO;
//import cn.infocore.protobuf.StmStreamerDrManage;
//import org.springframework.stereotype.Service;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//
//import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
//import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
//import cn.infocore.protobuf.StmStreamerDrManage.MetaBackupInfo;
//import cn.infocore.utils.StupidStringUtil;
//
//
//import java.util.LinkedList;
//import java.util.List;
//
//@Service
//public class MetaBackupManager extends ServiceImpl<MdbDeviceMapper,MdbDeviceDo> {
//
//
//    public void updateByRealId(String id, MdbDeviceDo mdbBackupDO) {
//        LambdaQueryWrapper<MdbDeviceDo> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(MdbDeviceDo::getUuid, id);
//        this.baseMapper.update(mdbBackupDO, queryWrapper);
//    }
//
//    public List<FaultSimple> updateList(List<MetaBackupInfo> metaBackupInfos) {
//        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
//        for (MetaBackupInfo metaBackupInfo : metaBackupInfos) {
//            update(metaBackupInfo);
//            List<FaultType> list = metaBackupInfo.getStatusList();
//            faultList.addAll(listFaults(list));
//        }
//        return faultList;
//    }
//
//
//    public void update(MetaBackupInfo metaBackupInfo) {
//        MdbDeviceDo mdbDeviceDo = collectMetaBackupInfomation(metaBackupInfo);
//        this.updateByObjestSetId(mdbDeviceDo);
//
//    }
//
//
//    public MdbDeviceDo collectMetaBackupInfomation(MetaBackupInfo metaBackupInfo) {
//        String id = metaBackupInfo.getId();
//        String name = metaBackupInfo.getName();
//        ClientType type = metaBackupInfo.getType();
//        List<FaultType> faultTypes = metaBackupInfo.getStatusList();
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
//
//
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
////        this.update(mdbDeviceDo, queryWrapper);
//        this.update(mdbDeviceDo,queryWrapper);
//    }
//
//
//    List<FaultSimple> listFaults(List<FaultType> faultTypes) {
//        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
//        if (faultTypes != null) {
//            FaultSimple faultSimple = new FaultSimple();
//            faultSimple.setClientType(ClientType.MetaDBBackup);
//            faultSimple.setFaultTypes(faultTypes);
//            faultList.add(faultSimple);
//        }
//        return faultList;
//    }
//
//
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
//
//}
