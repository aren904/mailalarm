package cn.infocore.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.RDSInstanceMapper;
import cn.infocore.entity.RDSInstance;

@Component
public class RDSInstanceManager extends ServiceImpl<RDSInstanceMapper, RDSInstance> {

	public void updateByUUIDBatch(List<RDSInstance> rdsInstanceList) {
		
		for (RDSInstance rdsInstance : rdsInstanceList) {
			patchInstance(rdsInstance);
		}
	}

	public boolean patchInstance(RDSInstance instance) {
		LambdaUpdateWrapper<RDSInstance> updateWrapper = new UpdateWrapper<RDSInstance>().lambda()
				.eq(RDSInstance::getId, instance.getId())
				.set(RDSInstance::getName, instance.getName())
				.set(RDSInstance::getExceptions, instance.getExceptions());
		return this.update(new RDSInstance(), updateWrapper);
	}
}
