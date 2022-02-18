package cn.infocore.manager;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.User;
import cn.infocore.mapper.UserMapper;

@Service
public class UserManager extends ServiceImpl<UserMapper, User> {

	/**
	 * 根据id获取对象
	 * @param id
	 * @return
	 */
    public User getUserById(Long id){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,id);
        return this.getOne(queryWrapper);
    }
    
}
