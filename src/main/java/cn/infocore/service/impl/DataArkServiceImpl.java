package cn.infocore.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.DataArk;
import cn.infocore.manager.DataArkManager;
import cn.infocore.mapper.DataArkMapper;
import cn.infocore.service.DataArkService;

@Service
public class DataArkServiceImpl extends ServiceImpl<DataArkMapper, DataArk> implements DataArkService {

    private  static final Logger logger = Logger.getLogger(DataArkServiceImpl.class);
    
    @Autowired
    private DataArkManager dataArkManager;
    
    /**
     * 更新数据方舟
     */
    @Override
    public void update(DataArkDTO data_ark) {
        Long ecsUsed = data_ark.getEcsUsed();
        Long rdsUsed = data_ark.getRdsUsed();
        Long ossUsed = data_ark.getOssUsed();
        Long metaUsed = data_ark.getMetaUsed();
        Long cloudUsed = ecsUsed+rdsUsed+ossUsed+metaUsed;
        
        DataArk dataArk = new DataArk();
        dataArk.setUuid(data_ark.getUuid())
                .setUsedCloudSpaceSize(cloudUsed)
                .setExceptions(data_ark.getExcept())
                .setTotalPoolSize(data_ark.getTotal_cap())
                .setUsedPoolSize(data_ark.getUsed_cap())
                .setTotalOracleSpaceSize(data_ark.getTotal_oracle_capacity())
                .setTotalCloudSpaceSize(data_ark.getCloudVol())
                .setUsedOracleSpaceSize(data_ark.getRacUsed())
                .setUsedEcsSpaceSize(ecsUsed)
                .setUsedRdsSpaceSize(rdsUsed)
                .setUsedOssSpaceSize(ossUsed)
                .setUsedMdbSpaceSize(metaUsed)
                .setLimitClientCount(data_ark.getLimitClientCount())
                .setLimitVcenterVmCount(data_ark.getLimitVcenterVmCount());
        
        logger.debug("Update data_ark:"+data_ark.getUuid());
        logger.debug(dataArk.toString());
        dataArkManager.updateByUuid(dataArk);
    }
    
    @Override
	public DataArk findByUuid(String uuid) {
    	return dataArkManager.getDataArkByUuid(uuid);
	}

    /**
     * 获取当前数据方舟的ip列表
     * @return
     */
	@Override
	public List<String> findIps() {
		return dataArkManager.findIps();
	}

	/**
	 * 根据uuid更新指定数据方舟状态：在线离线
	 * @param uuid
	 * @param online 是否在线
	 * @return 
	 */
	@Override
	public void updateDataArkStatus(String uuid, boolean online) {
		DataArk dataArk=dataArkManager.getDataArkByUuid(uuid);
		dataArk.setExceptions(online ? "0" : "10");
		dataArkManager.updateByUuid(dataArk);
	}
    
}
