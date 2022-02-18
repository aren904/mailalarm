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
import cn.infocore.manager.RdsManager;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.RdsService;

@Service
public class RdsServiceImpl implements RdsService {
	
	private static final Logger logger = Logger.getLogger( RdsServiceImpl.class);

    @Autowired
    private RdsManager rdsManager;

    @Autowired
    private ClientManager clientManager;

    @Autowired
    private ClientBackupManager clientBackupManager;

    /**
     * 更新RDS客户端与实例
     */
    @Override
    public void updateRdsClient(StmAlarmManage.RdsInfo rdsClient) {
    	List<StmAlarmManage.RdsInstanceInfo> rdsInstanceInfos = rdsClient.getInstanceListList();
    	List<StmAlarmManage.FaultType> rdsFaults = rdsClient.getStatusList();
    	//收集RDS客户端的异常
        StringBuffer faults = new StringBuffer();
        for (StmAlarmManage.FaultType fault : rdsFaults) {
            faults.append(fault.getNumber()).append(";");
        }
        
        String uuid = rdsClient.getUuid();
        Client client = new Client();
        client.setName(rdsClient.getName())
                .setUuId(uuid)
                .setType(rdsClient.getType().getNumber())
                .setExceptions(faults.toString());
        clientManager.updateClient(uuid, client);
        
        if(faults!=null){
            for (StmAlarmManage.RdsInstanceInfo rdsInstanceInfo : rdsInstanceInfos) {
                ClientBackup clientBackupDo =  clientBackupManager.ConvertRDSClientBackup(rdsInstanceInfo);
                clientBackupManager.updateClientBackup(clientBackupDo,clientBackupDo.getUuid());
            }
        }
    }

    /**
     * 从RDS客户端下收集异常信息
     */
    @Override
    public List<FaultDTO> findFaultFromRdsClients(List<StmAlarmManage.RdsInfo> rdsInfos) {
        List<FaultDTO> faultSimpleList = new LinkedList<>();
        for (StmAlarmManage.RdsInfo rdsInfo : rdsInfos) {
            faultSimpleList.addAll(rdsManager.findFaultFromRdsClient(rdsInfo));
        }
        return faultSimpleList;
    }

}
