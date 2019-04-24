package cn.infocore.main;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import cn.infocore.utils.MyDataSource;

public class Test {
	public static void main(String[] args) throws SQLException {
		Object[] para = {"1","2"};
		String sql="select * from test where clientId=? and streamerId=?";
		QueryRunner qr2 = MyDataSource.getQueryRunner();
		//db error
		System.out.println("start too query");
		List<Integer> dbErrors = qr2.query(sql, new ColumnListHandler<Integer>("id"),para);
		System.out.println(dbErrors.size()+","+dbErrors.toString());
		
	}
}
