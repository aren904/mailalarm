package cn.infocore.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import cn.infocore.bo.FaultSimple;



@Component
public class OssObjectSetManager  {

    private static final Logger logger = Logger.getLogger(OssObjectSetManager.class);

    public List<FaultSimple> updateList(List<StreamerClouddrmanage.OssObjectSetInfo> ossObjectSetInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (StreamerClouddrmanage.OssObjectSetInfo ossObjectSetInfo : ossObjectSetInfos) {
            List<StreamerClouddrmanage.FaultType> list = ossObjectSetInfo.getStatusList();
            faultList.addAll(listFaults(list,ossObjectSetInfo));
        }
        return faultList;
    }

    List<FaultSimple> listFaults(List<StreamerClouddrmanage.FaultType> faultTypes, StreamerClouddrmanage.OssObjectSetInfo ossObjectSetInfo) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(StreamerClouddrmanage.ClientType.OssObjectSet);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(ossObjectSetInfo.getName());
            faultSimple.setTargetUuid(ossObjectSetInfo.getId());
            faultList.add(faultSimple);
        }
        return faultList;
    }
}
