package cn.infocore.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

/*
 * 继承QueryRunner()，封装实现了事务
 */
public class TxQueryRunner extends QueryRunner {

	@Override
	public int[] batch(String sql, Object[][] params) throws SQLException {
		Connection con = MyDataSource.getConnection();
		int[] result = super.batch(con, sql, params);
		MyDataSource.close(con);
		return result;
	}

	@Override
	public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
		Connection con = MyDataSource.getConnection();
		T result = super.query(con, sql, rsh, params);
		MyDataSource.close(con);
		return result;
	}

	@Override
	public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException {
		Connection con = MyDataSource.getConnection();
		T result = super.query(con, sql, rsh);
		MyDataSource.close(con);
		return result;
	}

	@Override
	public int update(String sql, Object... params) throws SQLException {
		Connection con = MyDataSource.getConnection();
		int result = super.update(con, sql, params);
		MyDataSource.close(con);
		return result;
	}

	@Override
	public int update(String sql, Object param) throws SQLException {
		Connection con = MyDataSource.getConnection();
		int result = super.update(con, sql, param);
		MyDataSource.close(con);
		return result;
	}

	@Override
	public int update(String sql) throws SQLException {
		Connection con = MyDataSource.getConnection();
		int result = super.update(con, sql);
		MyDataSource.close(con);
		return result;
	}
	
	
	public static void main(String[] args) {
	}
}
