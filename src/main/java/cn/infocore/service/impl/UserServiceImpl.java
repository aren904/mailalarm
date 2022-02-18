package cn.infocore.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.infocore.entity.User;
import cn.infocore.manager.UserManager;
import cn.infocore.mapper.UserMapper;
import cn.infocore.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private  static final Logger logger = Logger.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserManager userManager;
    
    @Override
	public User findById(Long userId) {
		return userManager.getById(userId);
	}

}
