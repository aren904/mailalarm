package cn.infocore.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.Quota;
import cn.infocore.manager.QuotaManager;
import cn.infocore.mapper.QuotaMapper;
import cn.infocore.service.QuotaService;

@Service
public class QuotaServiceImpl extends ServiceImpl<QuotaMapper, Quota> implements QuotaService {

    private  static final Logger logger = Logger.getLogger(QuotaServiceImpl.class);
    
    private QuotaManager quotaManager;
    
    @Override
	public Quota findByDataArkId(Long dataArkId) {
		return quotaManager.getByDataArkId(dataArkId);
	}

}
