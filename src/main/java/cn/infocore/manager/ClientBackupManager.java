package cn.infocore.manager;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.dto.VirtualMachineDTO;
import cn.infocore.entity.ClientBackup;
import cn.infocore.mapper.ClientBackupMapper;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.utils.ConvertUtils;

@Service
public class ClientBackupManager extends ServiceImpl<ClientBackupMapper, ClientBackup> {
	
	private static final Logger logger = Logger.getLogger(ClientBackupManager.class);
	
	@Autowired
    private AlarmLogManager alarmLogManager;
	
	/**
     * 更新备份关系/实例
     * @param clientBackup
     * @param uuid
     */
    public void updateClientBackup(ClientBackup clientBackup) {
        try {
        	logger.debug("Update client backup:"+clientBackup.toString());
			LambdaQueryWrapper<ClientBackup> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(ClientBackup::getUuid, clientBackup.getUuid());
			this.update(clientBackup, queryWrapper);
		} catch (Exception e) {
			logger.error("Failed to update client backup:"+clientBackup.getUuid(),e);
		}
    }
    
	/**
	 * OSS备份关系转ClientBackup对象
	 * @param ossObjectSetInfo
	 * @return
	 */
	public ClientBackup ConvertOSSClientBackup(StmAlarmManage.OssObjectSetInfo ossObjectSetInfo) {
        ClientBackup clientBackup = new ClientBackup();
        List<StmAlarmManage.FaultType> faultTypeList = ossObjectSetInfo.getStatusList();
        String exceptions = ConvertUtils.convertFaultTypesToString(faultTypeList);
        clientBackup.setUuid(ossObjectSetInfo.getId());
        clientBackup.setSize(ossObjectSetInfo.getSize());
        clientBackup.setName(ossObjectSetInfo.getName());
        clientBackup.setExceptions(exceptions);
        clientBackup.setPreoccupationSize(ossObjectSetInfo.getPreoccupationSizeByte());
        return clientBackup;
    }
	
	/**
	 * RDS备份关系转ClientBackup对象
	 * @param rdsInstanceInfo
	 * @return
	 */
	public ClientBackup ConvertRDSClientBackup(StmAlarmManage.RdsInstanceInfo rdsInstanceInfo) {
        List<StmAlarmManage.FaultType> faultTypeList = rdsInstanceInfo.getStatusList();
        String exceptions = ConvertUtils.convertFaultTypesToString(faultTypeList);
        ClientBackup clientBackup = new ClientBackup();
        clientBackup.setUuid(rdsInstanceInfo.getUuid());
        clientBackup.setSize(rdsInstanceInfo.getSize());
        clientBackup.setName(rdsInstanceInfo.getName());
        clientBackup.setExceptions(exceptions);
        clientBackup.setPreoccupationSize(rdsInstanceInfo.getPreoccupationSizeByte());
        return clientBackup;
    }
	
	/**
	 * ECS备份关系转ClientBackup对象
	 * @param ecsInstanceInfo
	 * @return
	 */
	public ClientBackup ConvertECSClientBackup(StmAlarmManage.EcsInstanceInfo ecsInstanceInfo) {
        List<StmAlarmManage.FaultType> faultTypeList = ecsInstanceInfo.getStatusList();
        String exceptions = ConvertUtils.convertFaultTypesToString(faultTypeList);
        ClientBackup clientBackup = new ClientBackup();
        clientBackup.setUuid(ecsInstanceInfo.getId());
        clientBackup.setSize(ecsInstanceInfo.getSize());
        clientBackup.setName(ecsInstanceInfo.getName());
        clientBackup.setExceptions(exceptions);
        clientBackup.setPreoccupationSize(ecsInstanceInfo.getPreoccupationSizeByte());
        return clientBackup;
    }

	/**
	 * META备份关系转ClientBackup对象
	 * @param ossObjectSetInfo
	 * @return
	 */
    public ClientBackup ConvertMetaClientBackup(StmAlarmManage.MetaBackupInfo metaBackupInfo) {
        List<StmAlarmManage.FaultType> faultTypeList = metaBackupInfo.getStatusList();
        String exceptions = ConvertUtils.convertFaultTypesToString(faultTypeList);
        ClientBackup clientBackup = new ClientBackup();
        clientBackup.setUuid(metaBackupInfo.getId());
        clientBackup.setSize(metaBackupInfo.getSize());
        clientBackup.setName(metaBackupInfo.getName());
        clientBackup.setExceptions(exceptions);
        clientBackup.setPreoccupationSize(metaBackupInfo.getPreoccupationSizeByte());
        return clientBackup;
    }
    
    /**
	 * VM转ClientBackup对象
	 * @param virtualMachineDTO
	 * @return
	 */
	public ClientBackup ConvertVMClientBackup(VirtualMachineDTO vm) {
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
        ClientBackup clientBackup = new ClientBackup();
        clientBackup.setUuid(vm.getUuid());
        clientBackup.setName(vm.getName());
        clientBackup.setPath(vm.getPath());
        clientBackup.setExceptions(vmExceptions);
        
        String version = "UnKnown";
        if (vm.getSystem_Version() == 0) {
            version = "Linux";
        } else if (vm.getSystem_Version() == 1) {
            version = "Windows";
        }
        clientBackup.setOperatingSystem(version);
        return clientBackup;
    }

}
