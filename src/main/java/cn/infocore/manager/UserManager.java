package cn.infocore.manager;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.User;
import cn.infocore.mapper.UserMapper;

@Service
public class UserManager extends ServiceImpl<UserMapper, User> {

    public User findUserByUuid(String uuid){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUuid,uuid);
        return this.getOne(queryWrapper);
    }

}
