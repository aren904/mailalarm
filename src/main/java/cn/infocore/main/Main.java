package cn.infocore.main;

import org.apache.log4j.Logger;

public class Main {
	//public static final ThreadConsume consum;
	public static final ThreadInformation information;
	public static final ThreadHeartbeat heartbeat;
	//public static final ThreadScanStreamer scan;
	protected static final  ThreadScanPoolService scanService; 
	private static final Logger logger=Logger.getLogger(Main.class);
	
	static {
		//consum=ThreadConsume.getInstance();
		information=ThreadInformation.getInstance();
		heartbeat=ThreadHeartbeat.getInstance();
		//scan=ThreadScanStreamer.getInstance();
		scanService= new ThreadScanPoolService();
	}
	
	//主函数入口
	public static void main(String[] args) {
		logger.info("Start CloudManager....");
		heartbeat.start();
		logger.info("Heartbeat is start....");
		//consum.start();
		//logger.info("Consum is start....");
		information.start();
		logger.info("Information is start....");
		
		
		
		scanService.start();		
		//scan.start();
		logger.info("Scan is start...");
		
		try {
			heartbeat.join();
			information.join();
			//consum.join();
			//scan.join();
			logger.info("CloudManager is stoped...");
		} catch (Exception e) {
			logger.warn("Failed occured...");
		}finally {
			System.exit(0);
		}
	}

}
