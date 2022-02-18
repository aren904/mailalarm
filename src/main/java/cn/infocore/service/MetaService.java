package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

public interface MetaService {

    public void updateMetaClient(StmAlarmManage.MetaInfo metaClient);

    public List<FaultDTO> findFaultFromMetaClients(List<StmAlarmManage.MetaInfo> metaClients);

}
