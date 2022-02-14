package cn.infocore.main;

import javax.annotation.PostConstruct;

//import cn.infocore.SimulatorSocket.TestSocket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class MailMain {
    //public static final ThreadConsume consum;
    @Autowired
    public ThreadInformation information;
    @Autowired
    public ThreadHeartbeat heartbeat;
//    @Autowired
//    public NoticeIpToOsnstm noticeIpToOsnstm;

    @Autowired
    protected ThreadScanPoolService scanService;

    @Autowired
    SendOsnThread sendOsnThread;

    @Autowired
    CaptureDataArkIp captureDataArkIp;

    private static final Logger logger = Logger.getLogger(MailMain.class);

    private int PROCESS_NUM = Runtime.getRuntime().availableProcessors();

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
//        暂时像别的线程一样去每次执行
        //String s = captureDataArkIp.GetDataArkIp();

        System.out.println("start====start");
        logger.info("Start SendingIp to osnstm ...");
//       ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(PROCESS_NUM);

//        //注意线程池相关代码里面不能@Autowired不然会不执行里面的业务逻辑
//        scheduledExecutorService.scheduleAtFixedRate(new NoticeIpToOsnstm1(captureDataArkIp),1,60, TimeUnit.SECONDS);


//        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(PROCESS_NUM, new ThreadPoolExecutor.DiscardPolicy());
//        scheduledThreadPoolExecutor.scheduleAtFixedRate(new NoticeIpToOsnstm1(captureDataArkIp),1,60, TimeUnit.SECONDS);

//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(PROCESS_NUM, PROCESS_NUM * 2, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());


        sendOsnThread.start();

//        noticeIpToOsnstm.start();
        logger.info("Start CloudManager....");
        heartbeat.start();
        logger.info("Heartbeat is start....");
        information.start();
        logger.info("Information is start....");
        scanService.start();
        //scan.start();
//        ScheduledExecutorService TimeKill = Executors.newScheduledThreadPool(PROCESS_NUM);
//        TimeKill.scheduleAtFixedRate(new ThreadScanStreamer(),1,3*60,TimeUnit.SECONDS );
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
//        threadPoolExecutor.execute(new ThreadScanStreamer());
        logger.info("Scan is start...");

        try {
//            noticeIpToOsnstm1.join();
//            noticeIpToOsnstm.join();
            sendOsnThread.join();
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
