package cn.infocore.main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import cn.infocore.utils.DBUtils;
import cn.infocore.utils.MyDataSource;

//内存中维护的客户端列表
public class ClientList {
	private static final Logger logger=Logger.getLogger(ClientList.class);
	private static volatile ClientList instance=null;
	//客户端的uuid
	private Set<String> client_list=new TreeSet<String>();
	
	private ClientList() {
		Connection connection=MyDataSource.getConnection();
		String sql="select id from client";
		ResultSet set=DBUtils.executQuery(connection, sql, null);
		try {
			while (set.next()) {
				this.client_list.add(set.getString("id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ClientList getInstance() {
		if (instance==null) {
			synchronized (ClientList.class) {
				if (instance==null) {
					instance=new ClientList();
				}
			}
		}
		return instance;
	}
	
	//添加客户端
	public synchronized void addClient(String id) {
		this.client_list.add(id);
	}
	//移除客户端
	public synchronized void removeClient(String id) {
		this.client_list.remove(id);
	}
	
	
}
