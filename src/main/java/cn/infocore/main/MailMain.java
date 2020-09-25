package cn.infocore.main;

import javax.annotation.PostConstruct;

//import cn.infocore.SimulatorSocket.TestSocket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailMain {
    //public static final ThreadConsume consum;
    @Autowired
    public ThreadInformation information;
    @Autowired
    public ThreadHeartbeat heartbeat;
    //public static final ThreadScanStreamer scan;
    @Autowired
    protected ThreadScanPoolService scanService;

    private static final Logger logger = Logger.getLogger(MailMain.class);

//	static {
//		//consum=ThreadConsume.getInstance();
//		information=ThreadInformation.getInstance();
//		heartbeat=ThreadHeartbeat.getInstance();
//		//scan=ThreadScanStreamer.getInstance();
//		scanService= new ThreadScanPoolService();
//	}
    //主函数入口
    //@PostConstruct
    public void start() {
        System.out.println("start====start");
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
//            testSocket.join();
            information.join();
            //consum.join();
//            scan.join();
            logger.info("CloudManager is stopped...");
        } catch (Exception e) {
            logger.warn("Failed occurred...");
        } finally {
            System.exit(0);
        }
    }

}
