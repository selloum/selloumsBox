package com.mana.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mana.miaosha.domin.User;
import com.mana.miaosha.rabbitmq.MQSender;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.redis.UserKey;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class DemoController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MQSender sender;
	
	//rest Api json    //controller中一般两种输出，一种时rest API的json输出，一种是页面输出
	//json输出要加注解@ResponseBody    code data json结构好处：仅知道code即可得到data信息或跳转页面
	
	//页面输出不加
	
	@RequestMapping("/")
	@ResponseBody
	public String home() {
		return "hello World!";
	}
	
//	@RequestMapping("/mq/topic")
//	@ResponseBody
//	public Result<String> topic() {
//		sender.sendTopic("hello mana");
//		return Result.success("hellomana");
//
//	}
//	
//	//swagger
//	@RequestMapping("/mq/fanout")
//	@ResponseBody
//	public Result<String> fanout() {
//		sender.sendFanout("hello mana");
//		return Result.success("hellomana");
//
//	}
//	
//	//swagger
//	@RequestMapping("/mq/header")
//	@ResponseBody
//	public Result<String> header() {
//		sender.sendHeader("hello mana");
//		return Result.success("hellomana");
//
//	}
//	
//	@RequestMapping("/mq")
//	@ResponseBody
//	public Result<String> mq() {
//		sender.send("hello mana");
//		return Result.success("hellomana");
//
//	}
	
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.success("hellomana");
//		return new Result(0,"success","hello,mana");
	}
	
	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError() {
		return Result.error(CodeMsg.SERVER_ERROR);
	}
	
	//页面
	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name","mana");
		return "hello";  //渲染hello模板
	}
	
	@RequestMapping("/db")
	@ResponseBody
	public Result<User> dbGet() {
		User user=userService.getById(1);
		return Result.success(user);
	}
	
	@Autowired
	RedisService redisService;
	
	@RequestMapping("/redis")
	@ResponseBody
	public Result<User> redisGet() {
		User  user  = redisService.get(UserKey.getById, ""+1, User.class); 
		return Result.success(user);
	}
	
	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> redisSet() {
		User user=new User();
		user.setId(1);
		user.setName("11111");
		redisService.set(UserKey.getById,""+1,user); //UserKey:id1
		return Result.success(true);
	}


}
