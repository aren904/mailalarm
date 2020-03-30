package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import cn.infocore.dto.DataArkDTO;

public class DataArkHandler implements ResultSetHandler<DataArkDTO>{

	public DataArkDTO handle(ResultSet rs) throws SQLException {
		StringBuilder builder=new StringBuilder();
		DataArkDTO data_ark=null;
		int i=0;
		while(rs.next()) {
			if(i==0){
				data_ark=new DataArkDTO();
				data_ark.setName(rs.getString("name"));
				data_ark.setIp(rs.getString("ip"));
				builder.append(rs.getString("user_id"));
			}else{
				builder.append(";");
				builder.append(rs.getString("user_id"));
			}
			i++;
		}
		if(data_ark!=null){
            data_ark.setUser_id(builder.toString());
        }
		return data_ark;

	}
}
