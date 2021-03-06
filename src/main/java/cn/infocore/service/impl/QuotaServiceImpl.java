package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.Quota;
import cn.infocore.manager.QuotaManager;
import cn.infocore.mapper.QuotaMapper;
import cn.infocore.service.QuotaService;

@Service
public class QuotaServiceImpl extends ServiceImpl<QuotaMapper, Quota> implements QuotaService {

    @Autowired
    private QuotaManager quotaManager;
    
    @Override
	public List<Quota> findByDataArkId(Long dataArkId) {
		return quotaManager.listByDataArkId(dataArkId);
	}

}
