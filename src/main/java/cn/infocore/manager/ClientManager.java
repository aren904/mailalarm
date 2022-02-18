package cn.infocore.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.Client;
import cn.infocore.mapper.ClientMapper;

@Service
public class ClientManager extends ServiceImpl<ClientMapper,Client> {

    @Autowired
    private UserManager userManager;
    
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
     * 根据uuid获取有用该客户端的用户uuid列表
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
    
    /**
     * 更新客户端：OSS
     * @param uuid
     * @param client
     */
    public void updateClient(String uuid, Client client) {
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getUuId, uuid);
        this.update(client,queryWrapper);
    }

}
