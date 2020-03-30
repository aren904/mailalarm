package cn.infocore.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.RDSInstanceMapper;
import cn.infocore.entity.RdsInstanceDO;

@Component
public class RDSInstanceManager extends ServiceImpl<RDSInstanceMapper, RdsInstanceDO> {

	public void updateByUUIDBatch(List<RdsInstanceDO> rdsInstanceList) {
		
		for (RdsInstanceDO rdsInstance : rdsInstanceList) {
			patchInstance(rdsInstance);
		}
	}

	public boolean patchInstance(RdsInstanceDO instance) {
		LambdaUpdateWrapper<RdsInstanceDO> updateWrapper = new UpdateWrapper<RdsInstanceDO>().lambda()
				.eq(RdsInstanceDO::getId, instance.getId())
				.set(RdsInstanceDO::getName, instance.getName())
				.set(RdsInstanceDO::getExceptions, instance.getExceptions());
		return this.update(new RdsInstanceDO(), updateWrapper);
	}
}
