package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.VirtualMachineDTO;
import cn.infocore.entity.ClientBackup;
import cn.infocore.manager.ClientBackupManager;
import cn.infocore.service.ClientBackupService;

@Service
public class ClientBackupServiceImpl implements ClientBackupService {

    @Autowired
    private ClientBackupManager clientBackupManager;
    
	@Override
	public void updateVirtualMachine(List<VirtualMachineDTO> vmList) {
		for(VirtualMachineDTO vm:vmList) {
			ClientBackup clientBackup=clientBackupManager.ConvertVMClientBackup(vm);
            clientBackupManager.updateClientBackup(clientBackup);
		}
	}


}
