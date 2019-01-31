package cn.infocore.utils;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbutils.QueryRunner;



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
		QueryRunner qr=new QueryRunner();
		String sql="insert into alarm_log values(?,?,?,?,?,?,?,?,?)";
	
		Object[][] param=new Object[10][];
		for (int i=0;i<10;i++) {
			param[i]=new Object[9];
			param[i][0]=null;
			param[i][1]=System.currentTimeMillis()/1000;
			param[i][2]=0;
			param[i][3]=i+12;
			param[i][4]="data_ark_id"+i;
			param[i][5]="data_ark_name"+i;
			param[i][6]="192.168.1."+i;
			param[i][7]="target-"+i;
			param[i][8]=System.currentTimeMillis()/1000;
		}
		try {
			qr.batch(connection, sql, param);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			MyDataSource.close(connection);
		}
		System.out.println("用时:"+(System.currentTimeMillis()/1000-before));
	}
}
