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
import cn.infocore.manager.OssManager;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.OssService;

@Service
public class OssServiceImpl implements OssService {
	
    @Autowired
    private OssManager ossManager;

    @Autowired
    private ClientManager clientManager;

    @Autowired
    private ClientBackupManager clientBackupManager;
    
    /**
     * 更新OSS客户端与备份关系
     */
    @Override
    public void updateOssClient(StmAlarmManage.OssInfo ossClient) {
        List<StmAlarmManage.OssObjectSetInfo> ossObjectSetInfos = ossClient.getObjListList();
        List<StmAlarmManage.FaultType> ossFaults = ossClient.getStatusList();
        //收集OSS客户端的异常
        StringBuffer faults = new StringBuffer();
        for (StmAlarmManage.FaultType fault : ossFaults) {
        	faults.append(fault.getNumber()).append(";");
        }

        String uuid = ossClient.getUuid();
        Client client = new Client();
        client.setName(ossClient.getName())
                .setUuId(uuid)
                .setType(ossClient.getType().getNumber())
                .setExceptions(faults.toString());
        clientManager.updateClient(client);
        
        if(faults!=null){
            for (StmAlarmManage.OssObjectSetInfo ossObjectSetInfo : ossObjectSetInfos) {
                ClientBackup clientBackup = clientBackupManager.ConvertOSSClientBackup(ossObjectSetInfo);
                clientBackupManager.updateClientBackup(clientBackup);
            }
        }
    }

    /**
     * 从OSS客户端下收集异常信息
     */
    @Override
    public List<FaultDTO> findFaultFromOssClients(List<StmAlarmManage.OssInfo> ossClients) {
        List<FaultDTO> faults =  new LinkedList<FaultDTO>();
        for (StmAlarmManage.OssInfo ossInfo : ossClients) {
        	faults.addAll(ossManager.findFaultFromOssClient(ossInfo));
        }
        return faults;
    }

}
