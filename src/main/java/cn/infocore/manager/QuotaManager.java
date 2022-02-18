package cn.infocore.manager;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.Quota;
import cn.infocore.mapper.QuotaMapper;

@Service
public class QuotaManager extends ServiceImpl<QuotaMapper,Quota> {

	/**
	 * 根据数据方舟ID获取配额
	 * @param dataArkId
	 * @return
	 */
	public Quota getByDataArkId(Long dataArkId){
        LambdaQueryWrapper<Quota> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Quota::getData_ark_id,dataArkId);
        return this.getOne(queryWrapper);
    }
	
}
