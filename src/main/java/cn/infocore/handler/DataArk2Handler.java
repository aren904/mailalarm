package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import cn.infocore.dto.DataArkDTO;

public class DataArk2Handler implements ResultSetHandler<DataArkDTO>{

	@Override
	public DataArkDTO handle(ResultSet rs) throws SQLException {
		DataArkDTO data_ark=null;
		while(rs.next()) {
			data_ark=new DataArkDTO();
			data_ark.setName(rs.getString("name"));
			data_ark.setIp(rs.getString("ip"));
//			data_ark.setId(rs.getString("id"));
			data_ark.setUuid(rs.getString("uuid"));
		}
		return data_ark;
	}
}
