package cn.infocore.handler;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.handler
 * @ClassName: UserIdHd
 * @Author: aren904
 * @Description:
 * @Date: 2021/7/6 11:30
 * @Version: 1.0
 */
public class UserIdHd implements ResultSetHandler<String> {
    @Override
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
