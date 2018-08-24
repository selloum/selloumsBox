package com.mana.miaosha.service;

import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mana.miaosha.dao.OrderDao;
import com.mana.miaosha.domin.MiaoshaOrder;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.domin.OrderInfo;
import com.mana.miaosha.redis.OrderKey;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.vo.GoodsVo;

@Service
public class OrderService {
	
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	RedisService redisService;
	
	//判断是否秒杀到了
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
		return redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+userId+"_"+goodsId, MiaoshaOrder.class);
	}
	
	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}

	@Transactional
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		orderDao.insert(orderInfo);
		
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderInfo.getId());
		miaoshaOrder.setUserId(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		//生成订单成功之后写入redis缓存
		 redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+user.getId()+"_"+goods.getId(), miaoshaOrder);
		
		return orderInfo;
	}

	
	
}

