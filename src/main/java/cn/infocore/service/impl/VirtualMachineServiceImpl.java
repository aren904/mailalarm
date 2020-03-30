package cn.infocore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.manager.VcenterManager;
import cn.infocore.manager.VirtualMachineManager;
import cn.infocore.service.VirtualMachineService;
@Service
public class VirtualMachineServiceImpl implements VirtualMachineService {
    
    @Autowired
    VcenterManager vcenterManager;
    
    @Autowired
    VirtualMachineManager virtualMachineManager;
    
    @Override
    public List<String> getUserIdListByVirtualMachineId(String id) {
        Integer vCenterDbId = virtualMachineManager.getVcenterDbIdInVirtualMachine(id);
        List<String> userIdList = vcenterManager.getUserIdListByVcenterDbId(vCenterDbId);
        
        return userIdList;
    }
    
    

}
