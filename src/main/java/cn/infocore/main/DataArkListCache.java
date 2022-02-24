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
    private static Map<String,String> data_ark_list = null;
    
    private static volatile DataArkListCache instance = null;
	
	private DataArkListCache() {}
	
	public static DataArkListCache getInstance(DataArkService dataArkService) {
		if (instance==null) {
			synchronized (DataArkListCache.class) {
                if (instance == null) {
                    instance = new DataArkListCache();
                    
                    logger.info("-------------Init,Start get all data ark from database.");
        			data_ark_list=new ConcurrentHashMap<String, String>();
        			
        			List<DataArk> dataArks=dataArkService.list();
        			for (DataArk dataArk:dataArks) {
        				data_ark_list.put(dataArk.getUuid(),dataArk.getIp());
        			}
        			logger.info("Succeed to get data ark,count:"+data_ark_list.size());
                }
            }
		}
		return instance;
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
