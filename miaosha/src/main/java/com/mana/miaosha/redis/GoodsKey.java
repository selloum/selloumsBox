package com.mana.miaosha.redis;


public class GoodsKey extends BasePrefix{

	private GoodsKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	//设置页面缓存的时间是1分钟，在缓存的同时保持页面的更新
	public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
	public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
	public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0, "gs");
}
