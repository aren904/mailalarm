package cn.infocore.manager;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.ClientBackup;
import cn.infocore.mapper.ClientBackupMapper;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.utils.ConvertUtils;

@Service
public class ClientBackupManager extends ServiceImpl<ClientBackupMapper, ClientBackup> {
	
	private static final Logger logger = Logger.getLogger(ClientBackupManager.class);
	
	/**
     * 更新备份关系/实例
     * @param clientBackup
     * @param uuid
     */
    public void updateClientBackup(ClientBackup clientBackup, String uuid) {
        LambdaQueryWrapper<ClientBackup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientBackup::getUuid, uuid);
        this.update(clientBackup, queryWrapper);
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
   
}
