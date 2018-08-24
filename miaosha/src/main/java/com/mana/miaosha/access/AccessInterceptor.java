package com.mana.miaosha.access;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.OutputStream;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.redis.AccessKey;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.MiaoshaUserService;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			MiaoshaUser user = getUser(request, response);
			UserContext.setUser(user);//将用户保存在ThreadLoal中  新建类UserContext
			HandlerMethod hm = (HandlerMethod)handler;
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			if(accessLimit == null) {
				//true表示验证通过，注解为空，说明不需要拦截（也就是根本不需要验证，等于验证通过)
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			String key = request.getRequestURI();
			if(needLogin) {
				if(user == null) {
					render(response, CodeMsg.SESSION_ERROR);
					return false;
				}
				key += "_" + user.getId();
			}else {
				//do nothing
			}
			//martine flower 重构-改善既有代码的设计
			AccessKey ak = AccessKey.withExpired(seconds);
			Integer count = redisService.get(ak, key, Integer.class);
	    	if(count  == null) {
	    		 redisService.set(ak, key, 1);
	    	}else if(count < maxCount) {
	    		 redisService.incr(ak, key);
	    	}else {
	    		render(response, CodeMsg.ACCESS_LIMIT_REACHED);
	    		return false;
	    	}
		}
		return true;
	}
	
	private void render(HttpServletResponse response, CodeMsg cm)throws Exception {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str  = JSON.toJSONString(Result.error(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	private String getCookieValue(HttpServletRequest request, String cookiName) {
		Cookie[]  cookies = request.getCookies();
		if(cookies == null || cookies.length <= 0){
			return null;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookiName)) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(MiaoshaUserService.COOK1_TOKEN_NAME);
		String cookieToken = getCookieValue(request, MiaoshaUserService.COOK1_TOKEN_NAME);
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		return userService.getByToken(response, token);
	}
}
