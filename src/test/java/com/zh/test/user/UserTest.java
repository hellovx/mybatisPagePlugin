package com.zh.test.user;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zh.entity.PageBean;
import com.zh.entity.user.User;
import com.zh.service.user.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml" })
public class UserTest {

	@Autowired
	private UserService userService;

	@Test
	public void listPageTest() {
		/*
		 * ApplicationContext context = new
		 * ClassPathXmlApplicationContext("spring/applicationContext.xml");
		 * userService = (UserService) context.getBean("userService");
		 */

		PageBean page = new PageBean();
		page.setPageSize(2);
		List<User> list = null;
		// userService.findListPage(page); // List<User>
		list = userService.findListPage(page);
		// list = userService.findalllist();
		System.out.println(page.getAllRow());
		System.out.println(page.getCurrentPage());
		System.out.println(page.getPageSize());
		System.out.println(page.getTotalPage());
		System.out.println(list.size());
		for (User user : list) {
			System.out.println(user.getName());
		}
	}

}
