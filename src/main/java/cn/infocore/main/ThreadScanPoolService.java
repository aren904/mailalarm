package cn.infocore.main;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
@Component
public class ThreadScanPoolService   extends Thread {

	volatile boolean flag = false;
	private ThreadPoolExecutor pool;
	private Long snapTime = 5000L;
	
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);

	
	
	public ThreadScanPoolService init() {
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
			logger.info("ScanService is starting....");
		} catch (InterruptedException e) {
			logger.error("scan thread interrupted",e);

		}
		super.run();
	}
	
	
	public void  startService() throws InterruptedException{
		
		while(true) {
			if (pool.getActiveCount()<1 && pool.getQueue().size()<1) {
				pool.execute(new ThreadScanStreamer());
			}
			Thread.sleep(snapTime);
		}
	}
	
}
