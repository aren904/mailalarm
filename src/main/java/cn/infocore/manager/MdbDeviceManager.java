package cn.infocore.manager;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import cn.infocore.dao.MdbDeviceMapper;
import cn.infocore.entity.MdbDeviceDo;
//import cn.infocore.protobuf.StmStreamerDrManage;
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



    public List<FaultSimple> updateList(List<StreamerClouddrmanage.MetaBackupInfo> metaBackupInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (StreamerClouddrmanage.MetaBackupInfo metaBackupInfo : metaBackupInfos) {
            List<StreamerClouddrmanage.FaultType> list = metaBackupInfo.getStatusList();
            faultList.addAll(listFaults(list,metaBackupInfo));
        }
        return faultList;
    }



    List<FaultSimple> listFaults(List<StreamerClouddrmanage.FaultType> faultTypes, StreamerClouddrmanage.MetaBackupInfo metaBackupInfo) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(StreamerClouddrmanage.ClientType.MetaDBBackup);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(metaBackupInfo.getName());
            faultSimple.setTargetUuid(metaBackupInfo.getId());
            faultList.add(faultSimple);
        }
        return faultList;
    }




}
