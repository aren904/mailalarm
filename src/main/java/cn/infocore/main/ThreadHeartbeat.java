package cn.infocore.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class ThreadHeartbeat extends Thread{

	private static final Logger logger=Logger.getLogger(ThreadHeartbeat.class);
	private static final int PORT=23335;
	private static volatile ThreadHeartbeat instance=null;
	private ExecutorService threadPool;
	
	private ThreadHeartbeat() {
		threadPool=Executors.newCachedThreadPool();
	}
	
	public static ThreadHeartbeat getInstance() {
		if (instance==null) {
			synchronized (ThreadHeartbeat.class) {
				if (instance==null) {
					instance=new ThreadHeartbeat();
				}
			}
		}
		return instance;
		
	}
	
	
	public void run() {
		ServerSocket serverSocket=null;
		try {
			serverSocket=new ServerSocket(PORT);
			while(true) {
				Socket socket=serverSocket.accept();
				//从线程池中取一个线程处理
				logger.info("Received a heartbeat from data ark...");
				threadPool.execute(new DealSocket(socket));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
