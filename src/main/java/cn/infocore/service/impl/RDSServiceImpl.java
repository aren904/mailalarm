package cn.infocore.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.infocore.dao.UserDAO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.Fault;
import cn.infocore.entity.RdsDO;
import cn.infocore.entity.RdsInstanceDO;
import cn.infocore.manager.RDSInstanceManager;
import cn.infocore.manager.RdsManager;
import cn.infocore.protobuf.StmStreamerDrManage.ClientType;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.protobuf.StmStreamerDrManage.RdsInfo;
import cn.infocore.protobuf.StmStreamerDrManage.RdsInstanceInfo;
import cn.infocore.service.RDSService;

@Service
public class RDSServiceImpl implements RDSService {

	@Autowired
	RDSInstanceManager rdsInstanceManager;

	@Autowired
	RdsManager rdsManager;

	@Autowired
	UserDAO userDAO;

	@Override
	public List<RdsDO> updateRdsInfo(DataArkDTO data_ark,List<RdsInfo> rdsInfoList) {
		List<RdsDO> rdsList = getRDSListFromSource(data_ark,rdsInfoList);
		rdsManager.updateByUUIDBatch(rdsList);
		
		List<RdsInstanceDO> rdsInstanceList = getRDSInstanceListFromSource(data_ark,rdsInfoList);

		rdsInstanceManager.updateByUUIDBatch(rdsInstanceList);
		return rdsList;
	}

	List<RdsDO> getRDSListFromSource(DataArkDTO data_ark,List<RdsInfo> rdsInfoList) {
		
		if (rdsInfoList != null && !rdsInfoList.isEmpty()) {
			String dataArkId  = data_ark.getId();
			List<RdsDO> rdsList = new ArrayList<>(rdsInfoList.size());

			for (RdsInfo rdsInfo : rdsInfoList) {

				String name = rdsInfo.getName();
				String rdsId = rdsInfo.getUuid();
				rdsInfo.getType();
				Long preoccupationSizeByte;//TODO ??? not exist in DB?
				List<FaultType> faultyList = rdsInfo.getStatusList();
				RdsDO rds = new RdsDO().setExceptionsWithFaultyTypeList(faultyList)
                				        .setRdsId(rdsId)
                				        .setName(name)
                				        .setDataArkId(dataArkId);
				rdsList.add(rds);
				
			}
			return rdsList;
		}
		return new ArrayList<>(0);
	}

	public List<RdsInstanceDO> getRDSInstanceListFromSource(DataArkDTO data_ark,List<RdsInfo> rdsInfoList) {
		List<RdsInstanceDO> rdsInstanceList = new ArrayList<>();
		if (rdsInfoList != null && !rdsInfoList.isEmpty()) {
			String dataArkId = data_ark.getId();
			for (RdsInfo rdsInfo : rdsInfoList) {
				List<RdsInstanceInfo> rdsInstanceInfoList = rdsInfo.getInstanceListList();

				if (rdsInstanceInfoList != null && !rdsInstanceInfoList.isEmpty()) {

					for (RdsInstanceInfo rdsInstance : rdsInstanceInfoList) {

						String name = rdsInstance.getName();
						String uuid = rdsInstance.getUuid();
						Long size = rdsInstance.getSize();
						  Long preoccupationSizeByte= rdsInstance.getPreoccupationSizeByte();
						rdsInstance.getType();
						List<FaultType> faultTypes = rdsInstance.getStatusList();

						RdsInstanceDO rInstance = new RdsInstanceDO().setExceptionsWithFaultyTypeList(faultTypes)
								.setName(name).setInstanceId(uuid).setSize(size).setPreoccupationSize(preoccupationSizeByte);
						rdsInstanceList.add(rInstance);
					}
				}
			}
		}
		return rdsInstanceList;
	}

	@Override
	public List<Fault> getFault(DataArkDTO data_ark, List<RdsInfo> rdsInfoList) {
		
		Long timestamp = System.currentTimeMillis()/1000L;
		List<Fault> resultList = new ArrayList<>();
		if (rdsInfoList != null && !rdsInfoList.isEmpty()) {
			
			for (RdsInfo rdsInfo : rdsInfoList) {

				List<RdsInstanceInfo> instanceInfos = rdsInfo.getInstanceListList();

				List<Fault> instanceFaults = getFaultFormInstacne(data_ark, instanceInfos);
				resultList.addAll(instanceFaults);
				List<FaultType> faultTypes = rdsInfo.getStatusList();
				if (faultTypes != null && !faultTypes.isEmpty()) {
					for (FaultType faultType : faultTypes) {

						String uuid = rdsInfo.getUuid();
						String name = rdsInfo.getName();

						Fault fault = new Fault();
						fault.setClient_id(uuid);

						fault.setClient_type(ClientType.Rds_VALUE);

						String dataArkId = data_ark.getId();
						fault.setData_ark_id(dataArkId);

						String dataArkIp = data_ark.getIp();

						fault.setData_ark_ip(dataArkIp);

						String dataArkName = data_ark.getName();

						fault.setData_ark_name(dataArkName);

						fault.setTarget(name);
						fault.setTimestamp(timestamp);
						fault.setType(faultType.getNumber());

						String userId = userDAO.getUserIdByRDS(uuid, dataArkId);
						if (userId==null || userId.isEmpty()) {
							userId = "root";
						}
						fault.setUser_id(userId);
						resultList.add(fault);

					}
				}
			}
		}
		

		return resultList;
	}

	private List<Fault> getFaultFormInstacne(DataArkDTO data_ark, List<RdsInstanceInfo> instanceInfos) {
		String dataArkId = data_ark.getId();
		Long timestamp = System.currentTimeMillis()/1000;
		List<Fault> resultList = new ArrayList<>();

		for (RdsInstanceInfo rdsInstanceInfo : instanceInfos) {
			List<FaultType> faultTypes = rdsInstanceInfo.getStatusList();

			if (faultTypes != null && !faultTypes.isEmpty()) {
				for (FaultType faultType : faultTypes) {

					String id = rdsInstanceInfo.getUuid();
					String name = rdsInstanceInfo.getName();

					Fault fault = new Fault();
					fault.setClient_type(ClientType.RdsInstance_VALUE);

					fault.setData_ark_id(dataArkId);

					String dataArkIp = data_ark.getIp();

					fault.setData_ark_ip(dataArkIp);

					String dataArkName = data_ark.getName();

					fault.setData_ark_name(dataArkName);

					fault.setClient_id(id);

					fault.setTarget(name);
					fault.setTimestamp(timestamp);
					fault.setType(faultType.getNumber());
					String userId = userDAO.getUserIdByRDSInstance(id, dataArkId);
					if (userId==null || userId.isEmpty()) {
						userId = "root";
					}
					fault.setUser_id(userId);
					
					resultList.add(fault);
				}

			}

		}

		return resultList;
	}

}
