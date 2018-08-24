package com.mana.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix{  //模板模式---抽象类
	
	private int expireSeconds;
	
	private String prefix;
	
	public BasePrefix(String prefix) {//0代表永不过期
		this(0, prefix);
	}
	
	public BasePrefix( int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}
	
	//对接口的简单实现
	public int expireSeconds() {//默认0代表永不过期
		return expireSeconds;
	}

	public String getPrefix() {
		String className = getClass().getSimpleName();  //通过类名查找，例如UserKey、GoodsKey...
		return className+":" + prefix;
	}

}
