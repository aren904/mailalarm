package cn.infocore.manager;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dao.VirtualMachineMapper;
import cn.infocore.entity.VirtualMachineDO;

@Service
public class VirtualMachineManager extends ServiceImpl<VirtualMachineMapper, VirtualMachineDO> {
    
    
    public Integer getVcenterDbIdInVirtualMachine(String id){
        VirtualMachineDO virtualMachineDO =  this.getById(id);
        Integer vCenterdbId = virtualMachineDO.getVCenterId();
        
        return vCenterdbId;
        
    }
}
