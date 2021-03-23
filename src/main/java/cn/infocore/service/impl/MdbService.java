package cn.infocore.service.impl;


import java.util.LinkedList;
import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.manager.MetaManager;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.utils.StupidStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.infocore.entity.MdbDO;
import cn.infocore.entity.MdbBackupDO;
import cn.infocore.manager.MetaBackupManager;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.MetaBackupInfo;
import cn.infocore.protobuf.StmStreamerDrManage.MetaInfo;

import static cn.infocore.protobuf.StmStreamerDrManage.*;

@Service
public class MdbService {
    @Autowired
    MetaBackupManager metaBackupManager;
    @Autowired
    MetaManager metaManager;

    public void updateMdbInfo(MetaInfo metaInfo) {
        List<MetaBackupInfo> backupInfos = metaInfo.getBackupListList();
        for (MetaBackupInfo metaBackupInfo : backupInfos) {
            String id = metaBackupInfo.getId();
            Long size = metaBackupInfo.getSize();
            MdbBackupDO mdbBackupDO = new MdbBackupDO();
            mdbBackupDO.setSize(size);
            List<FaultType> faults = metaInfo.getStatusList();
            StringBuilder sb = new StringBuilder();
            for (FaultType fault : faults) {
                int code = fault.getNumber();
                sb.append(code).append(";");
            }
            mdbBackupDO.setExceptions(sb.toString());
            metaBackupManager.updateByRealId(id, mdbBackupDO);
        }
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
        List<FaultSimple> MetaBackupFaultSimpleList = metaBackupManager.updateList(backupListList);

        MdbDO mdbDO = new MdbDO();
        mdbDO.setExceptions(StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes));


        List<FaultSimple> faultsList = listFaults(faultTypes);
        List<String> userIdList = metaManager.getMetaUserIdsById(id);
        faultsList.addAll(MetaBackupFaultSimpleList);
        for (FaultSimple faultSimple : faultsList) {
            faultSimple.setTargetId(id);
            faultSimple.setTargetName(name);
            faultSimple.setUserIds(userIdList);
        }

        faultsList.addAll(MetaBackupFaultSimpleList);

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

}