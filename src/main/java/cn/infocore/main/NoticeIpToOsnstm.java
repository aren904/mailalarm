package cn.infocore.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.log4j.Logger;

import cn.infocore.net.StmHeader;
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
            } catch (IOException e) {
                e.printStackTrace();
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
        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream sis = null;
        try {
            sis = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StmHeader header = new StmHeader();
        header.setVersion((byte) 1);
        header.setDataType((byte) 2);
        header.setDirection((short) 25);
        header.setFlags((short) 0);
        header.setFlags2((short) 0);
        header.setCommand(87001);
        header.setDataLength(bytes.length);
        header.setErrorCode((short) 0);
        
        byte[] headerBuffer = header.toByteArray();
        try {
            out.write(headerBuffer, 0, headerBuffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytes != null) {
            try {
                out.write(bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] headerBuffer1 = new byte[16];
        int ioret = 0;
        try {
            ioret = sis.read(headerBuffer1, 0, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ioret != 16) {
            System.out.println("error headerLength!");
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
