package cn.infocore.mail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;
import cn.infocore.entity.Email_alarm;
import cn.infocore.entity.Fault;
import cn.infocore.entity.Quota;
import cn.infocore.utils.MyDataSource;
import cn.infocore.handler.QuotaHandler;

//内存中维护的邮件注册列表
public class MailCenterRestry implements Center {
	private static final Logger logger = Logger.getLogger(MailCenterRestry.class);
	private static volatile MailCenterRestry instance = null;
	private Map<String, MailSender> list = null;// 必须线程安全

	private MailCenterRestry() {
		this.list = new ConcurrentHashMap<String, MailSender>();
		Connection connection = MyDataSource.getConnection();
		// 初始的时候，先从数据库中获取一次
		logger.info("Start collect mail config from database.");
		QueryRunner qr = new QueryRunner();
		String sql = "select * from email_alarm";
		List<Email_alarm> eList = null;
		try {
			eList = qr.query(connection, sql, new BeanListHandler<Email_alarm>(Email_alarm.class));
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
			MyDataSource.close(connection);
		}
	}

	public static MailCenterRestry getInstance() {
		if (instance == null) {
			synchronized (MailCenterRestry.class) {
				if (instance == null) {
					instance = new MailCenterRestry();
				}
			}
		}
		return instance;
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
		Connection connection = MyDataSource.getConnection();
		// 初始的时候，先从数据库中获取一次
		String sql = "select * from email_alarm where user_id=?";
		QueryRunner qr = new QueryRunner();
		Object[] para = { name };
		List<Email_alarm> elList = null;
		try {
			elList = qr.query(connection, sql, new BeanListHandler<Email_alarm>(Email_alarm.class), para);
			for (Email_alarm email_alarm : elList) {
				if (email_alarm.getEnabled() == (byte) 0)
					continue;
				this.list.put(name, new MailSender(email_alarm));
			}
		} catch (SQLException e) {
			logger.error("addMailService.", e);
		} finally {
			MyDataSource.close(connection);
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
		Connection connection = MyDataSource.getConnection();
		for (Fault fault : list_fault) {
			sql = "insert into alarm_log values(null,?,?,?,?,?,?,?,?) on duplicate key"
					+ " update timestamp=?,processed=?";
			Object[] condition = { fault.getTimestamp(), 0, fault.getType(), fault.getData_ark_id(),
					fault.getData_ark_name(), fault.getData_ark_ip(), fault.getTarget(), 0L, fault.getTimestamp(),
					fault.getType() == 0 ? 1 : 0 };
			QueryRunner qr = new QueryRunner();
			qr.execute(connection, sql, condition);
			if (fault.getType() != 0) {
				for (Map.Entry<String, MailSender> entry:this.list.entrySet()) {
					String user=entry.getKey();
					MailSender mailSender=entry.getValue();
					if (user.equalsIgnoreCase("admin")||user.equalsIgnoreCase("root")) {
						mailSender.judge(fault,user);
						logger.info("admin or root user start judge...");
					}else {
						sql = "select * from quota where user_id=? and data_ark_id=?";
						Object[] param= {user,fault.getData_ark_id()};
						QueryRunner qRunner = new QueryRunner();
						List<Quota> quotas = qRunner.query(connection, sql, new QuotaHandler(), param);
						if (!quotas.isEmpty()) {
							mailSender.judge(fault,user);
							logger.info("commom user start judge...");
						}else {
							logger.warn("email_alarm table has not user_id:"+user+" and data_ark_id:"+fault.getData_ark_id());
						}
					}
				}
			}
		}
		MyDataSource.close(connection);
	}

	public void updateMailService(String name, Email_alarm sender) {
		// 同理，查询数据库，更新
		// this.list.put(name, sender);
	}
}
