package cn.infocore.handler;

import org.apache.commons.dbutils.ResultSetHandler;

import cn.infocore.dto.EmailAlarmDTO;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.handler
 * @ClassName: email_alarmHandler
 * @Author: aren904
 * @Description:
 * @Date: 2021/7/7 15:20
 * @Version: 1.0
 */
public class email_alarmHandler implements ResultSetHandler<List<EmailAlarmDTO>> {
    @Override
    public List<EmailAlarmDTO> handle(ResultSet resultSet) throws SQLException {
        LinkedList<EmailAlarmDTO> list = new LinkedList<>();
        while (resultSet.next()) {
            EmailAlarmDTO emailAlarm = new EmailAlarmDTO();
            emailAlarm.setLimit_suppress_time(resultSet.getLong("limit_suppress_time"));
            emailAlarm.setUser_id(resultSet.getString("user_id"));
            emailAlarm.setEnabled(resultSet.getByte("enabled"));
            emailAlarm.setSmtp_auth_enabled(resultSet.getByte("smtp_auth_enabled"));
            emailAlarm.setSsl_encrypt_enabled(resultSet.getByte("ssl_encrypt_enabled"));
            emailAlarm.setSmtp_user_uuid(resultSet.getString("smtp_user_uuid"));
            emailAlarm.setReceiver_emails(resultSet.getString("receiver_emails"));
            emailAlarm.setSender_email(resultSet.getString("sender_email"));
            emailAlarm.setSmtp_address(resultSet.getString("smtp_address"));
//            InputStream smtpPassword = resultSet.getBlob("smtp_password").getBinaryStream();
//            emailAlarm.setSmtp_password(resultSet.getBlob("smtp_password"));
            emailAlarm.setSmtp_password(resultSet.getBytes("smtp_password"));
            emailAlarm.setSmtp_port(resultSet.getInt("smtp_port"));
            emailAlarm.setRole(resultSet.getInt("role"));
            emailAlarm.setExceptions(resultSet.getString("exceptions"));
            emailAlarm.setLimit_enabled(resultSet.getByte("limit_enabled"));
            list.add(emailAlarm);
        }
        return list;
    }


}
