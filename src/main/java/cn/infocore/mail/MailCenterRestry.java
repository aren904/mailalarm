package cn.infocore.mail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import cn.infocore.entity.Email_alarm;
import cn.infocore.entity.Fault;
import cn.infocore.entity.Quota;
import cn.infocore.handler.ExceptHandler;
import cn.infocore.handler.ExecptHandler;
import cn.infocore.handler.QuotaHandler;
import cn.infocore.utils.MyDataSource;

//内存中维护的邮件注册列表
public class MailCenterRestry implements Center {
	private static final Logger logger = Logger.getLogger(MailCenterRestry.class);
	private Map<String, MailSender> list = null;// 必须线程安全

	private MailCenterRestry() {
		this.list = new ConcurrentHashMap<String, MailSender>();
		// 初始的时候，先从数据库中获取一次
		logger.info("Start collect mail config from database.");
		QueryRunner qr = MyDataSource.getQueryRunner();
		String sql = "select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,sender_password,smtp_address,"
				+ "smtp_port,smtp_authentication,smtp_user_id,smtp_password,ssl_encrypt,receiver_emails,privilege_level "
				+ "from email_alarm,user where email_alarm.user_id=user.id";
		List<Email_alarm> eList = null;
		try {
			eList = qr.query(sql, new BeanListHandler<Email_alarm>(Email_alarm.class));
			if (eList.size() > 0) {
				logger.info("Get mail config count:" + eList.size());
				for (Email_alarm eAlarm : eList) {
					if (eAlarm.getEnabled() == (byte) 0)
						continue;
					this.list.put(eAlarm.getUser_id(), new MailSender(eAlarm));
				}
				logger.info("Collected mail config finished.");
			} else {
				logger.warn("Collected mail config failed.");
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			//MyDataSource.close(connection);
		}
	}

	private static class MailCenterRestryHolder{
		public static MailCenterRestry instance=new MailCenterRestry();
	}
	
	public static MailCenterRestry getInstance() {
		return MailCenterRestryHolder.instance;
	}

	public void addAllMailService(List<Email_alarm> l) {
		for (Email_alarm email_alarm : l) {
			MailSender sender = new MailSender(email_alarm);
			this.list.put(email_alarm.getUser_id(), sender);
		}

	}

	// 更新邮件配置还是使用该接口
	public void addMailService(String name) {
		// 通过查数据库，添加到本地，自己构造MailSender对象
		// 初始的时候，先从数据库中获取一次
		String sql="select user_id,enabled,exceptions,limit_enabled,limit_suppress_time,sender_email,sender_password,smtp_address," + 
				"smtp_port,smtp_authentication,smtp_user_id,smtp_password,ssl_encrypt,receiver_emails,privilege_level " + 
				"from email_alarm,user where email_alarm.user_id=user.id and email_alarm.user_id=?";
		QueryRunner qr = MyDataSource.getQueryRunner();
		Object[] para = { name };
		List<Email_alarm> elList = null;
		try {
			elList = qr.query( sql, new BeanListHandler<Email_alarm>(Email_alarm.class), para);
			for (Email_alarm email_alarm : elList) {
				if (email_alarm.getEnabled() == (byte) 0) {
					if (this.list.containsKey(name)) {
						this.list.remove(name);
					}
					continue;
				}
				this.list.put(name, new MailSender(email_alarm));
			}
		} catch (SQLException e) {
			logger.error("addMailService.", e);
		} finally {
			//MyDataSource.close(connection);
		}
	}

	public void deleteMailService(String name) {
		// 查询数据库，从本地删除
		if (this.list.containsKey(name)) {
			this.list.remove(name);
		}
	}

	public void notifyCenter(Fault... list_fault) throws SQLException {
		logger.info("Start NotifyCenetr inject mailsender.");
		String sql = null;
		Object[] condition=null;
		
		for (Fault fault : list_fault) {
			if (fault.getType()==0) {
				//1.confirm all alarm log for target.
				sql="update alarm_log set user_id=?,processed=1 where data_ark_id=? and target=?";
				condition= new Object[]{fault.getUser_id(),fault.getData_ark_id(),fault.getTarget()};
				//logger.error(fault.getUser_id()+" "+fault.getData_ark_id()+" "+fault.getTarget());
			}else {
				//add by wxx,for one fault to other fault and not confirm.
				//current error
				List<String> currentErrors=new ArrayList<String>();
				QueryRunner qr = MyDataSource.getQueryRunner();
				String excepts="";
				
				if(fault.getClient_type()==0){
					condition=new Object[]{fault.getClient_id()};
					logger.info("Current error condition:"+condition.length+","+condition[0]+",client type:"+fault.getClient_type());
				}else{
					condition=new Object[]{fault.getData_ark_id(),fault.getClient_id()};
					logger.info("Current error condition:"+condition.length+","+condition[0]+","+condition[1]+",client type:"+fault.getClient_type());
				}
				
				//注意这里名称不一致，需要特殊处理
				if(fault.getClient_type()==0){
					sql="select exceptions from data_ark where id=?";
					excepts=qr.query(sql, new ExceptHandler(), condition);
				}else if(fault.getClient_type()==1){
					sql="select execptions from client where data_ark_id=? and id=?";
					excepts=qr.query(sql, new ExecptHandler(), condition);
				}else if(fault.getClient_type()==2){
					sql="select exceptions from vcenter where data_ark_id=? and id=?";
					excepts=qr.query(sql, new ExceptHandler(), condition);
				}else if(fault.getClient_type()==3){
					sql="select exceptions from virtual_machine where data_ark_id=? and id=?";
					excepts=qr.query(sql, new ExceptHandler(), condition);
				}
				
				//current error
				logger.info("excepts:"+excepts);
				if(excepts!=""&&excepts!=null){
					currentErrors.addAll(Arrays.asList(excepts.split(";")));
				}
				
				logger.info("Current error size:"+currentErrors.size()+",fault type:"+fault.getClient_type());
				for(String ex:currentErrors){
					logger.info("Current error:"+ex);
				}
				
				//not confirm error
				sql="select * from alarm_log where data_ark_id=? and target=? and processed=0";
				condition=new Object[]{fault.getData_ark_id(),fault.getTarget()};
				//db error
				logger.info("DB error condition:"+condition.length+","+condition[0]+","+condition[1]);
				qr = MyDataSource.getQueryRunner();
				List<Integer> dbErrors=qr.query(sql, new ColumnListHandler<Integer>("exeception"),condition);
				logger.info("DB error size:"+dbErrors.size());
				for(Integer ex:dbErrors){
					logger.info("DB error:"+ex);
				}
				
				logger.info("start to compare current and db errors.");
				for(Integer type:dbErrors){
					if(!currentErrors.contains(String.valueOf(type))){
						logger.info("current not contains db,confirm it:"+type);
						//2.current not contains db,confirm it.
						sql="update alarm_log set user_id=?,processed=1 where data_ark_id=? and target=?";
						condition= new Object[]{fault.getUser_id(),fault.getData_ark_id(),fault.getTarget()};
					}
				}
				
				for(String type:currentErrors){
					if(!dbErrors.contains(Integer.parseInt(type))){
						logger.info("current is new,insert it:"+type);
						//3.current is new,insert it.
						sql = "insert into alarm_log values(null,?,?,?,?,?,?,?,?,?,?) on duplicate key"
								+ " update user_id=?,timestamp=?,processed=0";
						condition=new Object[] {fault.getTimestamp(),0,0,fault.getType(),fault.getData_ark_id(),
								fault.getData_ark_name(), fault.getData_ark_ip(), fault.getTarget(),0L,fault.getUser_id(),fault.getUser_id(),fault.getTimestamp()};
					}
				}
			}
			
			QueryRunner qr = MyDataSource.getQueryRunner();
			qr.execute(sql, condition);
			
			if (fault.getType() != 0) {
				for (Map.Entry<String, MailSender> entry:this.list.entrySet()) {
					String user=entry.getKey();
					MailSender mailSender=entry.getValue();
					//判断是否属于管理员用户
					Email_alarm conf=mailSender.getConfig();
					if (conf.getPrivilege_level()==0||conf.getPrivilege_level()==1) {
						mailSender.judge(fault,user);
						logger.info("admin or root user start judge...");
					}else {
						sql = "select * from quota where user_id=? and data_ark_id=?";
						Object[] param= {user,fault.getData_ark_id()};
						QueryRunner qRunner = MyDataSource.getQueryRunner();
						List<Quota> quotas = qRunner.query( sql, new QuotaHandler(), param);
						if (!quotas.isEmpty()) {
							//2019年3月11日18:04:13 朱伟添加
							if(fault.getClient_type().intValue()==1||fault.getClient_type().intValue()==2){
								//查询该user_id是否和报警客户端存在关系，即该客户端是否是该用户添加过，添加过则给该用户发送报警邮件
								Long count =findArkIdAndUserIdAndId(fault,user);
								if(count.intValue()==1){
									mailSender.judge(fault,user);
								}
							}else{
								mailSender.judge(fault,user);
							}
							logger.info("commom user start judge...");
						}else {
							logger.warn("email_alarm table has not user_id:"+user+" and data_ark_id:"+fault.getData_ark_id());
						}
					}
				}
			}
		}
		//MyDataSource.close(connection);
	}
	
	protected Long findArkIdAndUserIdAndId(Fault fault,String user){
		QueryRunner qclent = MyDataSource.getQueryRunner();
		String sql="";
		if(fault.getClient_type()==1){
			sql="select count(*) from client where user_id=? and data_ark_id=? and id=?";
		}else if(fault.getClient_type()==2){
			sql="select count(*) from virtual_machine where user_id=? and data_ark_id=? and id=?";
		}
		Object[] param1= {user,fault.getData_ark_id(),fault.getClient_id()};
		try {
			Long count = qclent.query(sql,new ScalarHandler<Long>(),param1);
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
	
	public void updateMailService(String name, Email_alarm sender) {
		// 同理，查询数据库，更新
		// this.list.put(name, sender);
	}

}
