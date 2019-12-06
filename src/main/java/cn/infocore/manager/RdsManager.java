package cn.infocore.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.RDSMapper;
import cn.infocore.entity.RDS;
@Component
public class RdsManager extends ServiceImpl<RDSMapper, RDS> {
	
	public void updateByUUIDBatch(List<RDS> rdsList) {

		for (RDS rds : rdsList) {
			updateByUUID(rds);
		}

	}

	public void updateByUUID(RDS rds) {
		
		LambdaUpdateWrapper<RDS> update = new UpdateWrapper<RDS>().lambda().eq(RDS::getRdsId, rds.getRdsId())
				.set(RDS::getExceptions, rds.getExceptions());
		this.update(new RDS(), update);
	}
	
	
	public RDS getByData(String rdsId, String StreamerServerId) {
		
		
		return null;
	}
	
}
