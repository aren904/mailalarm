package cn.infocore.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import cn.infocore.entity.UUid_ip;
import cn.infocore.utils.MyDataSource;
import cn.infocore.utils.UUid_ipHandler;


//内存中维护的数据方舟的列表,顺便初始化维护数据方舟心跳的单例queue
public class DataArkList {
	private static final Logger logger=Logger.getLogger(DataArkList.class);
	private static volatile DataArkList instance=null;
	private Connection connection=null;
	//维护的数据方舟的uuid-->ip列表
	private Map<String,String> data_ark_list=new ConcurrentHashMap<String, String>();
	
	private DataArkList() {
		logger.info("Init,Start get all data ark from database.");
		connection=MyDataSource.getConnection();
		//初始的时候，先从数据库中获取一次
		String sql="select id,ip from data_ark";
		QueryRunner qr=new QueryRunner();
		List<UUid_ip> lIps=null;
		try {
			lIps=qr.query(connection, sql, new UUid_ipHandler());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (UUid_ip uid_ip:lIps) {
			this.data_ark_list.put(uid_ip.getUuid(), uid_ip.getIp());
		}
		logger.info("Successed to get data ark,count:"+this.data_ark_list.size());
	}
	
	public static DataArkList getInstance() {
		if (instance==null) {
			synchronized (DataArkList.class) {
				if (instance==null) {
					instance=new DataArkList();
				}
			}
		}
		return instance;
	}
	
	//添加
	public synchronized void addDataArk(String uuid,String ip) {
		data_ark_list.put(uuid, ip);
	}
	//移除
	public synchronized void removeDataArk(String uuid) {
		data_ark_list.remove(uuid);
	}
	//获取所有
	public synchronized Map<String,String> getData_ark_list() {
		return data_ark_list;
	}
	
	
	
	
}
