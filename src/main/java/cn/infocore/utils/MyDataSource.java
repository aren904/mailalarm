package cn.infocore.utils;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MyDataSource {
	private static final Logger logger = Logger.getLogger(MyDataSource.class.getName());

	// 通过标识名来创建相应连接池
	private static ComboPooledDataSource dataSource = new ComboPooledDataSource("mysql");

	// 从连接池中取用一个连接
	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();

		} catch (Exception e) {
			logger.error("Exception happened when get database connection.", e);
		}
		return connection;
	}

	// 释放连接回连接池
	public static void close(Connection conn) {

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("Exception in happened when close database connection.", e);
			}
		}
	}
}
