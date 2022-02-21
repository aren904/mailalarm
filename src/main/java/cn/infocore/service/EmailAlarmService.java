package cn.infocore.service;

import java.sql.SQLException;
import java.util.List;

import cn.infocore.dto.ClientDTO;
import cn.infocore.dto.DataArkDTO;
import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.dto.Fault;
import cn.infocore.dto.FaultDTO;
import cn.infocore.dto.VCenterDTO;
import cn.infocore.dto.VirtualMachineDTO;

public interface EmailAlarmService {
	
	//添加所有邮件服务
	void addAllMailService(List<EmailAlarmDTO> l);
	
	//添加、更新邮件服务
	void addMailService(String name);
	
	//移除邮件服务
	void deleteMailService(Long userId);
	
	//通知
	void notifyCenter(DataArkDTO data_ark,List<ClientDTO> clientList,List<VCenterDTO> vcList,List<VirtualMachineDTO> vmList,List<Fault> list_fault) throws SQLException;

	//处理异常
	void sendFaults(List<FaultDTO> faults);
}
