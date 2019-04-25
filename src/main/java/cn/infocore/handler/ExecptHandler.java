package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * 因为表设计问题，对于客户端的execptions字段特殊处理
 */
public class ExecptHandler implements ResultSetHandler<String>{
	
	public String handle(ResultSet rs) throws SQLException {
		String name="";
		while(rs.next()) {
			name=rs.getString("execptions");
		}
		return name;
	}

}
