package cn.infocore.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.DataArk;
import cn.infocore.mapper.DataArkMapper;

@Component
public class DataArkManager extends ServiceImpl<DataArkMapper,DataArk> {
	
	private static final Logger logger = Logger.getLogger(DataArkManager.class);
	
	/**
     * 匹配uuid更新数据方舟
     * @param dataArkDO
     */
    public void updateByUuid(DataArk dataArk){
        try {
			LambdaQueryWrapper<DataArk> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(DataArk::getUuid,dataArk.getUuid());
			this.update(dataArk,queryWrapper);
		} catch (Exception e) {
			logger.error("Failed to update data_ark:"+dataArk.getUuid(),e);
		}
    }

    /**
     * 根据uuid获取对象
     * @param uuid
     * @return
     */
    public DataArk getDataArkByUuid(String uuid){
        LambdaQueryWrapper<DataArk> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DataArk::getUuid,uuid);
        return this.getOne(queryWrapper);
    }

	public List<String> findIps() {
        List<String> ips = new ArrayList<>();
        List<DataArk> dataArks=this.list(null);
        if (dataArks != null && !dataArks.isEmpty()) {
            for (DataArk dataArk : dataArks) {
                ips.add(dataArk.getIp());
            }
        }
		return ips;
	}
    
}
