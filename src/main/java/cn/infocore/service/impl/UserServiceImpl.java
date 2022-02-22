package cn.infocore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.infocore.entity.User;
import cn.infocore.manager.UserManager;
import cn.infocore.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserManager userManager;
    
    @Override
	public User findById(Long userId) {
		return userManager.getById(userId);
	}

	@Override
	public User findByUuid(String userUuid) {
		return userManager.findUserByUuid(userUuid);
	}

}
