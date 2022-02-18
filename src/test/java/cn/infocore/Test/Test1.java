package cn.infocore.Test;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.mail.MailSender;
import scmp.proto.alarm.CloudManagerAlarm;

import java.util.List;

public class Test1 {
    public static void main(String[] args) {
        EmailAlarmDTO email = new EmailAlarmDTO();
//		email.setSender_email(request.getSenderEmail());
        //email.setSender_email(request.getAlarmEmailConfig().getSenderEmail());
        email.setSender_email("zxcdr22@163.com");
//		email.setSmtp_address(request.getSmtpAddress());
        email.setSmtp_address("smtp.163.com");
//		email.setSmtp_port(requestsmtp.163.com.getSmtpPort());
        email.setSmtp_port(465);
//		email.setSsl_encrypt_enabled(request.getIsSslEncryptEnabled() ? (byte) 1 : 0);
        //email.setSsl_encrypt_enabled(request.getAlarmEmailConfig().getIsSslEncryptEnabled() ? (byte) 1 : 0);
        email.setSsl_encrypt_enabled((byte)1);
//		email.setSmtp_auth_enadled(request.getIsSmtpAuthentication() ? (byte) 1 : 0);
        email.setSmtp_auth_enabled((byte) 1 );
		email.setSmtp_user_uuid("zxcdr22@163.com");
//        email.setUser_id();
  //      email.setSmtp_user_uuid(request.getAlarmEmailConfig().getSmtpUserUuid());
//		email.setSmtp_password(request.getSmtpPassword());
//		email.setSmtp_password(request.getSmtpPassword().getBytes());

        email.setSmtp_password("YAFEVFXRDSGRJKHT".getBytes());
//        List<String> list = request.getAlarmEmailConfig().getReceiverEmailsList();
//        StringBuilder builder = new StringBuilder();
//        for (String s : list) {
//            builder.append(s + ";");
//        }
//        email.setReceiver_emails(builder.toString());
        email.setReceiver_emails("583295436@qq.com");

        boolean result = false;

        try {
            //result = new MailSender(email).send1(null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

        //request.toBuilder().clear();
        System.out.println(result);
    }
}
