package cn.infocore.service.impl;

import java.util.ArrayList;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.DateArkMapper;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.DataArkDO;
import cn.infocore.service.DataArkService;

@Service
public class DataArkServiceImpl extends ServiceImpl<DateArkMapper, DataArkDO> implements DataArkService {

    private  static final Logger logger = Logger.getLogger(DataArkServiceImpl.class);

    @Override
    public void update(DataArkDTO data_ark) {

        String id = data_ark.getId();
        String uuid = data_ark.getUuid();
        Long totalPoolSize = data_ark.getTotalCap();
        Long used = data_ark.getUsed_cap();
        String exceptions = data_ark.getExcept();
        String ip = data_ark.getIp();
        Long oracleSpaceSize = data_ark.getTotal_oracle_capacity();
        // Long rdsSpaceSize = data_ark.getTotal_rds_capacity();

        Long cloudVol = data_ark.getCloudVol();
        String name = data_ark.getName();
        Long racUsed = data_ark.getRacUsed();
        Long ecsUsed = data_ark.getEcsUsed();
        Long rdsUsed = data_ark.getRdsUsed();
        Long ossUsed = data_ark.getOssUsed();
        Long metaUsed = data_ark.getMetaUsed();
        int limitClientCount =  (int)data_ark.getLimitClientCount();

        DataArkDO dataArkDO = new DataArkDO();
        dataArkDO.setId(id)
                .setUuid(uuid)
                //.setIp(ip)
                .setLimitClientCount(limitClientCount)
                .setExceptions(exceptions)
                .setTotalPoolSize(totalPoolSize)
                .setUsedPoolSize(used)
                .setTotalOracleSpaceSize(oracleSpaceSize)
                .setTotalCloudSpaceSize(cloudVol)
                .setUsedOracleSpaceSize(racUsed)
                .setUsedEcsSpaceSize(ecsUsed)
                .setUsedRdsSpaceSize(rdsUsed)
                .setUsedOssSpaceSize(ossUsed)
                .setUsedMdbSpaceSize(metaUsed)
                .setName(name)
                ;

//        this.updateById(dataArkDO);
        this.updateByUuid(dataArkDO);
        logger.info("updateDataArk is accomplished");
    }
    @Override
    public String getDataArkNameById(String id) {

//        DataArkDO dataArkDO =  this.getById(id);
        DataArkDO dataArkDO = getDataArkByUuid(id);

        if (dataArkDO != null) {
            return  dataArkDO.getName();

        }

        return null;
    }

    public void updateByUuid(DataArkDO dataArkDO){
        LambdaQueryWrapper<DataArkDO> dataArkDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dataArkDOLambdaQueryWrapper.eq(DataArkDO::getUuid,dataArkDO.getUuid());
        this.update(dataArkDO,dataArkDOLambdaQueryWrapper);
    }

    public DataArkDO getDataArkByUuid(String uuid){
        LambdaQueryWrapper<DataArkDO> dataArkDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dataArkDOLambdaQueryWrapper.eq(DataArkDO::getUuid,uuid);
        DataArkDO dataArkDO = this.getOne(dataArkDOLambdaQueryWrapper);
        return dataArkDO;
    }

}
