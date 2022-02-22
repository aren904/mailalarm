package cn.infocore.service;

import cn.infocore.entity.User;

public interface UserService {

	public User findById(Long userId);

	public User findByUuid(String userUuid);
	
}
