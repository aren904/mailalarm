package cn.infocore.main;

import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadConsume2 extends Thread{
	private static final Logger logger = Logger.getLogger(ThreadConsume2.class);
	private ThreadPoolExecutor consumePool;

	private ThreadConsume2() {
		consumePool= (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
		consumePool.setKeepAliveTime(10, TimeUnit.SECONDS);
		consumePool.allowCoreThreadTimeOut(true);
	}
	
	private static class ThreadConsumeHolder{
		public static ThreadConsume2 instance=new ThreadConsume2();
	}
	
	
	public static ThreadConsume2 getInstance() {
		return ThreadConsumeHolder.instance;
	}
	
	
	
	public void run() {
		GetServerInfoReturn hrt=null;
		while(true) {
			hrt=CachedQueue.getInstance().getOutFromQueue();
			logger.info("Start deal heartbeat..");
			consumePool.execute(new ProcessData(hrt));
		}
		
	}
	
}
