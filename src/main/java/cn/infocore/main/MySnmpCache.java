package cn.infocore.main;


import org.apache.log4j.Logger;

import cn.infocore.entity.MySnmp;
import cn.infocore.service.MySnmpService;

/**
 * Snmp只有唯一的一个值
 */
public class MySnmpCache {
	
	private static final Logger logger = Logger.getLogger(MySnmpCache.class);
	
	private MySnmp snmp = null;
	
	private boolean inited = false;
	
	public MySnmpCache() {}
	
	private MySnmpCache init(MySnmpService mySnmpService) {
		if (this.inited == false||snmp ==null) {
			this.snmp=mySnmpService.get();
			
			if (this.snmp != null) {
				this.inited = true;
				try {
					this.logMe();
				} catch (Exception e) {}
			} else {
				logger.info("snmp dose not set");
			}
		}
		return this;
	}

	private static class MySnmpHolder {
		public static MySnmpCache instance = new MySnmpCache();
	}

	public static MySnmpCache getInstance(MySnmpService mySnmpService) {
		return MySnmpHolder.instance.init(mySnmpService);
	}

	// 更新：即需要重新到数据库查询
	public void updateMySnmp() {
		logger.info("Start to update snmp[Name:]" + snmp.getStation_name() + "[IP]:" + snmp.getStation_ip() + ",[Port:]"
				+ snmp.getStation_port());
		MySnmpHolder.instance = new MySnmpCache();
	}

	public synchronized MySnmp getMySnmp() {
		if (snmp != null) {
			try {
				logger.info("Prepare to send snmp.name: " + snmp.getStation_name() + "uri:" + snmp.getStation_ip()
						+ ":" + snmp.getStation_port() + ",enabled:" + snmp.getEnabled());
			} catch (Exception e) {
				logger.info("Failed to getMySnmp.",e);
			}
		}
		return this.snmp;
	}

	public void logMe() {
		logger.debug("-----Current Snmp Info-----");
		logger.debug("Station IP:" + this.snmp.getStation_ip());
		logger.debug("Station Name:" + this.snmp.getStation_name());
		logger.debug("Station Port:" + this.snmp.getStation_port());
		logger.debug("Read Community Name:" + this.snmp.getRead_comm_name());
		logger.debug("Timeout ms:" + this.snmp.getTimeout_ms());
		logger.debug("Version:" + this.snmp.getVersion());
		logger.debug("Update Version:" + this.snmp.getUpdate_version());
	}
}
