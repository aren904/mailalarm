package cn.infocore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.infocore.entity.Client;

public interface ClientMapper extends BaseMapper<Client> {
	
	public void updateClientByUuid(String name, String ips, String exception, String system_Version, String uuid);

	public void updateVCenterByUuid(String name, String ips, String exception, String uuid);


}
