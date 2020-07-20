package cn.infocore.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.Client_;
import cn.infocore.entity.Fault;
import cn.infocore.entity.RdsDO;
import cn.infocore.entity.RdsInstanceDO;
import cn.infocore.entity.Vcenter;
import cn.infocore.entity.Virtual_machine;
import cn.infocore.handler.DataArkHandler;
import cn.infocore.handler.ExceptHandler;
import cn.infocore.service.impl.MailServiceImpl;
import cn.infocore.utils.MyDataSource;

/**
 * 检测streamer服务端状态
 */
public class ThreadScanStreamer implements Runnable {
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);
	private static final long split = 365*24 * 60 *60;
	// private static final long split=30;
	// Long timestamp = System.currentTimeMillis();

	// Long stp = 5000L;
	public ThreadScanStreamer() {
		logger.info("Init Data ark offline listener thread.");
	}

	private static class ThreadScanStreamerHolder {
		public static ThreadScanStreamer instance = new ThreadScanStreamer();
	}

	public static ThreadScanStreamer getInstance() {
		return ThreadScanStreamerHolder.instance;
	}

	@Override
	public void run() {
		Map<String, Long> map = null;
		List<String> uuids = null;
		DataArkList.getInstance(); // for all streamer offline and start service not getcache

		while (true) {
			try {
				map = HeartCache.getInstance().getAllCacheList();
				logger.info("Start Scanner data ark offline is or not....data_ark size:" + map.size());
				uuids = new ArrayList<String>();
				if (map.size() > 0) {
					for (Map.Entry<String, Long> entry : map.entrySet()) {
						String uuid = entry.getKey();
						long time = entry.getValue();
						long now = System.currentTimeMillis() / 1000;
						if (now - time > split) {
							// 当前时间-最后更新的时间>3分钟,认为掉线
							logger.info("uuid:" + uuid + " is offline,update database.");
							updateOffLine(uuid, false);
							// 每3分钟发送一次Trap
							logger.info("Collect offline streamer:" + uuid);
							uuids.add(uuid);
						} else {
							logger.info("uuid:" + uuid + " is online,update database.");
							updateOffLine(uuid, true);
						}
					}

					if (uuids.size() > 0) {
						logger.info("Sender snmp server alarm.");
						SnmpTrapSender.run(uuids);
					}
				}

				try {
					Thread.sleep(split * 1000);
				} catch (InterruptedException e) {
					logger.error("ThreadScanStreamer interupted...", e);
				}
			} catch (Exception e) {
				logger.error("ThreadScanStreamer:" + e);
			}
		}
	}

	// 更新数据库中是否离线的标志
	public static void updateOffLine(String uuid, boolean online) {
		// true 在线 false 离线
		long now = System.currentTimeMillis() / 1000;
		String sql = "";
		QueryRunner qr = MyDataSource.getQueryRunner();
		// bug#773->solved: maintain exceptions if online
		List<String> result = getExceptionDb(uuid, qr);
		if ((result != null && result.contains("10") && online) || !online) {
			updateStreamerStatus(uuid, online, qr);
		}

		if (!online) {
			logger.warn("The data ark which uuid:" + uuid + "is offline...");
			// 如果离线，触发邮件报警
			List<Fault> data_ark_fault_list = new LinkedList<Fault>();
			Fault fault = new Fault();
			fault.setTimestamp(now);
			fault.setType(10);
			fault.setData_ark_id(uuid);
			fault.setClient_type(0);
			sql = "select d.name,d.ip,q.user_id from data_ark as d,quota as q where d.id=q.data_ark_id and q.data_ark_id=?";
			String sql1 = "select name,user_id,ip from data_ark where id=?";
			Object[] param1 = { uuid };
			try {
				DataArkDTO data_ark = qr.query(sql, new DataArkHandler(), param1);
				DataArkDTO adminData_ark = qr.query(sql1, new DataArkHandler(), param1);
				if (data_ark == null && adminData_ark != null) {
					data_ark = adminData_ark;
				}

				if (data_ark == null) {
					fault.setData_ark_name("null");
					fault.setData_ark_ip("null");
					fault.setTarget("null");
					fault.setUser_id("null");
					fault.setClient_id("null");
					fault.setData_ark_id("null");
				} else {
					fault.setData_ark_name(data_ark.getName());
					fault.setData_ark_ip(data_ark.getIp());
					fault.setTarget(data_ark.getName());
					fault.setUser_id(data_ark.getUser_id());
					fault.setClient_id(uuid); // add by wxx
					fault.setData_ark_id(uuid);
				}

				data_ark_fault_list.add(fault);
				data_ark.setFaults(data_ark_fault_list);
				List<Client_> clientList = new LinkedList<Client_>();
				List<Vcenter> vcList = new LinkedList<Vcenter>();
				List<Virtual_machine> vmList = new LinkedList<Virtual_machine>();
				List<RdsDO> rdsList = new ArrayList<>();
				List<RdsInstanceDO> rdsInstanceList = new ArrayList<>();
				
	            List<Fault> fault_list_single = new LinkedList<Fault>();
	            fault_list_single.add(fault);
				
				MailServiceImpl.getInstance().notifyCenter(data_ark, clientList, vcList, vmList,rdsList,rdsInstanceList, fault_list_single);
			} catch (Exception e1) {
				logger.error("ThreadScanStreamer:" + e1);
			}
		}
		// MyDataSource.close(connection);
	}

	private static void updateStreamerStatus(String uuid, boolean online, QueryRunner qr) {
		String sql;
		// 更新数据库数据方舟的状态
		sql = "update data_ark set exceptions=? where id=?";

		Object[] param = { online ? "0" : "10", uuid };
		try {
			qr.update(sql, param);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private static List<String> getExceptionDb(String uuid, QueryRunner qr) {
		String selectsql = "select `exceptions` from data_ark where id=?";
		Object[] parm0 = { uuid };

		try {
			List<String> source = qr.execute(selectsql, new ExceptHandler(), parm0);
			if (source != null && !source.isEmpty()) {
				String exceptions = source.get(0);
				if (exceptions!= null && exceptions.contains(";")) {
					String[] resultArray = source.get(0).split(";");
					return new ArrayList<String>(Arrays.asList(resultArray));
				} else {
					List<String> result = new ArrayList<String>();
					result.add(exceptions);
					return result;
				}

			}else {
                return new ArrayList<String>();
            }
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return null;
	}
}
