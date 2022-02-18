package cn.infocore.service;

import java.util.List;

import cn.infocore.dto.DataArkDTO;
import cn.infocore.entity.DataArk;

public interface DataArkService  {

	/**
	 * 更新数据方舟
	 * @param data_ark
	 */
    public void update(DataArkDTO data_ark);
    
    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    public DataArk findByUuid(String uuid);

    /**
     * 获取当前数据方舟的ip列表
     * @return
     */
	public List<String> findIps();

	/**
	 * 根据uuid更新指定数据方舟状态：在线离线
	 * @param uuid
	 * @param online
	 */
	public void updateDataArkStatus(String uuid, boolean online);
    
}
