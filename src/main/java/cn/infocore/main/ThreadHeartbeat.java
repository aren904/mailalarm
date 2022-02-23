package cn.infocore.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.service.AlarmLogService;
import cn.infocore.service.ClientBackupService;
import cn.infocore.service.ClientService;
import cn.infocore.service.DataArkService;
import cn.infocore.service.EcsService;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.service.MetaService;
import cn.infocore.service.OssService;
import cn.infocore.service.QuotaService;
import cn.infocore.service.RdsService;
import cn.infocore.service.UserService;

/**
 * 负责接收来自数据方舟端（stm）发来的心跳，心跳1分钟发一次
 * 协议：streamer_clouddrmanage.proto->GetServerInfoReturn
 */
@Component
public class ThreadHeartbeat extends Thread {
	
	private static final Logger logger = Logger.getLogger(ThreadHeartbeat.class);
	
    private static final int PORT = 23335;
    
    private ThreadPoolExecutor threadPool;
    
    @Autowired
    private static RdsService rdsService;
    
    @Autowired
    private EcsService ecsService;
    
    @Autowired
    private MetaService metaService;
    
    @Autowired
    private DataArkService dataArkService;
    
    @Autowired
    private OssService ossService;
    
    @Autowired
    private AlarmLogService alarmLogService;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private QuotaService quotaService;
    
    @Autowired
    private EmailAlarmService emailAlarmService;
    
    @Autowired
    private ClientBackupService clientBackupService;
    
    public ThreadHeartbeat() {}
    
    @PostConstruct
    public void init(){
    	logger.debug("Create pool for ThreadHeartbeat."+dataArkService);
    	//新建一个100-500的线程池
        threadPool = new ThreadPoolExecutor(100, 500, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        //无任务执行时回收线程
        threadPool.allowCoreThreadTimeOut(true);
    }
    
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                //从线程池中取一个线程处理
                logger.info("------------------Receiving a heartbeat from osnstm..."+socket.getInetAddress().getHostAddress());
                DealSocket dealSocket = new DealSocket();
                dealSocket.setSocket(socket);
                dealSocket.setRdsService(rdsService);
                dealSocket.setEcsService(ecsService);
                dealSocket.setMetaService(metaService);
                dealSocket.setDataArkService(dataArkService);
                dealSocket.setOssService(ossService);
                dealSocket.setAlarmLogService(alarmLogService);
                dealSocket.setClientService(clientService);
                dealSocket.setUserService(userService);
                dealSocket.setQuotaService(quotaService);
                dealSocket.setClientBackupService(clientBackupService);
                dealSocket.setEmailAlarmService(emailAlarmService);
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
