package com.mana.miaosha.redis;

public interface KeyPrefix {  //模板模式---接口
	
	public int expireSeconds();  //有效期
	
	public String getPrefix();  //前缀
	
}
