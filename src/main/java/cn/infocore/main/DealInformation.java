package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import cn.infocore.entity.Email_alarm;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.mail.MailSender;
import cn.infocore.operator.InforHeader;
import cn.infocore.protobuf.CloudManagerAlarm.AddDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.CreateEmailAlarmRequest;
import cn.infocore.protobuf.CloudManagerAlarm.RemoveDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.UpdateDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.UpdateEmailAlarmRequest;
import cn.infocore.protobuf.CloudManagerAlarm.VerifyEmailAlarmRequest;
import cn.infocore.utils.MyDataSource;
import cn.infocore.utils.StringHandler;

public class DealInformation implements Runnable {
	private static final Logger logger = Logger.getLogger(DealInformation.class);
	private InputStream in;
	private OutputStream out;
	private Socket socket;

	public DealInformation(Socket socket) {
		this.socket = socket;
		this.in = null;
		this.out = null;
	}

	/*
	 * cloudmanager来的所有请求，数据库都不需要我来操作，只是通知我，对本地缓存操作.
	 */
	public void run() {
		int ioret;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			byte[] header = new byte[InforHeader.INFOR_HEADER_LENGTH];
			ioret = this.in.read(header, 0, InforHeader.INFOR_HEADER_LENGTH);
			if (ioret != InforHeader.INFOR_HEADER_LENGTH) {
				logger.error(fmt("Failed to recived header,[%d] byte(s) expected,but [%d] is recevied.",
						InforHeader.INFOR_HEADER_LENGTH, ioret));
				return;
			}
			InforHeader myHeader = new InforHeader();
			myHeader.parseByteArray(header);
			logger.info("Successed recived heartbeat from qiangge.");
			int opCode = myHeader.getCommand();
			if (opCode == 205) {
				addDataArk(myHeader);
			} else if (opCode == 206) {
				removeDataArk(myHeader);
			} else if (opCode == 207) {
				updateDataArk(myHeader);
			} else if (opCode == 502) {
				createEmailAlarm(myHeader);
			} else if (opCode == 501) {
				updateEmailAlarm(myHeader);
			} else if (opCode == 504) {
				verifyEmailAlarm(myHeader);
			} else {
				logger.error("Unknown Operation Code:" + opCode);
			}
			byte[] resp=myHeader.toByteArray();
			out.write(resp, 0, resp.length);
			logger.info("Successed recived information.");

		} catch (Exception e) {
			logger.error("Failed to recived on DealInformation.",e);
		}finally {
			try {
				if (this.in!=null) {
					this.in.close();
				}
				if (this.out!=null) {
					this.out.close();
				}
				this.socket.close();
			} catch (Exception e2) {
				logger.error("IO Exception occured while closing socket.", e2);
			}
		}

	}

	// 添加数据方舟
	private void addDataArk(InforHeader header) throws Exception {
		logger.info("Recived addDataArk command.");
		if (header == null) {
			return;
		}
		if (header.getCommand() != 205) {
			logger.error("Get information from cloudmanager,operation code:" + header.getCommand());
			return;
		}
		int ioret;
		byte[] buffer = new byte[header.getDataLength()];
		ioret = in.read(buffer, 0, buffer.length);
		if (ioret != buffer.length) {
			return;
		}
		AddDataArkRequest request = AddDataArkRequest.parseFrom(buffer);
		if (request == null) {
			return;
		}
		String uuid = request.getId();
		Connection conn = MyDataSource.getConnection();
		logger.info("Need to add data ark id:" + uuid);
		String ip = "";
		String sql = "select ip from data_ark where id=?";
		Object[] param = { uuid };
		QueryRunner qr = new QueryRunner();
		ip = qr.query(sql, new StringHandler(), param);
		MyDataSource.close(conn);
		logger.info("Need to add data ark ip:" + ip);
		DataArkList.getInstance().addDataArk(uuid, ip);
		logger.info("Add data ark successed.");
		header.setErrorCode(0);
	}

	// 删除数据方舟
	private void removeDataArk(InforHeader header) throws IOException {
		if (header == null) {
			return;
		}
		if (header.getCommand() != 206) {
			logger.error("Get information from cloudmanager,operation code:" + header.getCommand());
			return;
		}
		int ioret;
		byte[] buffer = new byte[header.getDataLength()];
		ioret = in.read(buffer, 0, buffer.length);
		if (ioret != buffer.length) {
			return;
		}
		RemoveDataArkRequest request = RemoveDataArkRequest.parseFrom(buffer);
		if (request == null) {
			return;
		}
		String uuid = request.getId();
		DataArkList.getInstance().removeDataArk(uuid);
		header.setErrorCode(0);
	}

	// 更新数据方舟
	private void updateDataArk(InforHeader header) throws IOException {
		if (header == null) {
			return;
		}
		if (header.getCommand() != 207) {
			logger.error("Get information from cloudmanager,operation code:" + header.getCommand());
			return;
		}
		int ioret;
		byte[] buffer = new byte[header.getDataLength()];
		ioret = in.read(buffer, 0, buffer.length);
		if (ioret != buffer.length) {
			return;
		}
		UpdateDataArkRequest request = UpdateDataArkRequest.parseFrom(buffer);
		if (request == null) {
			return;
		}
		// 使用添加接口
		DataArkList.getInstance().addDataArk(request.getId(), request.getIp());
		logger.info("Update data ark successed.");
		header.setErrorCode(0);
	}

	// 添加邮件报警配置
	private void createEmailAlarm(InforHeader header) throws IOException {
		if (header == null) {
			return;
		}
		if (header.getCommand() != 502) {
			logger.error("Get information from cloudmanager,operation code:" + header.getCommand());
			return;
		}
		int ioret;
		byte[] buff = new byte[header.getDataLength()];
		ioret = in.read(buff, 0, buff.length);
		if (ioret != buff.length) {
			return;
		}
		CreateEmailAlarmRequest request = CreateEmailAlarmRequest.parseFrom(buff);
		if (request == null) {
			return;
		}
		String name = request.getUserId();
		header.setErrorCode(0);
		MailCenterRestry.getInstance().addMailService(name);
		logger.info("Add email alarm user successed.");
	}

	// 更新邮件报警配置,其实可以和上面同用一个接口
	private void updateEmailAlarm(InforHeader header) throws IOException {
		if (header == null) {
			return;
		}
		if (header.getCommand() != 501) {
			logger.error("Get information from cloudmanager,operation code:" + header.getCommand());
			return;
		}
		int ioret;
		byte[] buff = new byte[header.getDataLength()];
		ioret = in.read(buff, 0, buff.length);
		if (ioret != buff.length) {
			return;
		}
		UpdateEmailAlarmRequest request = UpdateEmailAlarmRequest.parseFrom(buff);
		if (request == null) {
			return;
		}
		String name = request.getUserId();
		header.setErrorCode(0);
		MailCenterRestry.getInstance().addMailService(name);
		logger.info("Update email alarm user successed.");
	}

	// 测试邮件报警配置
	private void verifyEmailAlarm(InforHeader header) throws IOException {
		if (header == null)
			return;
		if (header.getCommand() != 504) {
			logger.error("Get information from cloudmanager,operation code:" + header.getCommand());
			return;
		}
		int ioret;
		byte[] buffer = new byte[header.getDataLength()];
		ioret = in.read(buffer, 0, buffer.length);
		if (ioret != buffer.length)
			return;
		header.setErrorCode(0);
		VerifyEmailAlarmRequest request = VerifyEmailAlarmRequest.parseFrom(buffer);
		if (request == null)
			return;
		Email_alarm email = new Email_alarm();
		email.setSender_email(request.getSenderEmail());
		email.setSmtp_address(request.getSmtpAddress());
		email.setSmtp_port(request.getSmtpPort());
		email.setSsl_encrypt(request.getIsSslEncryptEnabled() ? (byte) 1 : 0);
		email.setSmtp_authentication(request.getIsSmtpAuthentication() ? (byte) 1 : 0);
		email.setSmtp_user_id(request.getSmtpUserId());
		email.setStmp_password(request.getSmtpPassword());
		List<String> list = request.getReceiverEmailsList();
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append(s + ";");
		}
		email.setReceiver_emails(builder.toString());
		new MailSender(email).send(null);
	}

	private static String fmt(String fmt, Object... obj) {
		return String.format(fmt, obj);
	}

}
