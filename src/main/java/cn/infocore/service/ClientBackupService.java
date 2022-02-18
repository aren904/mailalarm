package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.VirtualMachineDTO;

public interface ClientBackupService {

	public void updateVirtualMachine(List<VirtualMachineDTO> vmList);
	
}
