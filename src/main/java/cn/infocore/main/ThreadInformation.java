package cn.infocore.main;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.service.DataArkService;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.service.SnmpService;
import cn.infocore.service.UserService;
import cn.infocore.utils.Utils;

/**
 * 接收来自管理平台的请求
 * 协议：cloud-manager-alarm.proto
 * 此版本修改了通信方式，配合管理平台修改为AFUNIXSocket
 */
@Component
public class ThreadInformation extends Thread {
	
    private static final Logger logger = Logger.getLogger(ThreadInformation.class);
    
	private static final String MAIL_ALARM_SOCK="/var/run/mailalarm.sock";
	
    private ThreadPoolExecutor pool;
    
    @Autowired
    private DataArkService dataArkService;
    
    @Autowired
    private SnmpService mySnmpService;
    
    @Autowired
    private EmailAlarmService emailAlarmService;
    
    @Autowired
    private UserService userService;

    private ThreadInformation() {
    	logger.debug("Create pool for ThreadInformation.");
    	//创建一个可重用，固定线程数为10的线程池
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        //空闲线程超过10s便销毁
        pool.setKeepAliveTime(10, TimeUnit.SECONDS);
        //允许空闲核心线程超时
        pool.allowCoreThreadTimeOut(true);
    }
    
    private static class ThreadInformationHolder {
        public static ThreadInformation instance = new ThreadInformation();
    }
    
    public static ThreadInformation instance() {
        return ThreadInformationHolder.instance;
    }

    @Override
    public void run() {
        AFUNIXServerSocket server=null;
        try {
            final File socketFile = new File(MAIL_ALARM_SOCK);

            if (socketFile.exists() && (! socketFile.isDirectory()) && socketFile.canWrite()) {
                logger.error(Utils.fmt("Socket file [%s] already exists, remove.", MAIL_ALARM_SOCK));
                socketFile.delete();
            }

            logger.info("ThreadInformation initialized for CloudManager.");
            server = AFUNIXServerSocket.newInstance();
            server.bind(new AFUNIXSocketAddress(socketFile));

            while (! Thread.interrupted()) {
                logger.info("ThreadInformation waiting for new incoming connection.");
                Socket incoming = server.accept();
                logger.info("ThreadInformation new incoming connection.");
                DealInformation dealInfo=new DealInformation(incoming);
                dealInfo.setDataArkService(dataArkService);
                dealInfo.setMySnmpService(mySnmpService);
                dealInfo.setEmailAlarmService(emailAlarmService);
                dealInfo.setUserService(userService);
                pool.execute(dealInfo);
            }

            logger.error("ThreadInformation stopped.");
        } catch (IOException e) {
            logger.error("ThreadInformation socket shutdown unexpectedly, halt! e:"+e);
            if(server!=null){
                try {
                    server.close();
                } catch (IOException e1) {}
            }
            System.exit(1);
        }

    }
}
