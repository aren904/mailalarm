package cn.infocore.manager;

import cn.infocore.dao.CloudDeviceMapper;
import cn.infocore.entity.CloudDeviceDo;
import cn.infocore.entity.CloudDo;
import cn.infocore.entity.OssObjectSetDO;
import cn.infocore.protobuf.StmStreamerDrManage;
import cn.infocore.service.impl.MailServiceImpl;
import cn.infocore.utils.StupidStringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.manager
 * @ClassName: CloudClientDeviceManager
 * @Author: aren904
 * @Description:
 * @Date: 2021/5/13 11:06
 * @Version: 1.0
 */
@Service
public class CloudClientDeviceManager extends ServiceImpl<CloudDeviceMapper, CloudDeviceDo> {
    private static final Logger logger = Logger.getLogger(CloudClientDeviceManager.class);

    public CloudDeviceDo ReSetOssCloudDevice(StmStreamerDrManage.OssObjectSetInfo ossObjectSetInfo) {
        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
        String name = ossObjectSetInfo.getName();
        long size = ossObjectSetInfo.getSize();
        StmStreamerDrManage.ClientType type = ossObjectSetInfo.getType();
        String id = ossObjectSetInfo.getId();
        List<StmStreamerDrManage.FaultType> faultTypeList = ossObjectSetInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);

        cloudDeviceDo.setExceptions(exceptions);
        cloudDeviceDo.setType(type.getNumber());
        cloudDeviceDo.setName(name);
        cloudDeviceDo.setUuid(id);

        return cloudDeviceDo;
    }

    public CloudDeviceDo ReSetEcsCloudDevice(StmStreamerDrManage.EcsInstanceInfo ecsInstanceInfo) {
        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
        String name = ecsInstanceInfo.getName();
        long size = ecsInstanceInfo.getSize();
        StmStreamerDrManage.ClientType type = ecsInstanceInfo.getType();
        String id = ecsInstanceInfo.getId();
        List<StmStreamerDrManage.FaultType> faultTypeList = ecsInstanceInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);

        cloudDeviceDo.setExceptions(exceptions);
        cloudDeviceDo.setType(type.getNumber());
        cloudDeviceDo.setName(name);
        cloudDeviceDo.setUuid(id);

        return cloudDeviceDo;
    }

    public CloudDeviceDo ReSetRdsCloudDevice(StmStreamerDrManage.RdsInstanceInfo rdsInstanceInfo) {
        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
        String name = rdsInstanceInfo.getName();
        long size = rdsInstanceInfo.getSize();
        StmStreamerDrManage.ClientType type = rdsInstanceInfo.getType();
        String id = rdsInstanceInfo.getUuid();
        List<StmStreamerDrManage.FaultType> faultTypeList = rdsInstanceInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);

        cloudDeviceDo.setExceptions(exceptions);
        cloudDeviceDo.setType(type.getNumber());
        cloudDeviceDo.setName(name);
        cloudDeviceDo.setUuid(id);

        return cloudDeviceDo;
    }

    public CloudDeviceDo ReSetMetaBackupListCloudDevice(StmStreamerDrManage.MetaBackupInfo metaBackupInfo){
        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
        String id = metaBackupInfo.getId();
        String name = metaBackupInfo.getName();
        StmStreamerDrManage.ClientType type = metaBackupInfo.getType();
        long size = metaBackupInfo.getSize();
        List<StmStreamerDrManage.FaultType> faultTypeList = metaBackupInfo.getStatusList();
        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);

        cloudDeviceDo.setExceptions(exceptions);
        cloudDeviceDo.setType(type.getNumber());
        cloudDeviceDo.setName(name);
        cloudDeviceDo.setUuid(id);
        return cloudDeviceDo;
    }

    public void updateObjectSetDo(CloudDeviceDo  cloudDeviceDo, String uuid, StmStreamerDrManage.ClientType type){
        LambdaQueryWrapper<CloudDeviceDo> cloudDeviceDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cloudDeviceDoLambdaQueryWrapper.eq(CloudDeviceDo::getUuid,uuid).eq(CloudDeviceDo::getType,type.getNumber());
//        String setId = cloudDeviceDo.getUuid();
//        Boolean aBoolean = checkDrInstance(setId);
//        if (aBoolean){
//            cloudDeviceDo.setPreoccupationSize(cloudDeviceDo.getPreoccupationSize());
//            cloudDeviceDo.setSize(cloudDeviceDo.getSize());
//        }
//        this.update(cloudDeviceDo,cloudDeviceDoLambdaQueryWrapper);
        update(cloudDeviceDo,cloudDeviceDoLambdaQueryWrapper);
        logger.info(cloudDeviceDo);
        logger.info("cloudDevice update is accomplished");

    }

//    public Boolean checkDrInstance(String setId){
//        LambdaQueryWrapper<CloudDeviceDo> cloudDeviceDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        cloudDeviceDoLambdaQueryWrapper.eq(CloudDeviceDo::getUuid,setId);
//        CloudDeviceDo cloudDeviceDo = this.getOne(cloudDeviceDoLambdaQueryWrapper);
//        if(cloudDeviceDo!=null){
//            Integer isDrEnabled = cloudDeviceDo.getIsDrEnabled();
//            return isDrEnabled!=null&&isDrEnabled >0;
//        }
//        return false;
//    }
}
