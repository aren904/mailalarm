package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

public interface EcsService {

    public void updateEcsClient(StmAlarmManage.EcsInfo ecsClient);

    public List<FaultDTO> findFaultFromEcsClients(List<StmAlarmManage.EcsInfo> ecsClients);

}
