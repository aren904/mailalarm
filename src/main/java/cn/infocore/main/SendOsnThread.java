package cn.infocore.main;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.service.DataArkService;

/**
 * 定时通知stm服务发心跳：1分钟
 */
@Component
public class SendOsnThread extends Thread{
	
	private static final Logger logger = Logger.getLogger(SendOsnThread.class);

    @Autowired
    private DataArkService dataArkService;

    private ThreadPoolExecutor pool;
    
    private Long snapTime = 60000L;

    private boolean flag =false;
    
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
            logger.info("SendOsnThread is starting....");
        } catch (InterruptedException e) {
            logger.error("SendOsnThread interrupted",e);
        }
        super.run();
    }

    public void  startService() throws InterruptedException{
        while(true) {
            if (pool.getActiveCount()<1 && pool.getQueue().size()<1) {
                pool.execute(new NoticeIpToOsnstm(dataArkService));
            }
            Thread.sleep(snapTime);
        }
    }
}
