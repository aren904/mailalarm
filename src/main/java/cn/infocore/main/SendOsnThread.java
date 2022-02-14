package cn.infocore.main;

import StmStreamerDrManage.StreamerClouddrmanage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class SendOsnThread extends Thread{

    @Autowired
    CaptureDataArkIp captureDataArkIp;

    private ThreadPoolExecutor pool;
    private Long snapTime = 60000L;

    private static final Logger logger = Logger.getLogger(SendOsnThread .class);
    boolean flag =false;
    public SendOsnThread init() {
        if (!flag) {
            flag = true;
            pool = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1));
            pool.allowCoreThreadTimeOut(true);
        }
        return this;
    }


    @Override
    public void run() {
        init();
        try {
            startService();
            logger.info("SendOsnstm is starting....");
        } catch (InterruptedException e) {
            logger.error("scan thread interrupted",e);

        }
        super.run();
    }


    public void  startService() throws InterruptedException{

        while(true) {
            if (pool.getActiveCount()<1 && pool.getQueue().size()<1) {
                pool.execute(new NoticeIpToOsnstm1(captureDataArkIp));
//				pool.execute(new ThreadScanStreamer(captureDataArkIp));
            }
            Thread.sleep(snapTime);
        }
    }
}
