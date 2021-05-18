package cn.infocore.service.impl;


import java.util.LinkedList;
import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.entity.CloudDo;
import cn.infocore.entity.MdbDeviceDo;
import cn.infocore.main.InfoProcessData;
import cn.infocore.manager.CloudClientManager;
import cn.infocore.manager.MdbDeviceManager;
import cn.infocore.manager.MetaManager;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.infocore.entity.MdbDO;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.MetaBackupInfo;
import cn.infocore.protobuf.StmStreamerDrManage.MetaInfo;

import static cn.infocore.protobuf.StmStreamerDrManage.*;

@Service
public class MdbService {
    //    @Autowired
//    MetaBackupManager metaBackupManager;
    @Autowired
    MdbDeviceManager mdbDeviceManager;

    @Autowired
    MetaManager metaManager;

    private static final Logger logger = Logger.getLogger(MdbService.class);


    public void ReUpdateRdsClient(MetaInfo metaClient) {
        String metaClientId = metaClient.getId();
        List<FaultType> MetaFaultList = metaClient.getStatusList();
        StringBuilder metaFaultList = new StringBuilder();
        for (FaultType metaFaultSimple : MetaFaultList) {
            int code = metaFaultSimple.getNumber();
            metaFaultList.append(code).append(";");
        }
        MdbDO mdbDO = new MdbDO();
        mdbDO.setUuid(metaClientId);
        mdbDO.setName(metaClient.getName());
        mdbDO.setType(metaClient.getType().getNumber());//
        mdbDO.setExceptions(metaFaultList.toString());
        Boolean isDr = CheckMdbIsDr(mdbDO);
        if (isDr) {
            mdbDO.setIsDr(1);
        } else {
            mdbDO.setIsDr(0);
        }
        metaManager.updateMetaClientByMdbId(metaClientId, mdbDO);
    }

    public List<FaultSimple> updateMetaClientList(List<MetaInfo> metaClientsList) {
        List<FaultSimple> faultSimpleList = new LinkedList<>();
        for (MetaInfo metaInfo : metaClientsList) {
            faultSimpleList.addAll(updateMetaClient(metaInfo));
        }
        return faultSimpleList;
    }

    public List<FaultSimple> updateMetaClient(MetaInfo metaInfo) {

        String id = metaInfo.getId();
        String name = metaInfo.getName();
        List<FaultType> faultTypes = metaInfo.getStatusList();
        List<MetaBackupInfo> backupListList = metaInfo.getBackupListList();
        List<FaultSimple> MetaBackupFaultSimpleList = mdbDeviceManager.updateList(backupListList);

        MdbDO mdbDO = new MdbDO();
        mdbDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));


        List<FaultSimple> faultsList = listFaults(faultTypes);
        List<String> userIdList = metaManager.getMetaUserIdsById(id);

//        faultsList.addAll(MetaBackupFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetUuid(id);
            faultSimple.setTargetName(name);

        }
        faultsList.addAll(MetaBackupFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setUserUuids(userIdList);
        }

        logger.info(faultsList);
        return faultsList;
    }


    List<FaultSimple> listFaults(List<FaultType> faultTypes) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.MetaDB);
            faultSimple.setFaultTypes(faultTypes);
            faultList.add(faultSimple);
        }
        return faultList;
    }


    public Boolean CheckMdbIsDr(MdbDO mdbDo) {
        LambdaQueryWrapper<MdbDO> mdbDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        mdbDOLambdaQueryWrapper.eq(MdbDO::getUuid, mdbDo.getUuid());
        MdbDO mdbDO = metaManager.getOne(mdbDOLambdaQueryWrapper);
        if (mdbDO != null) {
            Integer isDr = mdbDo.getIsDr();
            return isDr != null && isDr > 0;
        }
        return false;
    }
}