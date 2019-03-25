package cn.infocore.main;



import org.apache.log4j.Logger;

public class Main {
	
	private static final Logger logger=Logger.getLogger(Main.class);
	
	//public static final ThreadConsume consum;
	public static final ThreadInformation information;
	public static final ThreadHeartbeat heartbeat;
	public static final ThreadScanStreamer scan;
	
	static {
		//consum=ThreadConsume.getInstance();
		information=ThreadInformation.getInstance();
		heartbeat=ThreadHeartbeat.getInstance();
		scan=ThreadScanStreamer.getInstance();
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
		
		scan.start();
		logger.info("Scan is start...");
		
		try {
			heartbeat.join();
			information.join();
			//consum.join();
			scan.join();
			logger.info("CloudManager is stoped...");
		} catch (Exception e) {
			logger.warn("Failed occured...");
		}finally {
			System.exit(0);
		}
		
		
	}

}
