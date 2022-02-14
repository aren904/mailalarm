package cn.infocore.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import StmStreamerDrManage.StreamerClouddrmanage;
import cn.infocore.operator.Header;
import cn.infocore.operator.StreamerHeader;
import cn.infocore.utils.Utils;
import org.apache.log4j.Logger;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.service.AlarmLogService;
import cn.infocore.service.DataArkService;
import cn.infocore.service.OssService;
import cn.infocore.service.RDSService;
import cn.infocore.service.impl.EcsService;
import cn.infocore.service.impl.MdbService;

@Component
public class ThreadHeartbeat extends Thread {
    private static final int PORT = 23335;
    //	private static final String MAIL_ALARM_SOCK="/var/run/mailalarm.sock";
    private ThreadPoolExecutor threadPool;
    private static final Logger logger = Logger.getLogger(ThreadHeartbeat.class);
    @Autowired
    RDSService rdsService;
    @Autowired
    EcsService ecsService;
    @Autowired
    MdbService mdbService;
    @Autowired
    DataArkService dataArkService;
    @Autowired
    OssService ossService;
    @Autowired
    AlarmLogService alarmLogService;


    private ThreadHeartbeat() {
        threadPool = new ThreadPoolExecutor(100, 500, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        threadPool.allowCoreThreadTimeOut(true);
    }

    private static class ThreadHeartbeatHolder {
        public static ThreadHeartbeat instance = new ThreadHeartbeat();
    }

    public static ThreadHeartbeat getInstance() {
        return ThreadHeartbeatHolder.instance;
    }



    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {



                Socket socket = serverSocket.accept();
                //从线程池中取一个线程处理
                logger.info("Received a heartbeat from data ark...");
                DealSocket dealSocket = new DealSocket();
                dealSocket.setSocket(socket);
                dealSocket.setRdsService(rdsService);
                dealSocket.setEcsService(ecsService);
                dealSocket.setMdbService(mdbService);
                dealSocket.setDataArkService(dataArkService);
                dealSocket.setOssService(ossService);
                dealSocket.setAlarmLogService(alarmLogService);
                threadPool.execute(dealSocket);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                logger.error(e);
            }
        }

    }
}
