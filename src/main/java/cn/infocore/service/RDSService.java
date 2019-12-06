package cn.infocore.service;

import java.util.List;

import cn.infocore.entity.Data_ark;
import cn.infocore.entity.Fault;
import cn.infocore.entity.RDS;
import cn.infocore.entity.RDSInstance;
import cn.infocore.protobuf.StmStreamerDrManage.RdsInfo;

public interface RDSService {

	List<RDS> updateRdsInfo( Data_ark data_ark,List<RdsInfo> rdsInfoList);

	List<Fault> getFault(Data_ark data_ark, List<RdsInfo> rdsInfoList);
	List<RDSInstance> getRDSInstanceListFromSource(Data_ark data_ark, List<RdsInfo> rdsInfoList);
}
