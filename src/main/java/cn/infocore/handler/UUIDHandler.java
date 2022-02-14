package cn.infocore.handler;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.handler
 * @ClassName: UUIDHandler
 * @Author: aren904
 * @Description:
 * @Date: 2021/7/2 13:22
 * @Version: 1.0
 */
public class UUIDHandler implements ResultSetHandler<String> {
    @Override
    public String handle(ResultSet rs) throws SQLException {
        StringBuilder builder=new StringBuilder();
        while(rs.next()) {
//			builder.append(rs.getString("user_id"));
            builder.append(rs.getString("user_id"));
            builder.append(";");
        }
        if (builder.length()>=1) {
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }
}
