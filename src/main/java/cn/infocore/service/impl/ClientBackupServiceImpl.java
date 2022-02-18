package cn.infocore.service.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.AlarmLogDAO;
import cn.infocore.dto.VirtualMachineDTO;
import cn.infocore.entity.ClientBackup;
import cn.infocore.manager.AlarmLogManager;
import cn.infocore.mapper.ClientBackupMapper;
import cn.infocore.service.ClientBackupService;

@Service
public class ClientBackupServiceImpl extends ServiceImpl<ClientBackupMapper, ClientBackup> implements ClientBackupService {

    private static final Logger logger = Logger.getLogger(ClientBackupServiceImpl.class);
    
    @Autowired
    private ClientBackupMapper clientBackupMapper;
    
    @Autowired
    private AlarmLogManager alarmLogManager;

	@Override
	public void updateVirtualMachine(List<VirtualMachineDTO> vmList) {
		for(VirtualMachineDTO vm:vmList) {
			String vmExceptions=vm.getException();
			//收集异常
			if (vmExceptions != null && !vmExceptions.isEmpty()) {
				String[] exceptions = vmExceptions.split(";");
				
				Set<Integer> errorSet = new TreeSet<Integer>();
                for (String string : exceptions) {
                    Integer exception = Integer.getInteger(string);
                    if (exception != null) {
                        errorSet.add(exception);
                    }
                }
                
                //获取未确认异常
                List<Integer> uncheckedErrors = alarmLogManager.findVmUncheckedExceptions(vm.getUuid());
                if (uncheckedErrors != null && !uncheckedErrors.isEmpty()) {
                    errorSet.addAll(uncheckedErrors);
                    StringBuilder sb = new StringBuilder();
                    for (Integer error : errorSet) {
                        sb.append(error).append(";");
                    }
                    if (sb.length() > 1) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    vmExceptions = sb.toString();
                    logger.debug("vm: " + vm.getName()+"|"+vm.getUuid() + ",exceptions:" + vmExceptions);
                }
			}
			
			String version = "UnKnown";
            if (vm.getSystem_Version() == 0) {
                version = "Linux";
            } else if (vm.getSystem_Version() == 1) {
                version = "Windows";
            }
			
			clientBackupMapper.updateVirtualMachineByUuid(vm.getName(),vm.getPath(),vmExceptions,version,vm.getUuid());
		}
	}


}
