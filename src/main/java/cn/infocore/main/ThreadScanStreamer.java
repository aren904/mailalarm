package cn.infocore.main;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import cn.infocore.entity.Fault;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.utils.MyDataSource;

//从DataArkList中取出，每隔一段时间ping一次，保证在线
public class ThreadScanStreamer extends Thread {
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);
	private static final long sleeptime = 3 * 60 * 1000;

	private static volatile ThreadScanStreamer instance = null;

	public ThreadScanStreamer() {
		logger.info("Init Data ark offline listener thread.");
	}

	public static ThreadScanStreamer getInstance() {
		if (instance == null) {
			synchronized (ThreadScanStreamer.class) {
				if (instance == null) {
					instance = new ThreadScanStreamer();
				}
			}
		}
		return instance;
	}

	@Override
	public void run() {
		Map<String, String> map = null;
		while (true) {
			try {
				map = DataArkList.getInstance().getData_ark_list();
				if (map.size()>0) {
					logger.info("Successed get data ark list.");
					for (Map.Entry<String, String> entry : map.entrySet()) {
						String uuid = entry.getKey();
						String ip = entry.getValue();
						boolean connected = checkOffLine(ip);
						updateOffLine(uuid, ip, connected);
					}
				}else {
					logger.warn("Failed to get data ark list.");
				}
				logger.info("ThreadScanStreamer sleep 3 minutes.");
				Thread.sleep(sleeptime);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	// 检查是否掉线
	private boolean checkOffLine(String ip) {
		boolean status = false;
		if (ip != null) {
			try {
				status = InetAddress.getByName(ip).isReachable(1000);
			} catch (IOException e) {
				return status;
			}
		}
		return status;
	}

	// 更新数据库中是否离线的标志
	private void updateOffLine(String uuid, String ip, boolean online) {
		// true 在线 false 离线
		long now = System.currentTimeMillis() / 1000;
		if (!online) {
			logger.warn("The data ark which ip:"+ip+"is offline...");
			//如果离线，触发邮件报警
			Fault fault = new Fault();
			fault.setTimestamp(now);
			fault.setType(10);
			fault.setData_ark_id(uuid);
			fault.setData_ark_name("null");
			fault.setData_ark_ip(ip);
			fault.setTarget("null");
			try {
				MailCenterRestry.getInstance().notifyCenter(fault);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Connection connection=MyDataSource.getConnection();
		String sql = "update data_ark set exceptions=? where id=?";
		Object[] param = { online ? "10" : "0", uuid };
		QueryRunner qr=new QueryRunner();
		try {
			qr.update(connection,sql, param);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		MyDataSource.close(connection);
	}

}
