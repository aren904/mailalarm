package cn.infocore.manager;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.dao.ClientBackupMapper;
import cn.infocore.entity.ClientBackupDo;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ClientBackupManager extends ServiceImpl<ClientBackupMapper, ClientBackupDo> {
    @Autowired
    UserManager userManager;


    private static final Logger logger = Logger.getLogger(ClientBackupManager.class);

    public void updateObjectSetDo(ClientBackupDo clientBackupDo, String objectSetId) {
        LambdaQueryWrapper<ClientBackupDo> clientDeviceDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clientDeviceDoLambdaQueryWrapper.eq(ClientBackupDo::getUuid, objectSetId);
        update(clientBackupDo, clientDeviceDoLambdaQueryWrapper);
        logger.info("update ClientBackupDo is accomplished");
    }

    public ClientBackupDo ResetClientBackup(StreamerClouddrmanage.RdsInstanceInfo rdsInstanceInfo) {
        ClientBackupDo clientBackupDo = new ClientBackupDo();
        List<StreamerClouddrmanage.FaultType> faultTypeList = rdsInstanceInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
        long preoccupationSizeByte = rdsInstanceInfo.getPreoccupationSizeByte();
        int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));

        String name = rdsInstanceInfo.getName();
        long size = rdsInstanceInfo.getSize();
        String uuid = rdsInstanceInfo.getUuid();
//        StreamerClouddrmanage.FaultType faultType = rdsInstanceInfo.getStatus();

        clientBackupDo.setUuid(uuid);
        clientBackupDo.setSize(size);
        clientBackupDo.setName(name);
        clientBackupDo.setExceptions(exceptions);
        clientBackupDo.setPreoccupationSize(preoccupationSizebyte);
        return clientBackupDo;
    }


    public ClientBackupDo ResetClientBackup(StreamerClouddrmanage.OssObjectSetInfo ossObjectSetInfo) {
        ClientBackupDo clientBackupDo = new ClientBackupDo();
        String name = ossObjectSetInfo.getName();
        long size = ossObjectSetInfo.getSize();
        String uuid = ossObjectSetInfo.getId();
        long preoccupationSizeByte = ossObjectSetInfo.getPreoccupationSizeByte();
        List<StreamerClouddrmanage.FaultType> faultTypeList = ossObjectSetInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
        int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
        clientBackupDo.setUuid(uuid);
        clientBackupDo.setSize(size);
        clientBackupDo.setName(name);
        clientBackupDo.setExceptions(exceptions);
        clientBackupDo.setPreoccupationSize(preoccupationSizebyte);
        return clientBackupDo;
    }

    public ClientBackupDo ResetClientBackup(StreamerClouddrmanage.EcsInstanceInfo ecsInstanceInfo) {
        ClientBackupDo clientBackupDo = new ClientBackupDo();
        List<StreamerClouddrmanage.FaultType> faultTypeList = ecsInstanceInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
        long preoccupationSizeByte = ecsInstanceInfo.getPreoccupationSizeByte();
        int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));

        String name = ecsInstanceInfo.getName();
        long size = ecsInstanceInfo.getSize();
        String uuid = ecsInstanceInfo.getId();

        clientBackupDo .setUuid(uuid);
        clientBackupDo .setSize(size);
        clientBackupDo .setName(name);
        clientBackupDo .setExceptions(exceptions);
        clientBackupDo .setPreoccupationSize(preoccupationSizebyte);
        return clientBackupDo ;
    }

    public ClientBackupDo ResetClientBackup(StreamerClouddrmanage.MetaBackupInfo metaBackupInfo) {
        ClientBackupDo clientBackupDo = new ClientBackupDo();
        List<StreamerClouddrmanage.FaultType> faultTypeList = metaBackupInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
        long preoccupationSizeByte = metaBackupInfo.getPreoccupationSizeByte();
        int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
        String name = metaBackupInfo.getName();
        long size = metaBackupInfo.getSize();
        String uuid = metaBackupInfo.getId();

        clientBackupDo.setUuid(uuid);
        clientBackupDo.setSize(size);
        clientBackupDo.setName(name);
        clientBackupDo.setExceptions(exceptions);
        clientBackupDo.setPreoccupationSize(preoccupationSizebyte);
        return clientBackupDo;
    }
}
