package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.entity.DataArk;
import cn.infocore.handler.StringHandler;
import cn.infocore.mail.MailSender;
import cn.infocore.net.CMHeader;
import cn.infocore.protobuf.CloudAlarmManage;
import cn.infocore.service.DataArkService;
import cn.infocore.service.impl.MailServiceImpl;
import cn.infocore.utils.MyDataSource;
import cn.infocore.utils.Utils;
import lombok.Data;

/**
 * 处理与管理界面的请求：不需要操作数据库，只更新缓存
 */
@Data
public class DealInformation implements Runnable {
	
	private static final Logger logger = Logger.getLogger(DealInformation.class);
	
	private InputStream in;
	private OutputStream out;
	private Socket socket;
	
	private DataArkService dataArkService;
	
	public DealInformation(Socket socket) {
		this.socket = socket;
		this.in = null;
		this.out = null;
	}
	
	@Override
	public void run() {
		int ioret;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			byte[] header = new byte[CMHeader.CM_HEADER_LENGTH];
			ioret = this.in.read(header, 0, CMHeader.CM_HEADER_LENGTH);
			if (ioret != CMHeader.CM_HEADER_LENGTH) {
				logger.error(Utils.fmt("Failed to received header,[%d] byte(s) expected,but [%d] is received.",
						CMHeader.CM_HEADER_LENGTH, ioret));
				return;
			}
			
			CMHeader myHeader = new CMHeader();
			myHeader.parseByteArray(header);
			logger.info("Successfully received request from Cloud Manager.");
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
	private void dispatch(CMHeader header) {
		if (header==null) {
			logger.error("CMHeader is null.");
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
		logger.info("----------------Received operation code:"+command);
		switch(command) {
			case 1501:
				try {
					CloudAlarmManage.AddDataArkRequest request = CloudAlarmManage.AddDataArkRequest.parseFrom(buffer);
					addDataArk(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error("Failed to AddDataArk.",e);
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
					CloudAlarmManage.RemoveDataArkRequest request = CloudAlarmManage.RemoveDataArkRequest.parseFrom(buffer);
					removeDataArk(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error("Failed to RemoveDataArk.",e);
					header.setErrorCode(11502);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 1505:
				try {
					CloudAlarmManage.UpdateDataArkRequest request = CloudAlarmManage.UpdateDataArkRequest.parseFrom(buffer);
					updateDataArk(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error("Failed to UpdateDataArk.",e);
					header.setErrorCode(12303);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2301:
				try {
					CloudAlarmManage.CreateEmailAlarmRequest request = CloudAlarmManage.CreateEmailAlarmRequest.parseFrom(buffer);
					createEmailAlarm(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error("Failed to CreateEmailAlarm.",e);
					header.setErrorCode(12301);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2303:
				try {
					CloudAlarmManage.UpdateEmailAlarmRequest request = CloudAlarmManage.UpdateEmailAlarmRequest.parseFrom(buffer);
					updateEmailAlarm(request);
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error("Failed to UpdateEmailAlarm.",e);
					header.setErrorCode(12303);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2304:
				try {
					CloudAlarmManage.VerifyEmailAlarmRequest request = CloudAlarmManage.VerifyEmailAlarmRequest.parseFrom(buffer);
					boolean result = verifyEmailAlarm(request);
					if (result) {
						header.setErrorCode(0);
					}else {
						header.setErrorCode(12304);
					}
				} catch (Exception e) {
					logger.error("Failed to VerifyEmailAlarm.",e);
					header.setErrorCode(12304);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			case 2401:
				try {
					MySnmpCache.getInstance().updateMySnmp();
					header.setErrorCode(0);
				} catch (Exception e) {
					logger.error("Failed to updateMySnmp.",e);
					header.setErrorCode(12401);
				}finally {
					byte[] resp=header.toByteArray();
					try {
						out.write(resp, 0, resp.length);
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
				break;
			default:logger.error("Unknown operation code:"+command);break;
		}

	}

	/**
	 * 添加数据方舟：更新DataArkList缓存里的<uuid,ip>键值对
	 * @param request
	 */
	private void addDataArk(CloudAlarmManage.AddDataArkRequest request){
		String uuid = request.getUuid();
		DataArk dataArk=dataArkService.findByUuid(uuid);
		logger.info("addDataArk:" + uuid+"|"+dataArk.getIp());
		DataArkList.getInstance().addDataArk(uuid, dataArk.getIp());
		request.toBuilder().clear();
		request.toBuilder().clearUuid();
	}

	/**
	 * 移除数据方舟：删除DataArkList缓存里的uuid,ip键值对，删除HeartCache心跳里的缓存
	 * @param request
	 */
	private void removeDataArk(CloudAlarmManage.RemoveDataArkRequest request){
		String uuid = request.getUuid();
		logger.info("removeDataArk:" + uuid);
		DataArkList.getInstance().removeDataArk(uuid);
		HeartCache.getInstance().removeHeartCache(uuid);
		request.toBuilder().clear();
		request.toBuilder().clearUuid();
	}

	/**
	 * 更新数据方舟：覆盖DataArkList缓存里的uuid,ip键值对
	 * @param request
	 */
	private void updateDataArk(CloudAlarmManage.UpdateDataArkRequest request){
		String uuid = request.getUuid();
		DataArk dataArk=dataArkService.findByUuid(uuid);
		logger.info("updateDataArk:" + uuid+"|"+dataArk.getIp());
		DataArkList.getInstance().addDataArk(uuid, dataArk.getIp());
		request.toBuilder().clear();
		request.toBuilder().clearUuid();
	}

	/**
	 * 添加邮件报警配置：MailServiceImpl里的邮件配置
	 * @param request
	 */
	private void createEmailAlarm(CloudAlarmManage.CreateEmailAlarmRequest request){
		String userUuid = request.getUserUuid();
		logger.info("createEmailAlarm:" + userUuid);
		MailServiceImpl.getInstance().addMailService(userUuid);
		request.toBuilder().clear();
		request.toBuilder().clearUserUuid();
	}

	/**
	 * 更新邮件报警配置：同添加
	 * @param request
	 */
	private void updateEmailAlarm(CloudAlarmManage.UpdateEmailAlarmRequest request){
		String userUuid = request.getUserUuid();
		logger.info("updateEmailAlarm:" + userUuid);
		MailServiceImpl.getInstance().addMailService(userUuid);
		request.toBuilder().clear();
		request.toBuilder().clearUserUuid();
	}

	/**
	 * 测试邮件报警配置
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private boolean verifyEmailAlarm(CloudAlarmManage.VerifyEmailAlarmRequest request) throws Exception{
		EmailAlarmDTO email = new EmailAlarmDTO();
		email.setSender_email(request.getAlarmEmailConfig().getSenderEmail());
		email.setSmtp_address(request.getAlarmEmailConfig().getSmtpAddress());
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
		boolean result = new MailSender(email).sendTest(null);
		request.toBuilder().clear();
		return result;
	}

}
