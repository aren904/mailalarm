package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.ResultSetHandler;

public class ExceptHandler implements ResultSetHandler<String>{
	
	public String handle(ResultSet rs) throws SQLException {
		String name="";
		while(rs.next()) {
			name=rs.getString("exceptions");
		}
		return name;
	}

}
