package cn.infocore.main;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

import cn.infocore.protobuf.StmStreamerDrManage.GetServerInfoReturn;

//所有从socket中接收到的心跳，全部放入阻塞队列
public class CachedQueue {
	private static final Logger logger = Logger.getLogger(CachedQueue.class);
	private static final int CAP=100;
	private static volatile CachedQueue instance=null;
	
	private   BlockingQueue<GetServerInfoReturn>  queue;
	
	private CachedQueue() {
		queue=new ArrayBlockingQueue<GetServerInfoReturn>(CAP);
	}
	
	public static CachedQueue getInstance() {
		if (instance==null) {
			synchronized (CachedQueue.class) {
				if (instance==null) {
					instance=new CachedQueue();
				}
			}
		}
		return instance;
	}
	
	public void addIntoQueue(GetServerInfoReturn hrt) {
		try {
			this.queue.put(hrt);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GetServerInfoReturn getOutFromQueue() {
		GetServerInfoReturn s=null;
		
		try {
			s=this.queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

}
