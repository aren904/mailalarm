package cn.infocore.service;

import java.util.List;

import cn.infocore.bo.FaultSimple;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.Fault;
import cn.infocore.entity.RdsDO;
import cn.infocore.entity.RdsInstanceDO;
import cn.infocore.protobuf.StmStreamerDrManage.RdsInfo;

public interface RDSService {

//	List<RdsDO> updateRdsInfo( DataArkDTO data_ark,List<RdsInfo> rdsInfoList);
//
//	List<Fault> getFault(DataArkDTO data_ark, List<RdsInfo> rdsInfoList);
//
//	List<RdsInstanceDO> getRDSInstanceListFromSource(DataArkDTO data_ark, List<RdsInfo> rdsInfoList);

	List<FaultSimple> updateRdsInfoClientList(List<RdsInfo> rdsInfo);
}
