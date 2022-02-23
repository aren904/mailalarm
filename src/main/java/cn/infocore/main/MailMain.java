package cn.infocore.main;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 主程序入口
 */
@Component
public class MailMain {
	
	private static final Logger logger = Logger.getLogger(MailMain.class);
	
	//接收管理平台请求
    @Autowired
    public ThreadInformation information;
    
    //接收stm服务心跳
    @Autowired
    public ThreadHeartbeat heartbeat;
    
    //定时扫描检测更新数据方舟状态
    @Autowired
    protected ThreadScanPoolService scanService;
    
    @Autowired
    protected ThreadSendHeartbeatRequest request;

    //主函数入口
    public void start() {
    	logger.info("Mailalarm launched.");
    	
        heartbeat.start();
        logger.info("ThreadHeartbeat is start....");
        
        information.start();
        logger.info("ThreadInformation is start....");
        
        scanService.start();
        logger.info("ThreadScanPoolService is start...");
        
        request.start();
        logger.info("ThreadSendHeartbeatRequest is start...");

        try {
            heartbeat.join();
            information.join();
            scanService.join();
            request.join();
            logger.info("Mailalarm is stopped");
        } catch (Exception e) {
        	logger.warn("Application main thread is interrupted.", e);
        } finally {
            System.exit(0);
        }
    }

}
