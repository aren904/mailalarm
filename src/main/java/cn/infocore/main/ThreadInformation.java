package cn.infocore.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

//接收来自强哥的通知
public class ThreadInformation extends Thread{
	private static final Logger logger=Logger.getLogger(ThreadInformation.class);
	private static final int C_PORT=23334;
	private ThreadPoolExecutor pool;
	
	private ThreadInformation() {
		pool= (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		pool.setKeepAliveTime(10, TimeUnit.SECONDS);
		pool.allowCoreThreadTimeOut(true);
	}
	
	private static class ThreadInformationHolder{
		public static ThreadInformation instance=new ThreadInformation();
	}
	
	public static ThreadInformation getInstance() {
		return ThreadInformationHolder.instance;
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
				logger.error(e);
			}			
		}
	}

	
}
