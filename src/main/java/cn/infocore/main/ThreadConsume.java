package cn.infocore.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;

public class ThreadConsume extends Thread{
	private static final Logger logger = Logger.getLogger(ThreadConsume.class);
	private static volatile ThreadConsume instance=null;
	private ExecutorService consumePool;
	
	private ThreadConsume() {
		consumePool=Executors.newCachedThreadPool();
	}
	
	public static ThreadConsume getInstance() {
		if (instance==null) {
			synchronized (ThreadConsume.class) {
				if (instance==null)
				instance=new ThreadConsume();
			}
		}
		return instance;
	}
	
	
	
	public void run() {
		GetServerInfoReturn hrt=null;
		while(true) {
			hrt=CachedQueue.getInstance().getOutFromQueue();
			/*if (hrt!=null) {
				logger.info(hrt);
			}*/
			logger.info("Start deal heartbeat..");
			consumePool.execute(new ProcessData(hrt));
			
		}
		
	}
	
}
