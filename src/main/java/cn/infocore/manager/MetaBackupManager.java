package cn.infocore.manager;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.OssDO;
import cn.infocore.protobuf.StmStreamerDrManage;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import cn.infocore.dao.MdbBackupMapper;
import cn.infocore.entity.MdbBackupDO;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.MetaBackupInfo;
import cn.infocore.utils.StupidStringUtil;


import java.util.LinkedList;
import java.util.List;

@Service
public class MetaBackupManager extends ServiceImpl<MdbBackupMapper, MdbBackupDO> {


    public void updateByRealId(String id, MdbBackupDO mdbBackupDO) {
        LambdaQueryWrapper<MdbBackupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MdbBackupDO::getBackupId, id);
        this.baseMapper.update(mdbBackupDO, queryWrapper);
    }

    public List<FaultSimple> updateList(List<MetaBackupInfo> metaBackupInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (MetaBackupInfo metaBackupInfo : metaBackupInfos) {
            update(metaBackupInfo);
            List<FaultType> list = metaBackupInfo.getStatusList();
            faultList.addAll(listFaults(list));
        }
        return faultList;
    }


    public void update(MetaBackupInfo metaBackupInfo) {
        MdbBackupDO mdbBackupDO = collectMetaBackupInfomation(metaBackupInfo);
        this.updateByObjestSetId(mdbBackupDO);

    }


    public MdbBackupDO collectMetaBackupInfomation(MetaBackupInfo metaBackupInfo) {
        String id = metaBackupInfo.getId();
        String name = metaBackupInfo.getName();
        ClientType type = metaBackupInfo.getType();
        List<FaultType> faultTypes = metaBackupInfo.getStatusList();
        Long size = metaBackupInfo.getSize();
        Long preoccupationSizeByte = metaBackupInfo.getPreoccupationSizeByte();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes);
        MdbBackupDO mdbBackupDO = new MdbBackupDO();
        mdbBackupDO.setBackupId(id);
        mdbBackupDO.setSize(size);
        mdbBackupDO.setPreoccupationSize(preoccupationSizeByte);
        mdbBackupDO.setExceptions(exceptions);

        return mdbBackupDO;
    }


    public void updateByObjestSetId(MdbBackupDO mdbBackupDO) {

        LambdaQueryWrapper<MdbBackupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MdbBackupDO::getBackupId, mdbBackupDO.getBackupId());
        String setid = mdbBackupDO.getBackupId();
        boolean isDr = checkDrInstance(setid);
        if (isDr) {
            mdbBackupDO.setDrSize(mdbBackupDO.getSize());
            mdbBackupDO.setPreoccupationDrSize(mdbBackupDO.getPreoccupationSize());
        }
        this.update(mdbBackupDO, queryWrapper);
    }


    List<FaultSimple> listFaults(List<FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.MetaDBBackup);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }


    public boolean checkDrInstance(String instanceId) {
        LambdaQueryWrapper<MdbBackupDO> queryWrapper = new LambdaQueryWrapper<MdbBackupDO>()
                .eq(MdbBackupDO::getBackupId, instanceId);
        MdbBackupDO mdbBackupDO = this.getOne(queryWrapper);
        if (mdbBackupDO != null) {
            Integer isDr = mdbBackupDO.getIsDrEnabled();
            return isDr != null && isDr > 0;
        }
        return false;

    }

}
