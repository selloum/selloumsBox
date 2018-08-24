package com.mana.miaosha.vo;

import com.mana.miaosha.domin.OrderInfo;

/*
 * 开始订单详情静态化

    首先定义OrderDetailVo类：
 */
public class OrderDetailVo {
	private GoodsVo goods;
	private OrderInfo order;
	public GoodsVo getGoods() {
		return goods;
	}
	public void setGoods(GoodsVo goods) {
		this.goods = goods;
	}
	public OrderInfo getOrder() {
		return order;
	}
	public void setOrder(OrderInfo order) {
		this.order = order;
	}
	
	
}
