package cn.infocore.handler;

import org.apache.commons.dbutils.ResultSetHandler;

import javax.xml.ws.handler.Handler;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.handler
 * @ClassName: dataIdHandler
 * @Author: aren904
 * @Description:
 * @Date: 2021/7/2 11:31
 * @Version: 1.0
 */
public class dataIdHandler implements ResultSetHandler<String> {
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
