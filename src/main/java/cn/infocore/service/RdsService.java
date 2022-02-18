package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.FaultDTO;
import cn.infocore.protobuf.StmAlarmManage;

public interface RdsService {

	public void updateRdsClient(StmAlarmManage.RdsInfo rdsClient);

	public List<FaultDTO> findFaultFromRdsClients(List<StmAlarmManage.RdsInfo> rdsInfo);
	
}
