package cn.infocore.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;

//所有从socket中接收到的心跳，全部放入阻塞队列
@Deprecated
public class CachedQueue {
	private static final Logger logger = Logger.getLogger(CachedQueue.class);
	private static final int CAP=100;
	
	private BlockingQueue<GetServerInfoReturn>  queue;
	
	private CachedQueue() {
		queue=new LinkedBlockingQueue<GetServerInfoReturn>();
	}
	
	private static class CacheQueueHolder{
		public static CachedQueue instance=new CachedQueue();
	}
	
	public static CachedQueue getInstance() {
		return CacheQueueHolder.instance;
	}
	
	public void addIntoQueue(GetServerInfoReturn hrt) {
		try {
			this.queue.put(hrt);
		} catch (InterruptedException e) {
			logger.error(e.toString());
		}
	}
	
	public GetServerInfoReturn getOutFromQueue() {
		GetServerInfoReturn s=null;
		try {
			s=this.queue.take();
		} catch (InterruptedException e) {
			logger.error(e.toString());
		}
		return s;
	}

}
