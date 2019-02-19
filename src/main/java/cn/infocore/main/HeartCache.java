package cn.infocore.main;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class HeartCache {
	private static final Logger logger=Logger.getLogger(HeartCache.class);
	
	private static volatile HeartCache heartCache=null;
	//string-->数据方舟的uuid  long--->最近一次心跳过来的时间
	private Map<String, Long> cache=null;
	
	private HeartCache() {
		cache=new ConcurrentHashMap<String, Long>();
		logger.info("Init HeartCache successed.");
	}
	
	public static HeartCache getInstance() {
		if (heartCache==null) {
			synchronized (HeartCache.class) {
				if (heartCache==null) {
					heartCache=new HeartCache();
				}
			}
		}
		return heartCache;
	}
	
	public synchronized void addHeartCache(String uuid,Long time) {
		cache.put(uuid, time);
	}
	//移除
	public synchronized void removeHeartCache(String uuid) {
		cache.remove(uuid);
	}
	//获取所有
	public synchronized Map<String,Long> getAllCacheList() {
		return cache;
	}
	
	
	
}
