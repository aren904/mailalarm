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
import cn.infocore.handler.UUid_ipHandler;


//内存中维护的数据方舟的列表,顺便初始化维护数据方舟心跳的单例queue
public class DataArkList {
	private static final Logger logger=Logger.getLogger(DataArkList.class);
	//维护的数据方舟的uuid-->ip列表
	private Map<String,String> data_ark_list=new ConcurrentHashMap<String, String>();
	
	private DataArkList() {
		logger.info("Init,Start get all data ark from database.");
		//初始的时候，先从数据库中获取一次
		String sql="select id,ip from data_ark";
		QueryRunner qr=MyDataSource.getQueryRunner();
		List<UUid_ip> lIps=null;
		try {
			lIps=qr.query( sql, new UUid_ipHandler());
			for (UUid_ip uid_ip:lIps) {
				this.data_ark_list.put(uid_ip.getUuid(),uid_ip.getIp());
				//同时初始化维护数据方舟掉线的列表
				HeartCache.getInstance().addHeartCache(uid_ip.getUuid(), 0L);
			}
			logger.info("Successed to get data ark,count:"+this.data_ark_list.size());
		} catch (SQLException e) {
			logger.error(e);
		}finally {
			//MyDataSource.close(connection);
		}
	}
	private static class DataArkListHolder{
		public static DataArkList instance=new DataArkList();
	}
	
	
	public static DataArkList getInstance() {
		return DataArkListHolder.instance;
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
