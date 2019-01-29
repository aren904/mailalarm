package cn.infocore.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class DBUtils {
	private static Logger logger = Logger.getLogger(DBUtils.class);

	/**
	 * 关闭连接对象
	 */
	public static void closeAll(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			logger.warn("Close Database connection failed.");
		}
	}
	
	/**
	 * 批量更新
	 */
	/*public static int batchUpdate(Connection conn, String sql, Object[] param,List<Client_> list) {
		int result=0;
		PreparedStatement statement=null;
		try {
			boolean auto=conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			 String sql="update client set type=?,name=?,ips=?,execeptions=? where id=?";
			 
			statement=conn.prepareStatement(sql);
			for (Client_ client_:list) {
				
				//TODO
				statement.addBatch();
			}
			statement.executeBatch();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}*/
	
	

	/**
	 * 单条记录增删改操作
	 */
	public static int executUpdate(Connection conn, String sql, Object[] param) {
		int result = 0;
		PreparedStatement pstmt = null;
		try {
			boolean auto=conn.getAutoCommit();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					pstmt.setObject(i + 1, param[i]);
				}
			}
			result = pstmt.executeUpdate();
			conn.commit();//提交事务
			conn.setAutoCommit(auto);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();//回滚
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			closeAll(conn, pstmt, null);
		}
		return result;
	}

	/**
	 * 查询
	 */
	public static ResultSet executQuery(Connection conn, String sql, String[] param) {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					pstmt.setString(i + 1, param[i]);
				}
			}
			result = pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}