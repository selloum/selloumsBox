package com.mana.miaosha.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.druid.util.StringUtils;
import com.mana.miaosha.access.UserContext;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.service.MiaoshaUserService;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver{

	
	@Autowired
	MiaoshaUserService userService;
	

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz=parameter.getParameterType();
		return clazz==MiaoshaUser.class;   //判断参数类型是否是MiaoshaUser
	}

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		//这个函数在拦截器之后执行，直接获取 拦截器中已经存入到ThreadLocal中的user即可
		return UserContext.getUser();
	}

	
}
