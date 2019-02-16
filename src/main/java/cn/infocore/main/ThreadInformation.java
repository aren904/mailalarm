package cn.infocore.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;


//接收来自强哥的通知
public class ThreadInformation extends Thread{
	private static final Logger logger=Logger.getLogger(ThreadInformation.class);
	private static volatile ThreadInformation instance=null;
	private static final int C_PORT=23334;
	private ExecutorService pool;
	
	private ThreadInformation() {
		pool=Executors.newCachedThreadPool();
	}
	
	public static ThreadInformation getInstance() {
		if (instance==null) {
			synchronized (ThreadInformation.class) {
				if (instance==null) {
					instance=new ThreadInformation();
				}
			}
		}
		
		return instance;
	}
	
	//测试邮件通过把config直接传递过来，不需要读取数据库
	
	public void run() {
		ServerSocket server=null;
		try {
			server=new ServerSocket(C_PORT);
			logger.info("ThreadInformation start.....");
			while(true) {
				Socket socket=server.accept();
				logger.info("Recived information...");
				pool.execute(new DealInformation(socket));
			}
		} catch (Exception e) {
			logger.error("Exception happened:"+e);
		}finally {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}

	
}
