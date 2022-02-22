package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.log4j.Logger;

import cn.infocore.net.StmCommand;
import cn.infocore.net.StmHeader;
import cn.infocore.net.StmRetStatus;
import cn.infocore.protobuf.StmAlarmManage;
import cn.infocore.service.DataArkService;

/**
 * 发送请求给stm服务：主要是告知所有的数据方舟ip，以便stm服务给每个mailalarm发送心跳
 */
public class NoticeIpToOsnstm implements Runnable {
	
	private static final Logger logger = Logger.getLogger(NoticeIpToOsnstm.class);

	private DataArkService dataArkService;
    
    public static List<String> ips;

    public NoticeIpToOsnstm(DataArkService dataArkService){
        this.dataArkService = dataArkService;
    }
    
    @Override
    public void run() {
        ips = dataArkService.findIps();
        for (String ip : ips) {
            try {
                SendDifferentIps(ip);
            } catch (Exception e) {
                logger.error(e);
                continue;
            }
        }
    }
    
    /**
     * 给每台数据方舟都发送请求
     * @param ip
     * @throws IOException
     */
    public void SendDifferentIps(String ip) throws IOException {
        StmAlarmManage.SendDataArkIp.Builder builder = StmAlarmManage.SendDataArkIp.newBuilder();
        builder.addAllIp(ips);
        byte[] bytes = builder.build().toByteArray();

        Socket socket = new Socket(ip, 9997);
        OutputStream out = null;
        InputStream sis = null;
        try {
            sis = socket.getInputStream();
            out = socket.getOutputStream();
            
            StmHeader header = new StmHeader();
            header.setVersion((byte) 1);
            header.setDataType((byte) 2);
            header.setErrorCode(StmRetStatus.ST_RES_SUCCESS);
            header.setFlags((short) 0);
            header.setFrom((short) 25);
            header.setCommand(StmCommand.ST_OP_MAILALARM_GET_HEARTBEAT);
            header.setDataLength(bytes.length);
            
            byte[] headerBuffer = header.toByteArray();
            out.write(headerBuffer, 0, headerBuffer.length);
            if (bytes != null) {
            	out.write(bytes, 0, bytes.length);
            }
            
            byte[] headerBuffer1 = new byte[16];
            int ioret = sis.read(headerBuffer1, 0, 16);
            if (ioret != 16) {
                System.out.println("error headerLength!");
            }
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
}
