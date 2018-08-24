package com.mana.miaosha.access;


import com.mana.miaosha.domin.MiaoshaUser;
//ThreadLocal ，多线程时线程安全的一种访问方式，
//跟当前的线程绑定，如果是多线程，每个线程里面单独存一份，拦截器获取了用户
public class UserContext {
	private static ThreadLocal<MiaoshaUser> userHolder=new ThreadLocal<MiaoshaUser>();
	
	public static void setUser(MiaoshaUser user) {
		userHolder.set(user);
	}
	
	public static MiaoshaUser getUser() {
		return userHolder.get();
	}
	
}
