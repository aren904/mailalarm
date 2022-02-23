package cn.infocore.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.net.StmCommand;
import cn.infocore.net.StmHeader;
import cn.infocore.net.StmRetStatus;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.DataArkService;

/**
 * 通知stm服务发心跳：主要是为了告知每个stm服务当前有哪些数据方舟，后续数据方舟修改时也会通知
   因为stm服务只知道所在数据方舟自己添加的，告知所有后，就会向每台数据方舟上的mailalarm服务发送自己的数据方舟信息
 */
@Component
public class ThreadSendHeartbeatRequest extends Thread{
	
	private static final Logger logger = Logger.getLogger(ThreadSendHeartbeatRequest.class);
	
	private volatile boolean reload = true;
	
	@Autowired
    private DataArkService dataArkService;
	
	private static final long split = 3 * 60;
	
	private static List<String> noticedIps = new ArrayList<>();

	@Override
	public void run() {
		while(reload) {
			List<String> ips = dataArkService.findIps();
			logger.info("start to sendHeartbeat request:"+ips.toString());
	    	
			OutputStream out = null;
	        InputStream sis = null;
	        Socket socket=null;
	        StmAlarmManage.SendDataArkIp.Builder builder = StmAlarmManage.SendDataArkIp.newBuilder();
	        builder.addAllIp(ips);
	        byte[] bytes = builder.build().toByteArray();
	        
	        StmHeader header = new StmHeader();
	        header.setVersion((byte) 1);
	        header.setDataType((byte) 2);
	        header.setErrorCode(StmRetStatus.ST_RES_SUCCESS);
	        header.setFlags((short) 0);
	        header.setFrom((short) 25);
	        header.setCommand(StmCommand.ST_OP_MAILALARM_GET_HEARTBEAT);
	        header.setDataLength(bytes.length);
	        
	        //取差集
	        ips.removeAll(noticedIps);
	        logger.debug("SendHeartBeatRequest to:"+ips.toString());
	    	for (String ip : ips) {
	    		try {
		            logger.debug("current request for heartbeat:"+ip+","+ip.length());
		            socket = new Socket(ip, 9997);
	                sis = socket.getInputStream();
	                out = socket.getOutputStream();
	                
	                byte[] headerBuffer = header.toByteArrayLittle();
	                out.write(headerBuffer, 0, headerBuffer.length);
	                if (bytes != null) {
	                	out.write(bytes, 0, bytes.length);
	                }
	                
	                byte[] headerBuffer1 = new byte[16];
	                int ioret = sis.read(headerBuffer1, 0, 16);
	                if (ioret != 16) {
	                    logger.error("error headerLength!");
	                }
	                noticedIps.add(ip);
	            } catch (Exception e) {
	            	logger.error(e);
	            } finally {
	            	try {
	            		sis.close();
	                    out.close();
	                    socket.close();
	                } catch (Exception e) {
	                	logger.error(e);
	                }
	            }
	        }
	    	
	    	logger.info("current noticed ips:"+noticedIps.toString()+",all:"+ips.toString());
	    	if(noticedIps.size()==ips.size()) {
				reload=false;
			}else {
				try {
					Thread.sleep(split * 1000);
				} catch (InterruptedException e) {
					logger.error("ThreadSendHeartbeatRequest interrupted...", e);
				}
			}
		}
	}

}
