package cn.infocore.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import cn.infocore.entity.Email_alarm;

//自定义email_log中查询对象的封装
public class Email_logHandler implements ResultSetHandler<List<Email_alarm>>{

	public List<Email_alarm>  handle(ResultSet set) throws SQLException {
		
		List<Email_alarm> list=new LinkedList<Email_alarm>();
		while (set.next()) {
			Email_alarm email=null;
			//未开启邮件报警
			if (set.getByte("enabled")==0) {
				continue;
			}
			email=new Email_alarm();
			email.setEnabled(set.getByte("enabled"));
			email.setExcept(set.getString("exceptions"));
			email.setLimit_enabled(set.getByte("limit_enabled"));
			email.setLimit_suppress_time(set.getLong("limit_suppress_time"));
			email.setSender_email(set.getString("sender_email"));
			email.setSmtp_address(set.getString("smtp_address"));
			email.setSmtp_port(set.getInt("smtp_port"));
			email.setSsl_encrypt(set.getByte("ssl_encrypt"));
			email.setReceiver_emails(set.getString("receiver_emails"));
			email.setSmtp_authentication(set.getByte("smtp_authentication"));
			email.setSmtp_user_id(set.getString("smtp_user_id"));
			email.setStmp_password(set.getString("stmp_password"));
			list.add(email);
		}
		return list;
	}

}
