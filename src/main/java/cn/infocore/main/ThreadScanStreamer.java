package cn.infocore.main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import cn.infocore.entity.*;
import cn.infocore.utils.MyDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.handler.DataArkHandler;
import cn.infocore.handler.ExceptHandler;
import cn.infocore.service.impl.MailServiceImpl;
//import cn.infocore.protobuf.StmStreamerDrManage;
/**
 * 检测streamer服务端状态
 */
public class ThreadScanStreamer implements Runnable {
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);
	private static final long split = 3 * 60;
	// private static final long split=30;
	// Long timestamp = System.currentTimeMillis();
//	CaptureDataArkIp captureDataArkIp;

	// Long stp = 5000L;
//	public ThreadScanStreamer(CaptureDataArkIp captureDataArkIp) {
//		this.captureDataArkIp=captureDataArkIp;
//		logger.info("Init Data ark offline listener thread.");
//	}

	public ThreadScanStreamer() {
		logger.info("Init Data ark offline listener thread.");
	}

	@Override
	public void run() {
			Map<String, Long> map = null;
			List<String> uuids = null;
//			DataArkList.getInstance(); // for all streamer offline and start service not getcache
		   DataArkList.getInstance();
			//logger.info("kmj");
			while (true) {
				try {

					map = HeartCache.getInstance().getAllCacheList();
					logger.info("Start Scanner data ark offline is or not....data_ark size:" + map.size());
					uuids = new ArrayList<String>();
//				String ip = ObtainIpFromItself.getIp();
//				String ip = ObtainIpFromItself.getInterfaceAddresses();
//				List<InterfaceAddress> addresses = ObtainIpFromItself.getInterfaceAddresses();
//				logger.info("addresses: " + addresses);
//				String s = addresses.toString();
//				System.out.println(s);
					//	String[] ips = s.split("/");
//				System.out.println(ips[1]);

//				logger.info("ip:"+ips[1]);
//				String uuid1 = captureDataArkIp.getUuidByDataArkIp(ips[1]);
//				logger.info("当前ip的uuid:"+uuid1);
					if (map.size() > 0) {
						for (Map.Entry<String, Long> entry : map.entrySet()) {
							String uuid = entry.getKey();
							//只判断本机uuid的在线离线状态
							//	if (uuid1.equals(uuid)) {
							long time = entry.getValue();
//						logger.info("time:"+time);
							long now = System.currentTimeMillis() / 1000;
//						logger.info("now:"+now);
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
//						SnmpTrapSender.getInstance().run(uuids);
						}
					}


					try {
						Thread.sleep(split * 1000);
					} catch (InterruptedException e) {
						logger.error("ThreadScanStreamer interrupted...", e);
					}
				} catch (Exception e) {
					logger.error("ThreadScanStreamer:" + e);
				}
			}

	}

	private static class ThreadScanStreamerHolder {
		public static ThreadScanStreamer instance = new ThreadScanStreamer();
	}

	public static ThreadScanStreamer getInstance() {
		return ThreadScanStreamerHolder.instance;
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
			logger.warn("The data ark which uuid:" + uuid + " is offline...");
			// 如果离线，触发邮件报警
			List<Fault> data_ark_fault_list = new LinkedList<Fault>();
			Fault fault = new Fault();
			fault.setTimestamp(now);
			fault.setType(10);
			fault.setData_ark_uuid(uuid);
			fault.setClient_type(0);
			sql = "select d.name,d.ip,q.user_id from data_ark as d,quota as q where d.id=q.data_ark_id and q.data_ark_id=?";
//			String sql1 = "select name,user_id,ip from data_ark where id=?";
			String sql1 = "select name,user_uuid,ip from data_ark where uuid=?";
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
					fault.setTarget_name("null");
					fault.setUser_uuid("null");
					fault.setClient_id("null");
					fault.setData_ark_uuid("null");
				} else {
					fault.setData_ark_name(data_ark.getName());
					fault.setData_ark_ip(data_ark.getIp());
					fault.setTarget_name(data_ark.getName());
					fault.setUser_uuid(data_ark.getUser_uuid());
					fault.setClient_id(uuid); // add by wxx
					fault.setData_ark_uuid(uuid);
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
				
//				MailServiceImpl.getInstance().notifyCenter(data_ark, clientList, vcList, vmList,rdsList,rdsInstanceList, fault_list_single);
				MailServiceImpl.getInstance().notifyCenter(data_ark, clientList, vcList, vmList, fault_list_single);
			} catch (Exception e1) {
				logger.error("ThreadScanStreamer:" + e1);
			}
		}
		// MyDataSource.close(connection);
	}

	private static void updateStreamerStatus(String uuid, boolean online, QueryRunner qr) {
		String sql;
		// 更新数据库数据方舟的状态
//		sql = "update data_ark set exceptions=? where id=?";
		sql = "update data_ark set exceptions=? where uuid=?";
		Object[] param = { online ? "0" : "10", uuid };
		try {
			qr.update(sql, param);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private static List<String> getExceptionDb(String uuid, QueryRunner qr) {
//		String selectsql = "select `exceptions` from data_ark where id=?";
		String selectsql = "select `exceptions` from data_ark where uuid=?";
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
