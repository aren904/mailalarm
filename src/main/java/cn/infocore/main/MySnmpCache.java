package cn.infocore.main;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import cn.infocore.entity.MySnmp;
import cn.infocore.utils.MyDataSource;

/**
 * Snmp只有唯一的一个值
 */
public class MySnmpCache {
	private static final Logger logger=Logger.getLogger(MySnmpCache.class);
	private MySnmp snmp=null;

	private MySnmpCache(){
		String sql = "select * from snmp";
		QueryRunner qr=MyDataSource.getQueryRunner();
		try {
			this.snmp=qr.query(sql,new BeanHandler<MySnmp>(MySnmp.class));
			logger.info("Successed to get snmp[Name]:"+snmp.getStation_ip()+",[Port:]"+snmp.getStation_port());
		} catch (SQLException e) {
			logger.error(e);
		}finally {
			//MyDataSource.close(connection);
		}
		/*this.snmp=new MySnmp();
		snmp.setStation_ip("192.168.3.223");
		snmp.setStation_name("fygManger");
		snmp.setStation_port(162);
		snmp.setWrite_community_name("public");
		snmp.setVersion(1);
		snmp.setTimeout_ms(100);
		snmp.setUpdate_version(3);*/
		logMe();
	}
	
	private static class MySnmpHolder{
		public static MySnmpCache instance=new MySnmpCache();
	}
	
	public static MySnmpCache getInstance() {
		return MySnmpHolder.instance;
	}
	
	//更新：即需要重新到数据库查询
	public void updateMySnmp() {
		logger.info("Start to get new snmp[Name]:"+snmp.getStation_ip()+",[Port:]"+snmp.getStation_port());
		new MySnmpCache();
	}
	
	public synchronized MySnmp getMySnmp() {
		return this.snmp;
	}
	
	public void logMe(){
		logger.debug("-----Current Snmp Info-----");
		logger.debug("Station IP:"+this.snmp.getStation_ip());
		logger.debug("Station Name:"+this.snmp.getStation_name());
		logger.debug("Station Port:"+this.snmp.getStation_port());
		logger.debug("Read Community Name:"+this.snmp.getRead_community_name());
		logger.debug("Timeout ms:"+this.snmp.getTimeout_ms());
		logger.debug("Version:"+this.snmp.getVersion());
		logger.debug("Update Version:"+this.snmp.getUpdate_version());
	}
}
