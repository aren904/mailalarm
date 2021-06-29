package cn.infocore.handler;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.handler
 * @ClassName: userUUidhandler
 * @Author: aren904
 * @Description:
 * @Date: 2021/6/21 15:01
 * @Version: 1.0
 */
public class userUUidhandler implements ResultSetHandler<String> {
    @Override
    public String handle(ResultSet rs) throws SQLException {
        StringBuffer stringBuffer = new StringBuffer();
        while(rs.next()){
            stringBuffer.append(rs.getString("user_uuid"));
            stringBuffer.append(";");
        }
        if(stringBuffer.length()>=1){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        return stringBuffer.toString();
    }
}