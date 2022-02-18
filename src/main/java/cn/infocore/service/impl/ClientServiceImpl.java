package cn.infocore.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.VCenterDTO;
import cn.infocore.entity.Client;
import cn.infocore.manager.ClientManager;
import cn.infocore.mapper.ClientMapper;
import cn.infocore.service.ClientService;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    private static final Logger logger = Logger.getLogger(ClientServiceImpl.class);
    
    @Autowired
    private ClientMapper clientMapper;
    
    @Autowired
    private ClientManager clientManager;

	@Override
	public List<Long> findUserIdsByUuid(String uuid) {
		return clientManager.findUserIdsByUuid(uuid);
	}

	@Override
	public void updateClient(List<ClientDTO> clientList) {
		for(ClientDTO client:clientList) {
			clientMapper.updateClientByUuid(client.getName(),client.getIps(),client.getExcept(),client.getSystem_Version(),client.getUuid());
		}
	}

	@Override
	public void updateVCenter(List<VCenterDTO> vcList) {
		for(VCenterDTO vc:vcList) {
			clientMapper.updateVCenterByUuid(vc.getName(),vc.getIps(),vc.getException(),vc.getUuid());
		}
	}
    

}
