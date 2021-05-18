package cn.infocore.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.infocore.main.InfoProcessData;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dao.OssObjectSetMapper;
import cn.infocore.entity.OssObjectSetDO;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.OssObjectSetInfo;
import cn.infocore.utils.StupidStringUtil;


@Component
public class OssObjectSetManager  {

    private static final Logger logger = Logger.getLogger(OssObjectSetManager.class);

    public List<FaultSimple> updateList(List<OssObjectSetInfo> ossObjectSetInfos) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        for (OssObjectSetInfo ossObjectSetInfo : ossObjectSetInfos) {
            List<FaultType> list = ossObjectSetInfo.getStatusList();
            faultList.addAll(listFaults(list,ossObjectSetInfo));
        }
        return faultList;
    }

    List<FaultSimple> listFaults(List<FaultType> faultTypes,OssObjectSetInfo ossObjectSetInfo) {
        LinkedList<FaultSimple> faultList = new LinkedList<FaultSimple>();
        if (faultTypes != null) {
            FaultSimple faultSimple = new FaultSimple();
            faultSimple.setClientType(ClientType.OssObjectSet);
            faultSimple.setFaultTypes(faultTypes);
            faultSimple.setTargetName(ossObjectSetInfo.getName());
            faultSimple.setTargetUuid(ossObjectSetInfo.getId());
            faultList.add(faultSimple);
        }
        return faultList;
    }
}
