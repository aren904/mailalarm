package cn.infocore.main;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.operator.StreamerHeader;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class NoticeIpToOsnstm1 implements Runnable {

   private CaptureDataArkIp captureDataArkIp;
   private static final Integer PORT=9997;

    public NoticeIpToOsnstm1(CaptureDataArkIp captureDataArkIp){
        this.captureDataArkIp = captureDataArkIp;
    }
    private static final Logger logger = Logger.getLogger(NoticeIpToOsnstm1.class);

    public static List<String> ips;

    @Override
    public void run() {
        logger.info("enter OsnIpSendModule");
//        String s = captureDataArkIp.GetDataArkIp();
         ips = captureDataArkIp.GetDataArkIp();
        StreamerClouddrmanage.SendDataArkIp.Builder builder = StreamerClouddrmanage.SendDataArkIp.newBuilder();
        builder.addAllIp(ips);
//        byte[] bytes = builder.build().toByteArray();

//        System.out.println(s);
//        String[] ips = s.split(";");
//        StringBuffer sb = new StringBuffer();
//        sb.append(ips+";");
//        System.out.println(ips.length);
//        for (int i = 0; i <= ips.length; i++) {
//            SendDifferentIps(ips[i]);
//        }

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


    public void SendDifferentIps(String ip) throws IOException {
//        StreamerClouddrmanage.SendDataArkIp.Builder builder = StreamerClouddrmanage.SendDataArkIp.newBuilder();

//        builder.setIp(ip);
//        builder.addAllIp(ip)
//        builder.setIp()
//        logger.info(builder);

        StreamerClouddrmanage.SendDataArkIp.Builder builder = StreamerClouddrmanage.SendDataArkIp.newBuilder();
        builder.addAllIp(ips);
        byte[] bytes = builder.build().toByteArray();


//        byte[] bytes = builder.build().toByteArray();

//        logger.info("hahahah");
        Socket socket = null;
//        try {
            socket = new Socket(ip, 9997);
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.error(e);
//        }

        //根据输入输出流和服务端连接
        OutputStream out = null;//获取一个输出流，向服务端发送信息
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

        StreamerHeader streamerHeader = new StreamerHeader();
        streamerHeader.setCmd(87001);
        streamerHeader.setVersion((short) 0);
        streamerHeader.setFlags((short) 0);
        streamerHeader.setMsgType((short) 2);
        streamerHeader.setDataLength(bytes.length);
        streamerHeader.setFrom((byte) 25);
        streamerHeader.setRetStatus((short) 0);

       // System.out.println(streamerHeader.getDataLength() + "----datalength");

        byte[] headerBuffer = streamerHeader.toByteArrayLittle();  //模拟C发送消息，因为代码解析那边按照C解析的
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
