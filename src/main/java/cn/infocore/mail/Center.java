package cn.infocore.mail;

import java.sql.SQLException;
import java.util.List;
import cn.infocore.entity.Email_alarm;
import cn.infocore.entity.Fault;

public interface Center {
	//添加所有邮件服务
	void addAllMailService(List<Email_alarm> l);
	
	//添加邮件服务
	void addMailService(String name);
	
	//移除邮件服务
	void deleteMailService(String name);
	
	void updateMailService(String name,Email_alarm sender);
	
	//通知
	void notifyCenter (Fault... list_fault) throws SQLException;
}
