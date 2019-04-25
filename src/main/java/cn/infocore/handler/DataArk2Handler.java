package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import cn.infocore.entity.Data_ark;

public class DataArk2Handler implements ResultSetHandler<Data_ark>{

	public Data_ark handle(ResultSet rs) throws SQLException {
		Data_ark data_ark=null;
		while(rs.next()) {
			data_ark=new Data_ark();
			data_ark.setName(rs.getString("name"));
			data_ark.setIp(rs.getString("ip"));
			data_ark.setId(rs.getString("id"));
		}
		return data_ark;
	}
}
