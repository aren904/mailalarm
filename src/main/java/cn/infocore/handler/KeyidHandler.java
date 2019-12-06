package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.ResultSetHandler;

public class KeyidHandler implements ResultSetHandler<String>{
	
	public String handle(ResultSet rs) throws SQLException {
		StringBuilder builder=new StringBuilder();
		while(rs.next()) {
			builder.append(rs.getString("id"));
			builder.append(";");
		}
		if (builder.length()>=1) {
			builder.deleteCharAt(builder.length()-1);
		}
		return builder.toString();
	}
}

