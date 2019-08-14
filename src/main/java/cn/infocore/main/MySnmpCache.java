package cn.infocore.main;


import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import cn.infocore.entity.MySnmp;
import cn.infocore.utils.MyDataSource;

/**
 * Snmp只有唯一的一个值
 */
public class MySnmpCache {
	private static final Logger logger = Logger.getLogger(MySnmpCache.class);
	private MySnmp snmp = null;
	private  boolean inited = false;

	private MySnmpCache() {
		
	}

	private MySnmpCache init() {
		if (this.inited == false||snmp ==null) {

			String sql = "select * from snmp";
			QueryRunner qr = MyDataSource.getQueryRunner();
			try {
				this.snmp = qr.query(sql, new BeanHandler<MySnmp>(MySnmp.class));
				logger.info("Successed to get snmp[Name:]" + snmp.getStation_name() + "[IP]:" + snmp.getStation_ip()
						+ ",[Port:]" + snmp.getStation_port());
			} catch (Exception e) {
				logger.error(e);
			} finally {
				// MyDataSource.close(connection);
			}
			/*
			 * this.snmp=new MySnmp(); snmp.setStation_ip("192.168.11.206");
			 * snmp.setStation_name(" "); snmp.setStation_port(162);
			 * snmp.setWrite_community_name("public"); snmp.setVersion(1);
			 * snmp.setTimeout_ms(1001); snmp.setUpdate_version(3);
			 */
			if (this.snmp != null) {
				this.inited = true;
				try {
					this.logMe();
				} catch (Exception e) {
					// ignore
				}
				
			} else {
				logger.info("snmp is not set");
			}
		}
		return this;
	}

	private static class MySnmpHolder {
		public static MySnmpCache instance = new MySnmpCache();
	}

	public static MySnmpCache getInstance() {
		return MySnmpHolder.instance.init();
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
				logger.info("Prepare to send snmp. name: " + snmp.getStation_name() + "uri:" + snmp.getStation_ip()
						+ ":" + snmp.getStation_port() + ",enabled:" + snmp.getEnabled());
			} catch (NullPointerException e) {
				logger.info("snmp settings error");
			}
		}
		return this.snmp;
	}

	public void logMe() {
		logger.info("-----Current Snmp Info-----");
		logger.info("Station IP:" + this.snmp.getStation_ip());
		logger.info("Station Name:" + this.snmp.getStation_name());
		logger.info("Station Port:" + this.snmp.getStation_port());
		logger.info("Read Community Name:" + this.snmp.getRead_community_name());
		logger.info("Timeout ms:" + this.snmp.getTimeout_ms());
		logger.info("Version:" + this.snmp.getVersion());
		logger.debug("Update Version:" + this.snmp.getUpdate_version());
	}
}
