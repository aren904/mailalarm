package cn.infocore.manager;

import cn.infocore.dao.ClientMapper;
import cn.infocore.entity.ClientDo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ClientManager extends ServiceImpl<ClientMapper,ClientDo> {

    @Autowired
    UserManager userManager;


    public void updateClient(String uuid, ClientDo clientDo) {
        LambdaQueryWrapper<ClientDo> clientDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<ClientDo> queryWrapper = clientDoLambdaQueryWrapper.eq(ClientDo::getUuId, uuid);
        this.baseMapper.update(clientDo,queryWrapper);
    }

    public List<String> getUserIdByUuid(String uuid){
        LambdaQueryWrapper<ClientDo> clientDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据数据方舟uuid获取user_uuid,然后要到user表中根据user.id 找到user_uuid
        clientDoLambdaQueryWrapper.eq(ClientDo::getUuId,uuid);
        List<ClientDo> ClientDoList = this.list(clientDoLambdaQueryWrapper);
        LinkedList<String> UserUuId = new LinkedList<>();
        if(ClientDoList!=null){
            for (ClientDo clientDo:ClientDoList){
                String userId = clientDo.getUserId();
                String userUuid= userManager.getUserIdById(userId);
                UserUuId.add(userUuid);
            }
        }
        return  UserUuId;
    }
}
