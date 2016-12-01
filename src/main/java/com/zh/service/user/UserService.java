package com.zh.service.user;

import java.util.List;

import com.zh.entity.PageBean;
import com.zh.entity.user.User;

public interface UserService {
	
	public List<User> findListPage(PageBean page);
	
	public List<User> findalllist();

}
