package com.zh.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zh.entity.PageBean;
import com.zh.entity.user.User;
import com.zh.mapper.user.UserMapper;
import com.zh.service.user.UserService;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;

	public List<User> findListPage(PageBean page) {
		return userMapper.findListPage(page);
	}

	public List<User> findalllist() {
		// TODO Auto-generated method stub
		return userMapper.findalllist();
	}
	
	

}
