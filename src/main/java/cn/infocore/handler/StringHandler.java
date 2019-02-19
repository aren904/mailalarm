package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.ResultSetHandler;

public class StringHandler implements ResultSetHandler<String>{

	public String handle(ResultSet set) throws SQLException {
		String string="";
		if (set.next()) {
			string=set.getString("ip");
		}
		return string;
	}
}
