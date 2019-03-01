package cn.infocore.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;

public class ThreadConsume extends Thread{
	private static final Logger logger = Logger.getLogger(ThreadConsume.class);
	private ExecutorService consumePool;
	
	private ThreadConsume() {
		consumePool=Executors.newFixedThreadPool(50);
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
