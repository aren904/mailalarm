package cn.infocore.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.FaultDTO;
import cn.infocore.entity.Client;
import cn.infocore.entity.ClientBackup;
import cn.infocore.manager.ClientBackupManager;
import cn.infocore.manager.ClientManager;
import cn.infocore.manager.EcsManager;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.EcsService;

@Service
public class EcsServiceImpl implements EcsService{
	
    @Autowired
    private EcsManager ecsManager;
    
    @Autowired
    private ClientManager clientManager;

    @Autowired
    private ClientBackupManager clientBackupManager;

    /**
     * 更新ECS客户端与备份关系
     */
    @Override
    public void updateEcsClient(StmAlarmManage.EcsInfo ecsInfo) {
    	//收集客户端的异常
        List<StmAlarmManage.EcsInstanceInfo> ecsInstanceInfos = ecsInfo.getInstanceListList();
        List<StmAlarmManage.FaultType> ecsFaultList = ecsInfo.getStatusList();
        StringBuffer EcsFaultLists = new StringBuffer();
        for (StmAlarmManage.FaultType fault : ecsFaultList) {
            int code = fault.getNumber();
            EcsFaultLists.append(code).append(";");
        }
        
        String uuid = ecsInfo.getId();
        Client client = new Client();
        client.setName(ecsInfo.getName())
                .setUuId(uuid)
                .setType(ecsInfo.getType().getNumber())
                .setExceptions(EcsFaultLists.toString());
        clientManager.updateClient(client);
        
        if(EcsFaultLists!=null){
            for (StmAlarmManage.EcsInstanceInfo ecsInstanceInfo : ecsInstanceInfos) {
                ClientBackup clientBackup = clientBackupManager.ConvertECSClientBackup(ecsInstanceInfo);
                clientBackupManager.updateClientBackup(clientBackup);
            }
        }
    }

    /**
     * 从客户端下收集异常信息
     */
    @Override
    public List<FaultDTO> findFaultFromEcsClients(List<StmAlarmManage.EcsInfo> ecsClients) {
        List<FaultDTO> faults = new LinkedList<>();
        for (StmAlarmManage.EcsInfo ecsInfo : ecsClients) {
        	faults.addAll(ecsManager.findFaultFromEcsClient(ecsInfo));
        }
        return faults;
    }

}
