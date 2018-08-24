package com.mana.miaosha.access;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/*
 * 接口限流防刷

思路：对接口做限流

可以把用户访问这个url的次数存入 redis中
做次数限制

key是 前缀+url路径+用户id

使用拦截器，拦截器中判断次数

实现只写一个注解，就可以对这个url判断
多少秒，多少次数，是否需要登录
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
	int seconds();
	int maxCount();
	boolean needLogin() default true;
}
