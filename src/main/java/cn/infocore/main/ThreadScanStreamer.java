package cn.infocore.main;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.apache.log4j.Logger;
import cn.infocore.entity.Fault;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.protobuf.StmStreamerDrManage.FaultType;
import cn.infocore.utils.DBUtils;
import cn.infocore.utils.MyDataSource;

//从DataArkList中取出，每隔一段时间ping一次，保证在线
public class ThreadScanStreamer extends Thread {
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);
	private static final long sleeptime = 10 * 60 * 1000;

	private static volatile ThreadScanStreamer instance = null;
	private Connection connection = null;// 用于更新数据库，Streamer是否离线的连接

	public ThreadScanStreamer() {
		connection = MyDataSource.getConnection();
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

				for (Map.Entry<String, String> entry : map.entrySet()) {
					String uuid = entry.getKey();
					String ip = entry.getValue();
					boolean connected = checkOffLine(ip);
					updateOffLine(uuid, ip, connected);
				}
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
		// true 离线 false 在线
		long now = System.currentTimeMillis() / 1000;
		if (online) {
			Fault fault = new Fault();
			fault.setTimestamp(now);
			fault.setType(FaultType.STREAMER_OFFLINE);
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
		String sql = "update data_ark set exceptions=? where id=?";
		Object[] param = { online ? "10" : "0", uuid };
		DBUtils.executUpdate(connection, sql, param);

	}

}
