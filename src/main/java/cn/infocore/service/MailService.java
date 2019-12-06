package cn.infocore.service;

import java.sql.SQLException;
import java.util.List;

import cn.infocore.entity.Client_;
import cn.infocore.entity.Data_ark;
import cn.infocore.entity.Email_alarm;
import cn.infocore.entity.Fault;
import cn.infocore.entity.RDS;
import cn.infocore.entity.RDSInstance;
import cn.infocore.entity.Vcenter;
import cn.infocore.entity.Virtual_machine;

public interface MailService {
	
	//添加所有邮件服务
	void addAllMailService(List<Email_alarm> l);
	
	//添加邮件服务
	void addMailService(String name);
	
	//移除邮件服务
	void deleteMailService(String name);
	
	void updateMailService(String name,Email_alarm sender);
	
	//通知
	void notifyCenter (Data_ark data_ark,List<Client_> clientList,List<Vcenter> vcList,List<Virtual_machine> vmList,List<RDS> rdsList, List<RDSInstance> rdsInstances,Fault... list_fault) throws SQLException;
}
