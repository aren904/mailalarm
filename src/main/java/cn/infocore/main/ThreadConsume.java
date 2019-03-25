package cn.infocore.main;

import java.util.concurrent.*;

import org.apache.log4j.Logger;

import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;

@Deprecated
public class ThreadConsume extends Thread{
	private static final Logger logger = Logger.getLogger(ThreadConsume.class);
	private ThreadPoolExecutor consumePool;
	
	private ThreadConsume() {
		consumePool=new ThreadPoolExecutor(50,100,1,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(100));
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
