package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import cn.infocore.entity.Email_alarm;
import cn.infocore.mail.MailCenterRestry;
import cn.infocore.mail.MailSender;
import cn.infocore.operator.InforHeader;
import cn.infocore.protobuf.CloudManagerAlarm.AddDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.CreateEmailAlarmRequest;
import cn.infocore.protobuf.CloudManagerAlarm.ErrorCode;
import cn.infocore.protobuf.CloudManagerAlarm.RemoveDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.UpdateDataArkRequest;
import cn.infocore.protobuf.CloudManagerAlarm.UpdateEmailAlarmRequest;
import cn.infocore.protobuf.CloudManagerAlarm.VerifyEmailAlarmRequest;
import cn.infocore.utils.MyDataSource;
import cn.infocore.handler.StringHandler;
import cn.infocore.utils.Utils;

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
				logger.error(Utils.fmt("Failed to recived header,[%d] byte(s) expected,but [%d] is recevied.",
						InforHeader.INFOR_HEADER_LENGTH, ioret));
				return;
			}
			InforHeader myHeader = new InforHeader();
			myHeader.parseByteArray(header);
			logger.info("Successed recived heartbeat from qiangge.");
			dispatch(myHeader);
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
	
	//调度函数，同时捕获异常
	private void dispatch(InforHeader header) {
		if (header==null) {
			logger.error("InforHeader is null.");
			return;
		}
		int ioret=0;
		byte[] buffer=new byte[header.getDataLength()];
		try {
			ioret=in.read(buffer, 0, buffer.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ioret!=buffer.length) {
			return;
		}
		int command=header.getCommand();
		switch(command) {
		case 205:
			try {
				AddDataArkRequest request=AddDataArkRequest.parseFrom(buffer);
				addDataArk(request);
				header.setErrorCode(0);
			} catch (Exception e) {
				logger.error(e);
				header.setErrorCode(ErrorCode.ErrorCode_AddDataArkFailed_VALUE);
			}finally {
				byte[] resp=header.toByteArray();
				try {
					out.write(resp, 0, resp.length);
					logger.info("Successed recived information.");
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			break;
			
		case 206:
			try {
				RemoveDataArkRequest request = RemoveDataArkRequest.parseFrom(buffer);
				removeDataArk(request);
				header.setErrorCode(0);
			} catch (Exception e) {
				logger.error(e);
				header.setErrorCode(ErrorCode.ErrorCode_RemoveDataArkFailed_VALUE);
			}finally {
				byte[] resp=header.toByteArray();
				try {
					out.write(resp, 0, resp.length);
					logger.info("Successed recived information.");
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			break;
			
		case 207:
			try {
				UpdateDataArkRequest request=UpdateDataArkRequest.parseFrom(buffer);
				updateDataArk(request);
				header.setErrorCode(0);
			} catch (Exception e) {
				logger.error(e);
				header.setErrorCode(ErrorCode.ErrorCode_UpdateDataArkFailed_VALUE);
			}finally {
				byte[] resp=header.toByteArray();
				try {
					out.write(resp, 0, resp.length);
					logger.info("Successed recived information.");
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			break;
			
		case 502:
			try {
				CreateEmailAlarmRequest request = CreateEmailAlarmRequest.parseFrom(buffer);
				createEmailAlarm(request);
				header.setErrorCode(0);
			} catch (Exception e) {
				logger.error(e);
				header.setErrorCode(ErrorCode.ErrorCode_CreateEmailAlarmFailed_VALUE);
			}finally {
				byte[] resp=header.toByteArray();
				try {
					out.write(resp, 0, resp.length);
					logger.info("Successed recived information.");
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			break;
			
		case 501:
			try {
				UpdateEmailAlarmRequest request=UpdateEmailAlarmRequest.parseFrom(buffer);
				updateEmailAlarm(request);
				header.setErrorCode(0);
			} catch (Exception e) {
				logger.error(e);
				header.setErrorCode(ErrorCode.ErrorCode_UpdateEmailAlarmFailed_VALUE);
			}finally {
				byte[] resp=header.toByteArray();
				try {
					out.write(resp, 0, resp.length);
					logger.info("Successed recived information.");
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			break;
			
		case 504:
			try {
				VerifyEmailAlarmRequest request = VerifyEmailAlarmRequest.parseFrom(buffer);
				verifyEmailAlarm(request);
				header.setErrorCode(0);
			} catch (Exception e) {
				logger.error(e);
				header.setErrorCode(ErrorCode.ErrorCode_VerifyEmailAlarmFailed_VALUE);
			}finally {
				byte[] resp=header.toByteArray();
				try {
					out.write(resp, 0, resp.length);
					logger.info("Successed recived information.");
				} catch (IOException e1) {
					logger.error(e1);
				}
			}
			break;
		
			default:logger.error("Unknown Operation Code:"+command);break;
		}
		
	}
	
	// 添加数据方舟
	private void addDataArk(AddDataArkRequest request){
		
		String uuid = request.getId();
		Connection conn = MyDataSource.getConnection();
		logger.info("Need to add data ark id:" + uuid);
		String ip = "";
		String sql = "select ip from data_ark where id=?";
		Object[] param = { uuid };
		QueryRunner qr = new QueryRunner();
		try {
			ip = qr.query(conn,sql, new StringHandler(), param);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		MyDataSource.close(conn);
		logger.info("Need to add data ark ip:" + ip);
		DataArkList.getInstance().addDataArk(uuid, ip);
		logger.info("Add data ark successed.");
	}

	// 删除数据方舟
	private void removeDataArk(RemoveDataArkRequest request){
		String uuid = request.getId();
		DataArkList.getInstance().removeDataArk(uuid);
		//同时删除掉线的缓存
		HeartCache.getInstance().removeHeartCache(uuid);
	}

	// 更新数据方舟
	private void updateDataArk(UpdateDataArkRequest request){
		// 使用添加接口
		DataArkList.getInstance().addDataArk(request.getId(), request.getIp());
		logger.info("Update data ark successed.");
	}

	// 添加邮件报警配置
	private void createEmailAlarm(CreateEmailAlarmRequest request){
		String name = request.getUserId();
		MailCenterRestry.getInstance().addMailService(name);
		logger.info("Add email alarm user successed.");
	}

	// 更新邮件报警配置,其实可以和上面同用一个接口
	private void updateEmailAlarm(UpdateEmailAlarmRequest request){
		String name = request.getUserId();
		MailCenterRestry.getInstance().addMailService(name);
		logger.info("Update email alarm user successed.");
	}

	// 测试邮件报警配置
	private void verifyEmailAlarm(VerifyEmailAlarmRequest request){
		Email_alarm email = new Email_alarm();
		email.setSender_email(request.getSenderEmail());
		email.setSmtp_address(request.getSmtpAddress());
		email.setSmtp_port(request.getSmtpPort());
		email.setSsl_encrypt(request.getIsSslEncryptEnabled() ? (byte) 1 : 0);
		email.setSmtp_authentication(request.getIsSmtpAuthentication() ? (byte) 1 : 0);
		email.setSmtp_user_id(request.getSmtpUserId());
		email.setSmtp_password(request.getSmtpPassword());
		List<String> list = request.getReceiverEmailsList();
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append(s + ";");
		}
		email.setReceiver_emails(builder.toString());
		new MailSender(email).send(null);
	}
}
