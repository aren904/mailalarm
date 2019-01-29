package cn.infocore.main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import cn.infocore.utils.DBUtils;
import cn.infocore.utils.MyDataSource;


//内存中维护的数据方舟的列表,顺便初始化维护数据方舟心跳的单例queue
public class DataArkList {
	private static final Logger logger=Logger.getLogger(DataArkList.class);
	private static volatile DataArkList instance=null;
	private Connection connection=null;
	//维护的数据方舟的uuid-->ip列表
	private Map<String,String> data_ark_list=new ConcurrentHashMap<String, String>();
	
	private DataArkList() {
		connection=MyDataSource.getConnection();
		//初始的时候，先从数据库中获取一次
		String sql="select id,ips from data_ark";
		ResultSet set=DBUtils.executQuery(connection, sql, null);
		try {
			while (set.next()) {
				this.data_ark_list.put(set.getString("id"),set.getString("ips"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	public Map<String,String> getData_ark_list() {
		return data_ark_list;
	}
	
	
	
	
}
