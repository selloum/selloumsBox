package com.mana.miaosha.redis;

//订单key
//为秒杀做优化，为秒杀订单加缓存
public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
	}
	
	public static OrderKey getMiaoshaOrderByUidGid=new OrderKey("moug");
}
