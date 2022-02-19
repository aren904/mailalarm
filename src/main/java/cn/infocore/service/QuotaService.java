package cn.infocore.service;

import java.util.List;

import cn.infocore.entity.Quota;

public interface QuotaService {

	public List<Quota> findByDataArkId(Long dataArkId);
	
}
