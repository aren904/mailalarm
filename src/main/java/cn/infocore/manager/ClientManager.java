package cn.infocore.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.Client;
import cn.infocore.mapper.ClientMapper;

@Service
public class ClientManager extends ServiceImpl<ClientMapper,Client> {
	
	private static final Logger logger = Logger.getLogger(ClientManager.class);

    @Autowired
    private UserManager userManager;
    
    public Integer listCount(Long userId,Long dataArkId,int type) {
    	LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
    	queryWrapper.eq(Client::getUserId,userId).eq(Client::getDataArkId, dataArkId).eq(Client::getType, type);
    	return this.count(queryWrapper);
    }
    
    /**
     * 根据uuid获取有用该客户端的用户id列表
     * @param uuid
     * @return
     */
    public List<Long> findUserIdsByUuid(String uuid) {
    	LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
    	queryWrapper.eq(Client::getUuId,uuid);
    	List<Client> clients = this.list(queryWrapper);
    	List<Long> userIds=clients.stream().map(Client::getUserId).collect(Collectors.toList());
		return userIds;
	}
    
    /**
     * 根据uuid获取有用该客户端/关系的用户uuid列表
     * @param uuid
     * @return
     */
    public List<String> getUserUuidsByUuid(String uuid){
    	LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
    	queryWrapper.eq(Client::getUuId,uuid);
    	List<Client> clients = this.list(queryWrapper);
    	
        List<String> userUuIds = new ArrayList<>();
        for (Client client:clients){
            String userUuid= userManager.getById(client.getUserId()).getUuid();
            userUuIds.add(userUuid);
        }
        return userUuIds;
    }
    
	public void updateClient(Client client) {
		try {
			logger.debug("Update client:"+client.toString());
			LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(Client::getUuId,client.getUuId());
			this.update(client,queryWrapper);
		} catch (Exception e) {
			logger.error("Failed to update client:"+client.getUuId(),e);
		}
		
	}

}
