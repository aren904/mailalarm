package cn.infocore.manager;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.Quota;
import cn.infocore.mapper.QuotaMapper;

@Service
public class QuotaManager extends ServiceImpl<QuotaMapper,Quota> {

	/**
	 * 根据数据方舟ID获取配额集合
	 * @param dataArkId
	 * @return
	 */
	public List<Quota> listByDataArkId(Long dataArkId){
        LambdaQueryWrapper<Quota> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Quota::getDataArkId,dataArkId);
        return this.list(queryWrapper);
    }
	
}
