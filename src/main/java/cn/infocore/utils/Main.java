package cn.infocore.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class Main {
	public static void main(String[] args) {
		
		Connection connection=MyDataSource.getConnection();
		if (connection==null) {
			System.out.println("Failed");
		}
		System.out.println("Connected success");
		/*String sql="insert into alarm_log values(?,?,?,?,?,?,?,?,?)";
		Object[] param= {null,System.currentTimeMillis()/1000,0,12,"data_ark_id","data_ark_name",
				"192.168.1.1","data_ark_id",System.currentTimeMillis()/1000};
		DBUtils.executUpdate(connection, sql, param);
		MyDataSource.close(connection);
		System.out.println("OK");*/
		
		long before=System.currentTimeMillis()/1000;
		
		PreparedStatement statement=null;
		try {
			//boolean auto=connection.getAutoCommit();
			connection.setAutoCommit(false);
			String sql="insert into alarm_log values(?,?,?,?,?,?,?,?,?)";
			statement=connection.prepareStatement(sql);
			for (int i=1;i<10;i++) {
				try {
					statement.setObject(1, null);
					statement.setObject(2, System.currentTimeMillis()/1000);
					statement.setObject(3, 0);
					statement.setObject(4, i+12);
					statement.setObject(5, "data_ark_id"+i);
					statement.setObject(6, "data_ark_name"+i);
					statement.setObject(7, "192.168.1."+i);
					statement.setObject(8, "target"+i);
					statement.setObject(9, System.currentTimeMillis()/1000);
					statement.addBatch();
				} catch (Exception e) {
					System.out.println("类型异常");
				}
			}
			statement.executeBatch();
			connection.commit();
		//	connection.setAutoCommit(auto);
			System.out.printf("Commit successed.Used time:%ld\n",System.currentTimeMillis()-before);
		} catch (Exception e) {
			try {
				System.out.println(e);
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			if (connection!=null) {
				try {
					connection.close();
					System.out.println("Connection closed.");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
