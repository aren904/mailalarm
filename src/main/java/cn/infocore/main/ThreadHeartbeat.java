package cn.infocore.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.service.RDSService;
@Component
public class ThreadHeartbeat extends Thread{
	private static final int PORT=23335;
	private ThreadPoolExecutor threadPool;
	private static final Logger logger=Logger.getLogger(ThreadHeartbeat.class);
	@Autowired
	DealSocket dealSocket;
	

	private ThreadHeartbeat() {
		threadPool=  new ThreadPoolExecutor(100,500,1,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(500),new ThreadPoolExecutor.CallerRunsPolicy());
		threadPool.allowCoreThreadTimeOut(true);
	}
	
	private static class ThreadHeartbeatHolder{
		public static ThreadHeartbeat instance=new ThreadHeartbeat();
	}
	
	public static ThreadHeartbeat getInstance() {
		return ThreadHeartbeatHolder.instance;
	}
	
	public void run() {
		ServerSocket serverSocket=null;
		try {
			serverSocket=new ServerSocket(PORT);
			while(true) {
				Socket socket=serverSocket.accept();
				//从线程池中取一个线程处理
				logger.info("Received a heartbeat from data ark...");
				dealSocket.setSocket(socket);
				threadPool.execute(dealSocket);
			}
		} catch (Exception e) {
			
		}finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		
	}
	
}
