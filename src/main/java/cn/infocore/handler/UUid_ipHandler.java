package cn.infocore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import cn.infocore.dto.UUid_ip;

public class UUid_ipHandler implements ResultSetHandler<List<UUid_ip>>{
	@Override
	public List<UUid_ip> handle(ResultSet set) throws SQLException {
		List<UUid_ip> uid_ips=new LinkedList<UUid_ip>();
		while(set.next()) {
			UUid_ip uid_ip=new UUid_ip();
			uid_ip.setIp(set.getString("ip"));
//			uid_ip.setUuid(set.getString("id"));
			uid_ip.setUuid(set.getString("uuid"));
			uid_ips.add(uid_ip);
		}
		return uid_ips;
	}
}
