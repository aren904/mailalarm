package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.entity.MdbBackupDO;
import cn.infocore.manager.MetaBackupManager;
import cn.infocore.protobuf.StmStreamerDrManage.MetaBackupInfo;
import cn.infocore.protobuf.StmStreamerDrManage.MetaInfo;

@Service
public class MdbService {
    @Autowired
    MetaBackupManager metaBackupManager;

    public void updateMdbInfo(MetaInfo metaInfo) {

        List<MetaBackupInfo> backupInfos = metaInfo.getBackupListList();
        for (MetaBackupInfo metaBackupInfo : backupInfos) {
            String id = metaBackupInfo.getId();
            Long size = metaBackupInfo.getSize();
            MdbBackupDO mdbBackupDO = new MdbBackupDO();
            mdbBackupDO.setSize(size);
            metaBackupManager.updateByRealId(id, mdbBackupDO);
        }

    }

}