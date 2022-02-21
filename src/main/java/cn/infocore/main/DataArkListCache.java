package cn.infocore.main;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.infocore.entity.DataArk;
import cn.infocore.service.DataArkService;

/**
 * 内存中维护的数据方舟的列表<uuid,ip>,顺便初始化维护数据方舟心跳的单例queue
 */
public class DataArkListCache {

    private static final Logger logger = Logger.getLogger(DataArkListCache.class);
    
	//维护的数据方舟的uuid-->ip列表
	private Map<String,String> data_ark_list=new ConcurrentHashMap<String, String>();
	
	private DataArkListCache dataArkList = null;
	
	private boolean inited = false;
	
	private DataArkListCache() {}
	
	private DataArkListCache init(DataArkService dataArkService) {
		if (this.inited == false||dataArkList ==null) {
			this.inited = true;
			logger.info("Init,Start get all data ark from database.");
			List<DataArk> dataArks=dataArkService.list();
			for (DataArk dataArk:dataArks) {
				this.data_ark_list.put(dataArk.getUuid(),dataArk.getIp());
				//同时初始化维护数据方舟掉线的列表
				HeartCache.getInstance().addHeartCache(dataArk.getUuid(), 0L);
			}
			logger.info("Succeed to get data ark,count:"+this.data_ark_list.size());
		}
		return this;
		
	}

	private static class DataArkListHolder{
		public static DataArkListCache instance=new DataArkListCache();
	}

	public static DataArkListCache getInstance(DataArkService dataArkService) {
		return DataArkListHolder.instance.init(dataArkService);
	}

	//添加
	public synchronized void addDataArk(String uuid,String ip) {
		data_ark_list.put(uuid, ip);
	}

	//移除
	public synchronized void removeDataArk(String uuid) {
		data_ark_list.remove(uuid);
	}

	//获取所有
	public synchronized Map<String,String> getData_ark_list() {
		return data_ark_list;
	}

}
