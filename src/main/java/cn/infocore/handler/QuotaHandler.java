package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import cn.infocore.entity.Quota;

public class QuotaHandler implements ResultSetHandler<List<Quota>>{

	public List<Quota> handle(ResultSet set) throws SQLException {
		List<Quota> list=new LinkedList<Quota>();
		while(set.next()) {
			Quota quota=new Quota();
			quota.setData_ark_id(set.getString("data_ark_id"));
			quota.setUser_id(set.getString("user_id"));
			list.add(quota);
		}
		return list;
	}

}
