package cn.infocore.main;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.infocore.service.DataArkService;
import cn.infocore.service.EmailAlarmService;
import cn.infocore.service.SnmpService;
import cn.infocore.service.UserService;

/**
 * 每隔5秒检查一次，如果无扫描线程在，则启动扫描
 * 扫描线程：判断数据方舟是否在线
 */
@Component
public class ThreadScanPoolService extends Thread {
	
	private static final Logger logger = Logger.getLogger(ThreadScanStreamer.class);
	
	private volatile boolean flag = false;
	
	private ThreadPoolExecutor pool;
	
	private Long snapTime = 5000L;
	
	@Autowired
    private DataArkService dataArkService;
	
	@Autowired
	private SnmpService mySnmpService;
	
	@Autowired
	private EmailAlarmService emailAlarmService;
	
	@Autowired
	private UserService userService;
	
	public ThreadScanPoolService init() {
		if (!flag) {
			flag = true;	
			//新建一个固定大小为1的线程池
			pool = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1));
			//无任务执行时回收线程
			pool.allowCoreThreadTimeOut(true);
		}
		return this;
	}
	
	@Override
	public void run() {
		init();
		try {
			startService();
			logger.info("ThreadScanPoolService is starting...."+dataArkService);
		} catch (InterruptedException e) {
			logger.error("ThreadScanPoolService interrupted",e);

		}
		super.run();
	}
	
	
	public void startService() throws InterruptedException{
		while(true) {
			if (pool.getActiveCount()<1 && pool.getQueue().size()<1) {
				ThreadScanStreamer scan=new ThreadScanStreamer();
				scan.setDataArkService(dataArkService);
				scan.setEmailAlarmService(emailAlarmService);
				scan.setMySnmpService(mySnmpService);
				scan.setUserService(userService);
				pool.execute(scan);
			}
			Thread.sleep(snapTime);
		}
	}
	
}
