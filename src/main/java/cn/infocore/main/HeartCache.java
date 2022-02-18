package cn.infocore.main;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class HeartCache {
	
	private static final Logger logger=Logger.getLogger(HeartCache.class);
	
	//string-->数据方舟的uuid  long--->最近一次心跳过来的时间
	private Map<String, Long> cache=null;
	
	private HeartCache() {
		cache=new ConcurrentHashMap<String, Long>();
		logger.info("Init HeartCache is successful.");
	}
	
	private static class HeartCacheHolder{
		public static HeartCache instance=new HeartCache();
	}
	
	public static HeartCache getInstance() {
		return HeartCacheHolder.instance;
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
