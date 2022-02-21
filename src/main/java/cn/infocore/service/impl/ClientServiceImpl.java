package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.VCenterDTO;
import cn.infocore.entity.Client;
import cn.infocore.manager.ClientManager;
import cn.infocore.service.ClientService;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientManager clientManager;

	@Override
	public List<Long> findUserIdsByUuid(String uuid) {
		return clientManager.findUserIdsByUuid(uuid);
	}

	@Override
	public void updateClient(List<ClientDTO> clientList) {
		for(ClientDTO clientDto:clientList) {
			Client client = new Client();
			client.setExceptions(clientDto.getExcept()).setOperationSystem(clientDto.getSystem_Version()).setUuId(clientDto.getUuid());
			if(clientDto.getIps()!=null&&clientDto.getIps()!="") {
				 client.setIps(clientDto.getIps());
			}
			clientManager.updateClient(client);
		}
	}

	@Override
	public void updateVCenter(List<VCenterDTO> vcList) {
		for(VCenterDTO vcDto:vcList) {
			Client client = new Client();
			client.setExceptions(vcDto.getException()).setUuId(vcDto.getUuid());
			if(vcDto.getIps()!=null&&vcDto.getIps()!="") {
				 client.setIps(vcDto.getIps());
			}
			clientManager.updateClient(client);
		}
	}
    

}
