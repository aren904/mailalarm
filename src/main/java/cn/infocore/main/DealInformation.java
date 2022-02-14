package cn.infocore.main;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import cn.infocore.utils.MyDataSource;
import lombok.Synchronized;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;
import cn.infocore.entity.Email_alarm;
import cn.infocore.mail.MailSender;
import cn.infocore.operator.InforHeader;

import cn.infocore.service.impl.MailServiceImpl;
import cn.infocore.handler.StringHandler;
import cn.infocore.utils.Utils;
import scmp.proto.alarm.CloudManagerAlarm;

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
	@Override
	public void run() {
		int ioret;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			byte[] header = new byte[InforHeader.INFOR_HEADER_LENGTH];
			ioret = this.in.read(header, 0, InforHeader.INFOR_HEADER_LENGTH);
			if (ioret != InforHeader.INFOR_HEADER_LENGTH) {
				logger.error(Utils.fmt("Failed to received header,[%d] byte(s) expected,but [%d] is received.",
						InforHeader.INFOR_HEADER_LENGTH, ioret));
				return;
			}
			InforHeader myHeader = new InforHeader();
			myHeader.parseByteArray(header);
			logger.info("Successfully received heartbeat from Cloud Manager.");
			dispatch(myHeader);
		} catch (Exception e) {
			logger.error("Failed to received on DealInformation.",e);
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
				logger.error("IO Exception occurred while closing socket.", e2);
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
			logger.error("read bytes failed");
			e.printStackTrace();
		}
		if (ioret!=buffer.length) {
			logger.error("check length failed info:"+buffer.length +"actual:"+ioret);
			return;
		}
		int command=header.getCommand();
		logger.info("dispatch code:"+command);
		switch(command) {
			case 1501:
				try {
//					AddDataArkRequest request=AddDataArkRequest.parseFrom(buffer);
//					scmp.proto.alarm.CloudManagerAlarm.AddDataArkRequest request = AddDataArkRequest.parseFrom(buffer);
					CloudManagerAlarm.AddDataArkRequest request = CloudManagerAlarm.AddDataArkRequest.parseFrom(buffer);
					addDataArk(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error(e);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.ErrorCode_AddDataArkFailed_VALUE);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.AddDataArkFailed_VALUE);
					header.setErrorCode(11501);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
						logger.info("Successfully receive information.");
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 1502:
				try {
//					RemoveDataArkRequest request = RemoveDataArkRequest.parseFrom(buffer);
//					scmp.proto.alarm.CloudManagerAlarm.RemoveDataArkRequest request = RemoveDataArkRequest.parseFrom(buffer);
					CloudManagerAlarm.RemoveDataArkRequest request = CloudManagerAlarm.RemoveDataArkRequest.parseFrom(buffer);
					removeDataArk(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error(e);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.ErrorCode_RemoveDataArkFailed_VALUE);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.RemoveDataArkFailed_VALUE);
					header.setErrorCode(11502);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
						logger.info("Successfully receive  information.");
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;

			case 1505:
				try {
//					UpdateDataArkRequest request=UpdateDataArkRequest.parseFrom(buffer);
//					scmp.proto.alarm.CloudManagerAlarm.UpdateDataArkRequest request = UpdateDataArkRequest.parseFrom(buffer);
					CloudManagerAlarm.UpdateDataArkRequest request = CloudManagerAlarm.UpdateDataArkRequest.parseFrom(buffer);
//					updateDataArk(request);
					updateDataArk(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error(e);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.ErrorCode_UpdateDataArkFailed_VALUE);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.UpdateDataArkFailed_VALUE);
					header.setErrorCode(12303);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
						logger.info("Successfully receive  information.");
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2301:
				try {
//					CreateEmailAlarmRequest request = CreateEmailAlarmRequest.parseFrom(buffer);
//					scmp.proto.alarm.CloudManagerAlarm.CreateEmailAlarmRequest request = CreateEmailAlarmRequest.parseFrom(buffer);
					CloudManagerAlarm.CreateEmailAlarmRequest request = CloudManagerAlarm.CreateEmailAlarmRequest.parseFrom(buffer);
					createEmailAlarm(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error(e);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.ErrorCode_CreateEmailAlarmFailed_VALUE);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.CreateEmailAlarmFailed_VALUE);
					header.setErrorCode(12301);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
						logger.info("Successfully receive  information.");
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2303:
				try {
//					UpdateEmailAlarmRequest request= CloudManagerAlarm.UpdateEmailAlarmRequest.parseFrom(buffer);
//					scmp.proto.alarm.CloudManagerAlarm.UpdateEmailAlarmRequest request = UpdateEmailAlarmRequest.parseFrom(buffer);
					CloudManagerAlarm.UpdateEmailAlarmRequest request = CloudManagerAlarm.UpdateEmailAlarmRequest.parseFrom(buffer);
					updateEmailAlarm(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error(e);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.ErrorCode_UpdateEmailAlarmFailed_VALUE);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.ErrorCode_UpdateEmailAlarmFailed_VALUE);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.UpdateEmailAlarmFailed_VALUE);
					header.setErrorCode(12303);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
						logger.info("Successfully receive  information.");
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2304:
				try {
					logger.info("Start sending a test email");
//					VerifyEmailAlarmRequest request = VerifyEmailAlarmRequest.parseFrom(buffer);
//					scmp.proto.alarm.CloudManagerAlarm.VerifyEmailAlarmRequest request = VerifyEmailAlarmRequest.parseFrom(buffer);
					CloudManagerAlarm.VerifyEmailAlarmRequest request = CloudManagerAlarm.VerifyEmailAlarmRequest.parseFrom(buffer);
					boolean result = verifyEmailAlarm(request);
					if (result) {
						header.setErrorCode(0);
					}else {
						header.setErrorCode(12304);
					}

				} catch (Exception e) {
					logger.error(e);
					header.setErrorCode(12304);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
//						DataOutputStream dos = new DataOutputStream(out);
//						dos.write(resp,0,resp.length);
//						logger.info("长度:"+resp.length+" "+"内容:"+ Arrays.toString(resp));
						logger.info("Successfully receive information.");
//						if(dos!=null) {
//							dos.close();
//						}
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2401:
				//add by wxx 2019/04/16,update snmp
				try {
					MySnmpCache.getInstance().updateMySnmp();
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error(e);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.ErrorCode_UpdateSnmpFailed_VALUE);
					//header.setErrorCode(CloudManagerAlarm.ErrorCode.UpdateSnmpFailed_VALUE);
					header.setErrorCode(12401);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						logger.info("response info for updating MySnmp.");
						out.write(resp, 0, resp.length);
						logger.info("processed update MySnmp.");
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			default:logger.error("Unknown Operation Code:"+command);break;
		}

	}

	// 添加数据方舟
	private void addDataArk(CloudManagerAlarm.AddDataArkRequest request){
//		String uuid = request.getId();
		String uuid = request.getUuid();
//		request.getUuid()
		logger.info("Need to add data ark id:" + uuid);
		String ip = "";
		String sql = "select ip from data_ark where uuid=?";
		Object[] param = { uuid };
		QueryRunner qr = MyDataSource.getQueryRunner();
		try {
			ip = qr.query(sql, new StringHandler(), param);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//MyDataSource.close(conn);
		logger.info("Need to add data ark ip:" + ip);
		DataArkList.getInstance().addDataArk(uuid, ip);
		logger.info("Add data ark is successful.");
		request.toBuilder().clear();
//		request.toBuilder().clearId();
		request.toBuilder().clearUuid();
	}

	// 删除数据方舟
	private void removeDataArk(CloudManagerAlarm.RemoveDataArkRequest request){
//		String uuid = request.getId();
		String uuid = request.getUuid();
		DataArkList.getInstance().removeDataArk(uuid);
		//同时删除掉线的缓存
		HeartCache.getInstance().removeHeartCache(uuid);
		request.toBuilder().clear();
		request.toBuilder().clearUuid();
	}

	// 更新数据方舟
	private void updateDataArk(CloudManagerAlarm.UpdateDataArkRequest request){
		// 使用添加接口
		DataArkList.getInstance().addDataArk(request.getUuid(), request.getUuid());
		logger.info("Update data ark is successful.");
		request.toBuilder().clear();
		request.toBuilder().clearUuid();
	}

	// 添加邮件报警配置
	private void createEmailAlarm(CloudManagerAlarm.CreateEmailAlarmRequest request){
//		String name = request.getUserId();
		String name = request.getUserUuid();
		MailServiceImpl.getInstance().addMailService(name);
		logger.info("Add email alarm user is successful.");
		request.toBuilder().clear();
		request.toBuilder().clearUserUuid();
	}

	// 更新邮件报警配置,其实可以和上面同用一个接口
	private void updateEmailAlarm(CloudManagerAlarm.UpdateEmailAlarmRequest request){
		String name = request.getUserUuid();
		MailServiceImpl.getInstance().addMailService(name);
		logger.info("Update email alarm user is successful.");
		request.toBuilder().clear();
		request.toBuilder().clearUserUuid();
	}

	// 测试邮件报警配置
	private boolean verifyEmailAlarm(CloudManagerAlarm.VerifyEmailAlarmRequest request) throws Exception{
		Email_alarm email = new Email_alarm();
		email.setSender_email(request.getAlarmEmailConfig().getSenderEmail());
		email.setSmtp_address(request.getAlarmEmailConfig().getSmtpAddress());
//		email.setSmtp_port(request.getAlarmEmailConfig().getSmtpPort());
		//email.setSmtp_port(request.getAlarmEmailConfig().getSmtpPort());
		email.setSsl_encrypt_enabled(request.getAlarmEmailConfig().getIsSslEncryptEnabled() ? (byte) 1 : 0);
		email.setSmtp_auth_enabled(request.getAlarmEmailConfig().getIsSmtpAuthentication() ? (byte) 1 : 0);
		email.setSmtp_user_uuid(request.getAlarmEmailConfig().getSmtpUserUuid());
		email.setSmtp_password(request.getAlarmEmailConfig().getSmtpPassword().getBytes());
		List<String> list = request.getAlarmEmailConfig().getReceiverEmailsList();
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append(s + ";");
		}
		email.setReceiver_emails(builder.toString());
		boolean result = new MailSender(email).send1(null);
		request.toBuilder().clear();
		return result;
	}

}
