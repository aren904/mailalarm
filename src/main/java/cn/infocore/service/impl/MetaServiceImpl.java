package cn.infocore.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.dto.FaultDTO;
import cn.infocore.entity.Client;
import cn.infocore.entity.ClientBackup;
import cn.infocore.manager.ClientBackupManager;
import cn.infocore.manager.ClientManager;
import cn.infocore.manager.MetaManager;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.MetaService;

@Service
public class MetaServiceImpl implements MetaService {
	
	private static final Logger logger = Logger.getLogger(MetaServiceImpl.class);
	
    @Autowired
    private MetaManager metaManager;

    @Autowired
    private ClientManager clientManager;

    @Autowired
    private ClientBackupManager clientBackupManager;

    /**
     * 更新元数据客户端和备份关系
     * @param metaClient
     */
    public void updateMetaClient(StmAlarmManage.MetaInfo metaClient) {
        List<StmAlarmManage.MetaBackupInfo> metaBackupInfos = metaClient.getBackupListList();
        List<StmAlarmManage.FaultType> faultTypes = metaClient.getStatusList();
        //收集客户端的异常
        StringBuilder faults = new StringBuilder();
        for (StmAlarmManage.FaultType faultType : faultTypes) {
        	faults.append(faultType.getNumber()).append(";");
        }

        String uuid = metaClient.getId();
        String name = metaClient.getName();
        Client client = new Client();
        client.setName(name)
                .setUuId(uuid)
                .setType(metaClient.getType().getNumber())
                .setExceptions(faults.toString());
        clientManager.updateClient(uuid, client);
       
        if(metaBackupInfos!=null){
            for (StmAlarmManage.MetaBackupInfo metaBackupInfo : metaBackupInfos) {
                ClientBackup clientBackup = clientBackupManager.ConvertMetaClientBackup(metaBackupInfo);
                clientBackupManager.updateClientBackup(clientBackup,clientBackup.getUuid());
            }
        }
    }

    /**
     * 从元数据客户端下收集异常信息
     */
    public List<FaultDTO> findFaultFromMetaClients(List<StmAlarmManage.MetaInfo> metaClients) {
        List<FaultDTO> faults = new LinkedList<>();
        for (StmAlarmManage.MetaInfo metaInfo : metaClients) {
            faults.addAll(metaManager.findFaultFromMetaClient(metaInfo));
        }
        return faults;
    }

}