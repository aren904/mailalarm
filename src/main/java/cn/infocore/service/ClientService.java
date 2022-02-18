package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.VCenterDTO;

public interface ClientService {

	public List<Long> findUserIdsByUuid(String uuid);

	public void updateClient(List<ClientDTO> clientList);

	public void updateVCenter(List<VCenterDTO> vcList);

}
