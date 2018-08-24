package com.mana.miaosha.redis;

public class AccessKey extends BasePrefix{

	//限流防刷  限制超时时间
	private AccessKey(Integer expireSeconds,String prefix) {
		super(expireSeconds,prefix);
	}

	public static AccessKey withExpired(int expireSeconds) {
		return new AccessKey(expireSeconds,"access");
	}

}