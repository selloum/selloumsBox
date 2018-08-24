package com.mana.miaosha.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mana.miaosha.domin.MiaoshaOrder;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.domin.OrderInfo;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.GoodsService;
import com.mana.miaosha.service.MiaoshaService;
import com.mana.miaosha.service.MiaoshaUserService;
import com.mana.miaosha.service.OrderService;
import com.mana.miaosha.vo.GoodsVo;




@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	

	//rest Api json
	@RequestMapping("/info")
	@ResponseBody
    public Result<MiaoshaUser> info(Model model,MiaoshaUser user) {
    	return Result.success(user);
    	}
   
	

	
}
