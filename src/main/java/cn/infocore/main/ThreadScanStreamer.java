package cn.infocore.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import cn.infocore.entity.Data_ark;
import cn.infocore.entity.Fault;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.handler.DataArkHandler;
import cn.infocore.utils.MyDataSource;

public class ThreadScanStreamer extends Thread {
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);
	private static final long split=3*60;

	public ThreadScanStreamer() {
		logger.info("Init Data ark offline listener thread.");
	}

	private static class ThreadScanStreamerHolder{
		public static ThreadScanStreamer instance=new ThreadScanStreamer();
	}
	
	public static ThreadScanStreamer getInstance() {
		return ThreadScanStreamerHolder.instance;
	}

	@Override
	public void run() {
		Map<String, Long> map=null;
		while(true) {
			logger.info("Start Scanner data ark offline is or not....");
			map=HeartCache.getInstance().getAllCacheList();
			if (map.size()>0) {
				for (Map.Entry<String, Long> entry:map.entrySet()) {
					String uuid=entry.getKey();
					long time=entry.getValue();
					long now = System.currentTimeMillis() / 1000;
					if (now-time>split) {
						//当前时间-最后更新的时间>3分钟,认为掉线
						logger.info("uuid:"+uuid+" is offline,update database.");
						updateOffLine(uuid,false);
					}else {
						updateOffLine(uuid,true);
					}
					
				}
			}
			try {
				Thread.sleep(split*1000);
			} catch (InterruptedException e) {
				logger.error("ThreadScanStreamer interupted...",e);
			}
		}
	}

	
	// 更新数据库中是否离线的标志
	public static void updateOffLine(String uuid, boolean online) {
		// true 在线 false 离线
		long now = System.currentTimeMillis() / 1000;
		Connection connection=MyDataSource.getConnection();
		String sql="";
		QueryRunner qr=new QueryRunner();
		if (!online) {
			logger.warn("The data ark which uuid:"+uuid+"is offline...");
			//如果离线，触发邮件报警
			Fault fault = new Fault();
			fault.setTimestamp(now);
			fault.setType(10);
			fault.setData_ark_id(uuid);
			fault.setClient_type(0);
			sql="select d.name,d.ip,q.user_id from data_ark as d,quota as q where d.id=q.data_ark_id and q.data_ark_id=?";
			String sql1="select name,user_id,ip from data_ark where id=?";
			Object[] param1= {uuid};
			Data_ark data_ark=null;
			Data_ark adminData_ark=null;
			try {
				data_ark=qr.query(connection, sql, new DataArkHandler(), param1);
				adminData_ark=qr.query(connection, sql1, new DataArkHandler(), param1);
				if(data_ark==null&&adminData_ark!=null){
					data_ark=adminData_ark;
				}
			} catch (SQLException e1) {
				logger.error(e1);
			}
			if (data_ark==null) {
				fault.setData_ark_name("null");
				fault.setData_ark_ip("null");
				fault.setTarget("null");
				fault.setUser_id("null");
			}else {
				fault.setData_ark_name(data_ark.getName());
				fault.setData_ark_ip(data_ark.getIp());
				fault.setTarget(data_ark.getName());
				fault.setUser_id(data_ark.getUser_id());
			}
			try {
				MailCenterRestry.getInstance().notifyCenter(fault);
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		sql = "update data_ark set exceptions=? where id=?";
		Object[] param = { online ? null : "10", uuid };
		try {
			qr.update(connection,sql, param);
		} catch (SQLException e) {
			logger.error(e);
		}
		MyDataSource.close(connection);
	}
}
