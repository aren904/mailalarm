//package cn.infocore.manager;
//
//import StmStreamerDrManage.StreamerClouddrmanage;
//import cn.infocore.dao.CloudDeviceMapper;
//import cn.infocore.entity.CloudDeviceDo;
//import cn.infocore.entity.CloudDo;
//import cn.infocore.entity.OssObjectSetDO;
////import cn.infocore.protobuf.StmStreamerDrManage;
//import cn.infocore.service.impl.MailServiceImpl;
//import cn.infocore.utils.StupidStringUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import org.apache.log4j.Logger;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @ProjectName: mailalarm
// * @Package: cn.infocore.manager
// * @ClassName: CloudClientDeviceManager
// * @Author: aren904
// * @Description:
// * @Date: 2021/5/13 11:06
// * @Version: 1.0
// */
//@Service
//public class CloudClientDeviceManager extends ServiceImpl<CloudDeviceMapper, CloudDeviceDo> {
//    private static final Logger logger = Logger.getLogger(CloudClientDeviceManager.class);
//
//    public CloudDeviceDo ReSetOssCloudDevice(StreamerClouddrmanage.OssObjectSetInfo ossObjectSetInfo) {
//        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
//        String name = ossObjectSetInfo.getName();
//        long size = ossObjectSetInfo.getSize();
//        long preoccupationSizeByte = ossObjectSetInfo.getPreoccupationSizeByte();
////       ClientType type = ossObjectSetInfo.getType();
//        StreamerClouddrmanage.ClientType type = ossObjectSetInfo.getType();
//        String id = ossObjectSetInfo.getId();
//        List<StreamerClouddrmanage.FaultType> faultTypeList = ossObjectSetInfo.getStatusList();
//        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
//        cloudDeviceDo.setSize(size);
//        cloudDeviceDo.setPreoccupationSize(Integer.parseInt(String.valueOf(preoccupationSizeByte)));
//        cloudDeviceDo.setExceptions(exceptions);
////        cloudDeviceDo.setType(type.getNumber());
//        cloudDeviceDo.setName(name);
//        cloudDeviceDo.setUuid(id);
//
//        return cloudDeviceDo;
//    }
//
//    public CloudDeviceDo ReSetEcsCloudDevice(StreamerClouddrmanage.EcsInstanceInfo ecsInstanceInfo) {
//        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
//        String name = ecsInstanceInfo.getName();
//        long size = ecsInstanceInfo.getSize();
////        StreamerClouddrmanage.ClientType type = ecsInstanceInfo.getType();
//        String id = ecsInstanceInfo.getId();
//        List<StreamerClouddrmanage.FaultType> faultTypeList = ecsInstanceInfo.getStatusList();
//        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
//        long preoccupationSizeByte = ecsInstanceInfo.getPreoccupationSizeByte();
//        int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
//        cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
//        cloudDeviceDo.setSize(size);
//        cloudDeviceDo.setExceptions(exceptions);
////        cloudDeviceDo.setType(type.getNumber());
//        cloudDeviceDo.setName(name);
//        cloudDeviceDo.setUuid(id);
//
//        return cloudDeviceDo;
//    }
//
//    public CloudDeviceDo ReSetRdsCloudDevice(StreamerClouddrmanage.RdsInstanceInfo rdsInstanceInfo) {
//        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
//        String name = rdsInstanceInfo.getName();
//        long size = rdsInstanceInfo.getSize();
////        StreamerClouddrmanage.ClientType type = rdsInstanceInfo.getType();
//        String id = rdsInstanceInfo.getUuid();
//        List<StreamerClouddrmanage.FaultType> faultTypeList = rdsInstanceInfo.getStatusList();
//        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
//        long preoccupationSizeByte = rdsInstanceInfo.getPreoccupationSizeByte();
//        int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
//        cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
//        cloudDeviceDo.setExceptions(exceptions);
////        cloudDeviceDo.setType(type.getNumber());
//        cloudDeviceDo.setName(name);
//        cloudDeviceDo.setSize(size);
//        cloudDeviceDo.setUuid(id);
//
//        return cloudDeviceDo;
//    }
//
//    public CloudDeviceDo ReSetMetaBackupListCloudDevice(StreamerClouddrmanage.MetaBackupInfo metaBackupInfo){
//        CloudDeviceDo cloudDeviceDo = new CloudDeviceDo();
//        String id = metaBackupInfo.getId();
//        String name = metaBackupInfo.getName();
////        StreamerClouddrmanage.ClientType type = metaBackupInfo.getType();
//        long size = metaBackupInfo.getSize();
//        List<StreamerClouddrmanage.FaultType> faultTypeList = metaBackupInfo.getStatusList();
//        String exceptions = StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypeList);
//        long preoccupationSizeByte = metaBackupInfo.getPreoccupationSizeByte();
//        int preoccupationSizebyte = Integer.parseInt(String.valueOf(preoccupationSizeByte));
//        cloudDeviceDo.setPreoccupationSize(preoccupationSizebyte);
//        cloudDeviceDo.setSize(size);
//        cloudDeviceDo.setExceptions(exceptions);
////        cloudDeviceDo.setType(type.getNumber());
//        cloudDeviceDo.setName(name);
//        cloudDeviceDo.setUuid(id);
//        return cloudDeviceDo;
//    }
//
//    public void updateObjectSetDo(CloudDeviceDo  cloudDeviceDo, String uuid){
//        LambdaQueryWrapper<CloudDeviceDo> cloudDeviceDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        cloudDeviceDoLambdaQueryWrapper.eq(CloudDeviceDo::getUuid,uuid);
//        update(cloudDeviceDo,cloudDeviceDoLambdaQueryWrapper);
////        logger.info(cloudDeviceDo);
//        logger.info("cloudDevice update is accomplished");
//
//    }
//
////    public Boolean checkDrInstance(String setId){
////        LambdaQueryWrapper<CloudDeviceDo> cloudDeviceDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
////        cloudDeviceDoLambdaQueryWrapper.eq(CloudDeviceDo::getUuid,setId);
////        CloudDeviceDo cloudDeviceDo = this.getOne(cloudDeviceDoLambdaQueryWrapper);
////        if(cloudDeviceDo!=null){
////            Integer isDrEnabled = cloudDeviceDo.getIsDrEnabled();
////            return isDrEnabled!=null&&isDrEnabled >0;
////        }
////        return false;
////    }
//}
