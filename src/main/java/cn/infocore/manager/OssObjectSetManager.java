package cn.infocore.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dao.OssObjectSetMapper;
import cn.infocore.entity.Fault;
import cn.infocore.entity.OssObjectSetDO;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.OssObjectSetInfo;
import cn.infocore.utils.StupidStringUtil;
@Component
public class OssObjectSetManager extends ServiceImpl<OssObjectSetMapper, OssObjectSetDO>{

    public List<FaultSimple> updateList(List<OssObjectSetInfo> ossObjectSetInfos) {
        LinkedList<FaultSimple> faultList =  new LinkedList<FaultSimple>();

        
        for (OssObjectSetInfo ossObjectSetInfo : ossObjectSetInfos) {
            update(ossObjectSetInfo);
            List<FaultType> list = ossObjectSetInfo.getStatusList();
            faultList.addAll(listFaults(list));
        }
        
        return faultList;
    }

    
    public void update(OssObjectSetInfo ossObjectSetInfo) {
   
        OssObjectSetDO ossObjectSetDO = collectOssObjectSetInfomation(ossObjectSetInfo);
        this.updateByObjestSetId(ossObjectSetDO);
        return;
    }
    
    
    OssObjectSetDO collectOssObjectSetInfomation(OssObjectSetInfo ossObjectSetInfo) {
        
        String id = ossObjectSetInfo.getId();
        String name = ossObjectSetInfo.getName();
        ClientType type =ossObjectSetInfo.getType();
        List<FaultType> faultTypes  =  ossObjectSetInfo.getStatusList();
        Long size = ossObjectSetInfo.getSize();
        Long preoccupationSizeByte = ossObjectSetInfo.getPreoccupationSizeByte();
        String exceptions= StupidStringUtil.parseExceptionsToFaultyTypeString(faultTypes);
        
        OssObjectSetDO ossObjectSetDO = new OssObjectSetDO();
        ossObjectSetDO.setObjectSetId(id);
        ossObjectSetDO.setSize(size);
        ossObjectSetDO.setPreoccupationSize(preoccupationSizeByte);
        ossObjectSetDO.setExceptions(exceptions);

        return ossObjectSetDO;
    }
    
    
    
    void updateByObjestSetId(OssObjectSetDO ossObjectSetDO){

        LambdaQueryWrapper<OssObjectSetDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OssObjectSetDO::getObjectSetId,ossObjectSetDO.getObjectSetId());
        this.update(ossObjectSetDO, queryWrapper);
    }
    
    
    List<FaultSimple>  listFaults(List<FaultType> faultTypes){
        LinkedList<FaultSimple> faultList =  new LinkedList<FaultSimple>();
        if (faultTypes!= null) {
                FaultSimple faultSimple =  new FaultSimple();
                faultSimple.setClientType(ClientType.OssObjectSet);
                faultSimple.setFaultTypes(faultTypes);
        }
        return faultList;
    }
    
}
