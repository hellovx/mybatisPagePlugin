package com.zh.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zh.entity.PageBean;
import com.zh.entity.user.User;
import com.zh.service.user.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;

	@RequestMapping("/userlist")
	public String userlist(Model model, PageBean page, @RequestParam Map<String, Object> params) {
		/* PageBean page=new PageBean(); */
		System.out.println(page.getCurrentPage());
		page.setParams(params);
		List<User> list = userService.findListPage(page);
		page.setList(list);
		model.addAttribute("page", page);
		return "/user/userlist";
	}

}
