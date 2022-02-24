package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.log4j.Logger;

import cn.infocore.dto.EmailAlarmDTO;
import cn.infocore.entity.DataArk;
import cn.infocore.net.CMCommand;
import cn.infocore.net.CMHeader;
import cn.infocore.net.CMRetStatus;
import cn.infocore.protobuf.CloudAlarmManage;
import cn.infocore.service.DataArkService;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.service.SnmpService;
import cn.infocore.service.UserService;
import cn.infocore.utils.MailSender;
import cn.infocore.utils.Utils;
import lombok.Data;

/**
 * 处理与管理界面的请求：不需要更新数据库，只更新缓存
 */
@Data
public class DealInformation implements Runnable {
	
	private static final Logger logger = Logger.getLogger(DealInformation.class);
	
	private InputStream in;
	
	private OutputStream out;
	
	private Socket socket;
	
	private DataArkService dataArkService;
	
	private SnmpService mySnmpService;
	
	private EmailAlarmService emailAlarmService;
	
	private UserService userService;
	
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
			//分发请求
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
		
		CMCommand command=header.getCommand();
		logger.info("----------------DealInformation received operation code from cloud manager:"+command);
		switch(command.getValue()) {
			case 1501:
				try {
					CloudAlarmManage.AddDataArkRequest request = CloudAlarmManage.AddDataArkRequest.parseFrom(buffer);
					addDataArk(request);
					header.setErrorCode(CMRetStatus.ST_RES_SUCCESS);
				} catch (Exception e) {
					logger.error("Failed to AddDataArk.",e);
					header.setErrorCode(CMRetStatus.ST_RES_ADD_DATA_ARK_FAIL);
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
					header.setErrorCode(CMRetStatus.ST_RES_SUCCESS);
				} catch (Exception e) {
					logger.error("Failed to RemoveDataArk.",e);
					header.setErrorCode(CMRetStatus.ST_RES_REMOVE_DATA_ARK_FAIL);
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
					header.setErrorCode(CMRetStatus.ST_RES_SUCCESS);
				} catch (Exception e) {
					logger.error("Failed to UpdateDataArk.",e);
					header.setErrorCode(CMRetStatus.ST_RES_UPDATE_DATA_ARK_FAIL);
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
					//CloudAlarmManage.CreateEmailAlarmRequest request = CloudAlarmManage.CreateEmailAlarmRequest.parseFrom(buffer);
					//createEmailAlarm(request);
					header.setErrorCode(CMRetStatus.ST_RES_SUCCESS);
				} catch (Exception e) {
					logger.error("Failed to CreateEmailAlarm.",e);
					header.setErrorCode(CMRetStatus.ST_RES_CREATE_EMAIL_ALARM_FAIL);
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
					//CloudAlarmManage.UpdateEmailAlarmRequest request = CloudAlarmManage.UpdateEmailAlarmRequest.parseFrom(buffer);
					//updateEmailAlarm(request);
					header.setErrorCode(CMRetStatus.ST_RES_SUCCESS);
				} catch (Exception e) {
					logger.error("Failed to UpdateEmailAlarm.",e);
					header.setErrorCode(CMRetStatus.ST_RES_UPDATE_EMAIL_ALARM_FAIL);
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
						header.setErrorCode(CMRetStatus.ST_RES_SUCCESS);
					}else {
						header.setErrorCode(CMRetStatus.ST_RES_VERIFY_EMAIL_ALARM_FAIL);
					}
				} catch (Exception e) {
					logger.error("Failed to VerifyEmailAlarm.",e);
					header.setErrorCode(CMRetStatus.ST_RES_VERIFY_EMAIL_ALARM_FAIL);
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
					//mySnmpService.updateMySnmp();
					//去掉了缓存，压根不需要
					header.setErrorCode(CMRetStatus.ST_RES_SUCCESS);
				} catch (Exception e) {
					logger.error("Failed to updateMySnmp.",e);
					header.setErrorCode(CMRetStatus.ST_RES_UPDATE_SNMP_FAIL);
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
		logger.info("-----------addDataArk:" + uuid+"|"+dataArk.getIp());
		//DataArkListCache.getInstance(dataArkService).addDataArk(uuid, dataArk.getIp());
		//通知心跳信息改变
		new ThreadSendHeartbeatRequest().start();
		request.toBuilder().clear();
		request.toBuilder().clearUuid();
	}

	/**
	 * 移除数据方舟：删除DataArkList缓存里的uuid,ip键值对，删除HeartCache心跳里的缓存
	 * @param request
	 */
	private void removeDataArk(CloudAlarmManage.RemoveDataArkRequest request){
		String uuid = request.getUuid();
		logger.info("-----------removeDataArk:" + uuid);
		//DataArkListCache.getInstance(dataArkService).removeDataArk(uuid);
		//通知心跳信息改变
		new ThreadSendHeartbeatRequest().start();
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
		logger.info("-----------updateDataArk:" + uuid+"|"+dataArk.getIp());
		//DataArkListCache.getInstance(dataArkService).addDataArk(uuid, dataArk.getIp());
		//通知心跳信息改变
		new ThreadSendHeartbeatRequest().start();
		//request.toBuilder().clear();
		//request.toBuilder().clearUuid();
	}

	/**
	 * 添加邮件报警配置：MailServiceImpl里的邮件配置
	 * @param request
	 */
	private void createEmailAlarm(CloudAlarmManage.CreateEmailAlarmRequest request){
		String userUuid = request.getUserUuid();
		logger.info("createEmailAlarm:" + userUuid);
		//emailAlarmService.addEmailAlarm(userUuid);
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
		//emailAlarmService.addEmailAlarm(userUuid);
		request.toBuilder().clear();
		request.toBuilder().clearUserUuid();
	}

	/**
	 * 测试邮件报警配置
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private boolean verifyEmailAlarm(CloudAlarmManage.VerifyEmailAlarmRequest request){
		boolean result=false;
		try {
			EmailAlarmDTO email = new EmailAlarmDTO();
			email.setSenderEmail(request.getAlarmEmailConfig().getSenderEmail());
			email.setSmtpAddress(request.getAlarmEmailConfig().getSmtpAddress());
			email.setSslEncryptEnabled(request.getAlarmEmailConfig().getIsSslEncryptEnabled() ? (byte) 1 : 0);
			email.setSmtpAuthEnabled(request.getAlarmEmailConfig().getIsSmtpAuthentication() ? (byte) 1 : 0);
			email.setSmtpUserUuid(request.getAlarmEmailConfig().getSmtpUserUuid());
			email.setSmtpPassword(request.getAlarmEmailConfig().getSmtpPassword().getBytes());
			List<String> list = request.getAlarmEmailConfig().getReceiverEmailsList();
			StringBuilder builder = new StringBuilder();
			for (String s : list) {
				builder.append(s + ";");
			}
			email.setReceiverEmails(builder.toString());
			result = new MailSender(email).sendTest(null);
			request.toBuilder().clear();
		} catch (Exception e) {
			logger.error("Failed to verifyEmailAlarm.",e);
		}
		return result;
	}

}
