package cn.infocore.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.DateArkMapper;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.DataArkDO;
import cn.infocore.service.DataArkService;

@Service
public class DataArkServiceImpl extends ServiceImpl<DateArkMapper, DataArkDO> implements DataArkService {

    @Override
    public void update(DataArkDTO data_ark) {

        String id = data_ark.getId();
        Long totalPoolSize = data_ark.getTotalCap();
        Long used = data_ark.getUsed_cap();
        String exceptions = data_ark.getExcept();

        Long oracleSpaceSize = data_ark.getTotal_oracle_capacity();
        // Long rdsSpaceSize = data_ark.getTotal_rds_capacity();

        Long cloudVol = data_ark.getCloudVol();
        
        Long racUsed = data_ark.getRacUsed();
        Long ecsUsed = data_ark.getEcsUsed();
        Long rdsUsed = data_ark.getRdsUsed();
        Long ossUsed = data_ark.getOssUsed();
        int limitClientCount =  (int)data_ark.getLimitClientCount();
        
        DataArkDO dataArkDO = new DataArkDO();
        dataArkDO.setId(id)
                .setLimitClientCount(limitClientCount)
                .setExceptions(exceptions)
                .setTotalPoolSize(totalPoolSize)
                .setUsedPoolSize(used)
                .setTotalOracleSpaceSize(oracleSpaceSize)
                .setTotalCloudSpaceSize(cloudVol)
                .setUsedOracleSpaceSize(racUsed)
                .setUsedEcsSpaceSize(ecsUsed)
                .setUsedRdsSpaceSize(rdsUsed)
                .setUsedOssSpaceSize(ossUsed);

        this.updateById(dataArkDO);

    }

}
