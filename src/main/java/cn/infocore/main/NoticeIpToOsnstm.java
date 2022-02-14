//package cn.infocore.main;
//
//import StmStreamerDrManage.StreamerClouddrmanage;
//import cn.infocore.operator.Header;
//import cn.infocore.operator.StreamerHeader;
//import cn.infocore.service.impl.MailServiceImpl;
//import lombok.Data;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//@Data
//@Component
////@Async
//public class NoticeIpToOsnstm extends Thread {
//
//    @Autowired
//    CaptureDataArkIp captureDataArkIp;
//
//    private static final Logger logger = Logger.getLogger(NoticeIpToOsnstm.class);
//    private ThreadPoolExecutor threadPool;
//    private NoticeIpToOsnstm() {
//        threadPool = new ThreadPoolExecutor(100, 500, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
//        threadPool.allowCoreThreadTimeOut(true);
//    }
//
//    private static class NoticeIpToOsnstmHolder {
//        public static NoticeIpToOsnstm instance = new NoticeIpToOsnstm();
//    }
//
//    public static NoticeIpToOsnstm getInstance() {
//        return NoticeIpToOsnstm.NoticeIpToOsnstmHolder.instance;
//    }
//
//
//
//    @Override
////    @Scheduled(cron="0/10 * *  * * ? ")
//    public void run() {
//
//
//        logger.info("enter osnipsendModule");
////        String s = captureDataArkIp.GetDataArkIp();
//        String s = captureDataArkIp.GetDataArkIp();
//        String[] ips = s.split(";");
//       for (int i =0;i<ips.length;i++){
//           SendDifferentIps(ips[i]);
//        }
//
//
//    }
////    @Scheduled(cron="0/10 * *  * * ? ")
//    public void SendDifferentIps(String ip){
//        StreamerClouddrmanage.SendDataArkIp.Builder builder = StreamerClouddrmanage.SendDataArkIp.newBuilder();
//        builder.setIp(ip);
//        logger.info(builder);
//        byte[] bytes = builder.build().toByteArray();
//
//        logger.info("hahahah");
//        Socket socket = null;
//        try {
//            socket = new Socket(ip, 9997);
//            logger.info("创建socket成功");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //根据输入输出流和服务端连接
//        OutputStream out = null;//获取一个输出流，向服务端发送信息
//        try {
//            out = socket.getOutputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        InputStream sis = null;
//        try {
//            sis = socket.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        StreamerHeader streamerHeader = new StreamerHeader();
//        streamerHeader.setCmd(87001);
//        streamerHeader.setVersion((short) 0);
//        streamerHeader.setFlags((short) 0);
//        streamerHeader.setMsgType((short) 2);
//        streamerHeader.setDataLength(bytes.length);
//        streamerHeader.setFrom((byte) 25);
//        streamerHeader.setRetStatus((short) 0);
//
//        System.out.println(streamerHeader.getDataLength() + "----datalength");
//
//        byte[] headerBuffer = streamerHeader.toByteArrayLittle();  //模拟C发送消息，因为代码解析那边按照C解析的
//        try {
//            out.write(headerBuffer, 0, headerBuffer.length);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        if (bytes != null) {
//            try {
//                out.write(bytes, 0, bytes.length);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        byte[] headerBuffer1 = new byte[16];
//        int ioret = 0;
//        try {
//            ioret = sis.read(headerBuffer1, 0, 16);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (ioret != 16) {
//            System.out.println("error headerLength!");
//        }
//
//        try {
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
