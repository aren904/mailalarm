package cn.infocore.handler;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.handler
 * @ClassName: IdHandler
 * @Author: aren904
 * @Description:
 * @Date: 2021/5/18 19:17
 * @Version: 1.0
 */
public class IdHandler implements ResultSetHandler<String> {
    @Override
    public String handle(ResultSet rs) throws SQLException {
        StringBuffer stringBuffer = new StringBuffer();
        while(rs.next()){
            stringBuffer.append(rs.getString("id"));
            stringBuffer.append(";");
        }
        if(stringBuffer.length()>=1){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        return stringBuffer.toString();
    }
}
