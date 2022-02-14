package cn.infocore.service;

import java.util.List;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.bo.FaultSimple;
import scmp.proto.alarm.CloudManagerAlarm;
//import cn.infocore.protobuf.StmStreamerDrManage.RdsInfo;

public interface RDSService {

//	List<RdsDO> updateRdsInfo( DataArkDTO data_ark,List<RdsInfo> rdsInfoList);
//
//	List<Fault> getFault(DataArkDTO data_ark, List<RdsInfo> rdsInfoList);
//
//	List<RdsInstanceDO> getRDSInstanceListFromSource(DataArkDTO data_ark, List<RdsInfo> rdsInfoList);
	void ReUpdateRdsClient(StreamerClouddrmanage.RdsInfo rdsClient);
//	void ReUpdateRdsClient(CloudManagerAlarm.RdsInfo rdsClient);

	List<FaultSimple> updateRdsInfoClientList(List<StreamerClouddrmanage.RdsInfo> rdsInfo);
}
