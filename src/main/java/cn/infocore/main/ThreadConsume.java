package cn.infocore.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;

public class ThreadConsume extends Thread{
	private static final Logger logger = Logger.getLogger(ThreadConsume.class);
	private ThreadPoolExecutor consumePool;
	
	private ThreadConsume() {
		consumePool= (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
		consumePool.setKeepAliveTime(10, TimeUnit.SECONDS);
		consumePool.allowCoreThreadTimeOut(true);
	}
	
	private static class ThreadConsumeHolder{
		public static ThreadConsume instance=new ThreadConsume();
	}
	
	
	public static ThreadConsume getInstance() {
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
