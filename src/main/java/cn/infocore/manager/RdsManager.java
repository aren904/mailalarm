package cn.infocore.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cn.infocore.entity.MdbDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.RDSMapper;
import cn.infocore.entity.RdsDO;
@Component
public class RdsManager extends ServiceImpl<RDSMapper, RdsDO> {
	
	public void updateByUUIDBatch(List<RdsDO> rdsList) {

		for (RdsDO rds : rdsList) {
			updateByUUID(rds);
		}

	}

	public void updateByUUID(RdsDO rds) {
		
		LambdaUpdateWrapper<RdsDO> update = new UpdateWrapper<RdsDO>().lambda().eq(RdsDO::getRdsId, rds.getRdsId())
				.set(RdsDO::getExceptions, rds.getExceptions());
		this.update(new RdsDO(), update);
	}
	
	
	public RdsDO getByData(String rdsId, String StreamerServerId) {
		
		
		return null;
	}

	public List<String> getRdsUserIdsById(String uuid) {
		LambdaQueryWrapper<RdsDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(RdsDO::getId, uuid);
		LinkedList<String> userIdList = new LinkedList<>();

		Collection<RdsDO> rdsDOS = this.list(lambdaQueryWrapper);
		for (RdsDO rdsDO : rdsDOS) {
			userIdList.add(rdsDO.getUserId());
		}

		return userIdList;
	}

	
}
