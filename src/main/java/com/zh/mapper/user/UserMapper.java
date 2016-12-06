package com.zh.mapper.user;

import java.util.List;

import com.zh.entity.PageBean;
import com.zh.entity.user.User;

public interface UserMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(User record);

	int insertSelective(User record);

	User selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(User record);

	int updateByPrimaryKey(User record);

	public List<User> findalllist();

	public List<User> findListPage(PageBean page);

}