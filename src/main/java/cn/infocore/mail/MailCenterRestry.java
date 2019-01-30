package cn.infocore.mail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import cn.infocore.entity.Email_alarm;
import cn.infocore.entity.Fault;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.utils.DBUtils;
import cn.infocore.utils.MyDataSource;


//内存中维护的邮件注册列表
public class MailCenterRestry implements Center{
	private static final Logger logger=Logger.getLogger(MailCenterRestry.class);
	private static volatile MailCenterRestry instance=null;
	private Map<String, MailSender> list=null;//必须线程安全
	private Connection connection;

	private MailCenterRestry() {
		this.list=new ConcurrentHashMap<String,MailSender>();
		connection=MyDataSource.getConnection();
		//初始的时候，先从数据库中获取一次
		logger.info("Start collect mail config from database.");
		String sql="select * from email_alarm";
		ResultSet set=DBUtils.executQuery(connection, sql, null);
		try {
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
				this.list.put(set.getString("user_id"), new MailSender(email));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Collected mail config finished.");
	}
	
	
	public static MailCenterRestry getInstance() {
		if (instance==null) {
			synchronized (MailCenterRestry.class) {
				if (instance==null) {
					instance=new MailCenterRestry();
				}
			}
		}
		return instance;
	}
	
	
	public void addAllMailService(List<Email_alarm> l) {
		for (Email_alarm email_alarm:l) {
			MailSender sender=new MailSender(email_alarm);
			this.list.put(email_alarm.getUser_id(),sender);
		}
		
	}
	
	public void addMailService(String name) {
		//通过查数据库，添加到本地，自己构造MailSender对象
		connection=MyDataSource.getConnection();
		//初始的时候，先从数据库中获取一次
		String sql="select * from email_alarm where user_id=?";
		String[] para= {name};
		ResultSet set=DBUtils.executQuery(connection, sql, para);
		try {
			while(set.next()) {
				Email_alarm email=new Email_alarm();
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
				this.list.put(name, new MailSender(email));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void deleteMailService(String name) {
		//查询数据库，从本地删除
		if (this.list.containsKey(name)) {
			this.list.remove(name);
		}
	}


	public void notifyCenter(Fault... list_fault) throws SQLException {
		//1.通过对应的用户，找到对应的邮件报警配置，就不用通知所有的用户了
		//2.如果对应的用户没有设置邮件报警，则跳过
		/*for (Map.Entry<String, MailSender> entry:this.list.entrySet()) {
			entry.getValue().send(protobuf);
		}*/
		/*String user=protobuf.getUuid();
		MailSender sender=null;
		if (this.list.containsKey(user)) {
			sender=this.list.get(user);
			//sender.s
		}*/
		String sql=null;
		Connection connection=MyDataSource.getConnection();
		for (Fault fault:list_fault) {
				sql="insert into alarm_log(timestamp,processed,exeception,data_ark_id,data_ark_name,data_ark_ip,target,last_alarm_timestamp) values(?,?,?,?,?,?,?) on duplicate key"
				+ " update timestamp=values(?),processed=values(?)";
				Object[] condition = {fault.getTimestamp(),0,fault.getType(),fault.getData_ark_id(),fault.getData_ark_name(),
					fault.getData_ark_ip(),fault.getTarget(),0L,fault.getTimestamp(),fault.getType()==FaultType.NORMAL?1:0};
				DBUtils.executUpdate(connection, sql, condition);
				if (fault.getType()!=FaultType.NORMAL) {
					for (MailSender mailSender:this.list.values()) {
						Email_alarm email_alarm=mailSender.getConfig();
						sql="select * from quota where user_id=? and data_ark_id=?";
						String[] param= {email_alarm.getUser_id(),fault.getData_ark_id()};
						ResultSet resultSet=DBUtils.executQuery(connection, sql, param);
						if (resultSet.next()) {
							mailSender.judge(fault);						
						}
					}
				}
		}
		MyDataSource.close(connection);
	}
	
	public void updateMailService(String name, Email_alarm sender) {
		//同理，查询数据库，更新
		//this.list.put(name, sender);
	}
}
