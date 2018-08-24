package com.mana.miaosha.controller;



import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.mana.miaosha.domin.User;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.redis.UserKey;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.MiaoshaUserService;
import com.mana.miaosha.util.ValidatorUtil;
import com.mana.miaosha.vo.LoginVo;



@Controller
@RequestMapping("/login")
public class LoginController {

	private static Logger log=LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	MiaoshaUserService miaoshaUserService;
	
	//rest Api json
	@RequestMapping("/to_login")
	public String toLogin() {
		return "login";
	}
	
	@RequestMapping("/do_login")
	@ResponseBody
	//@Valid的参数后必须紧挨着一个BindingResult 参数，否则spring会在校验不通过时直接抛出异常
	public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
    	log.info(loginVo.toString());
    	//登录
    	String token = miaoshaUserService.login(response, loginVo);
    	return Result.success(token);
    }
	
	


}
