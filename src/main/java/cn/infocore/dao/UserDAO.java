package cn.infocore.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.stereotype.Component;

import cn.infocore.handler.User_idHandler;
import cn.infocore.utils.MyDataSource;

@Component
public class UserDAO {

	
	
	// 获取vc的userid，注意vc会被不同streamer添加
	public String getUserIdByVcent(String vcId, String data_ark_id) {
		QueryRunner q = MyDataSource.getQueryRunner();
		Object[] param = new Object[] { vcId, data_ark_id };
		String result = "";
		String sql = "select user_id from vcenter where vcenter_id=? and data_ark_id=?";
		try {
			result = q.query(sql, new User_idHandler(), param);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// MyDataSource.close(connection);
		}
		return result;
	}

//	public String getUserIdByVM(String uuid, String data_ark_id) {
//		// Connection connection=MyDataSource.getConnection();
//		QueryRunner q = MyDataSource.getQueryRunner();
//		Object[] param = new Object[] { uuid, data_ark_id };
//		String sql = "select user_id from rds where id=? and data_ark_id=?";
//		String result = "";
//		try {
//			result = q.query(sql, new User_idHandler(), param);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			// MyDataSource.close(connection);
//		}
//		return result;
//	}
	
	public String getUserIdByRDS(String rds_id, String data_ark_id) {
		// Connection connection=MyDataSource.getConnection();
		QueryRunner q = MyDataSource.getQueryRunner();
		Object[] param = new Object[] { rds_id, data_ark_id };
		String sql = "select user_id from rds where rds_id=? and data_ark_id=?";
		String result = "";
		try {
			result = q.query(sql, new User_idHandler(), param);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// MyDataSource.close(connection);
		}
		return result;
	}
	public String getUserIdByRDSInstance(String rds_instance_id, String data_ark_id) {
		// Connection connection=MyDataSource.getConnection();
		QueryRunner q = MyDataSource.getQueryRunner();
		Object[] param = new Object[] { rds_instance_id, data_ark_id };
		//String sql = "select user_id from rds_instance where id=? and data_ark_id=?";
		String sql = "SELECT user_id from scmp.rds_instance as A inner join scmp.rds as B on A.rds_id=B.rds_id  and A.rds_id =? and B.data_ark_id = ? ";
		String result = "";
		try {
			result = q.query(sql, new User_idHandler(), param);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// MyDataSource.close(connection);
		}
		return result;
	}
	
}
