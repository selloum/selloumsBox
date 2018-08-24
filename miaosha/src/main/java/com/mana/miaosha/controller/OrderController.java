package com.mana.miaosha.controller;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.sql.visitor.SchemaStatVisitor.OrderByStatVisitor;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.domin.OrderInfo;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.GoodsService;
import com.mana.miaosha.service.MiaoshaUserService;
import com.mana.miaosha.service.OrderService;
import com.mana.miaosha.vo.GoodsVo;
import com.mana.miaosha.vo.OrderDetailVo;





@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;
	
	//rest Api json
	@RequestMapping("/detail")
	@ResponseBody
//	@NeedLogin
	//首先分析需求，订单详情中需要订单相关信息和商品相关信息
    public Result<OrderDetailVo> info(Model model,MiaoshaUser user,
    		@RequestParam("orderId")long orderId) {
    	if(user==null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	//通过订单号找到订单
    	OrderInfo order=orderService.getOrderById(orderId);
    	if(order==null) {
    		return Result.error(CodeMsg.ORDER_NOT_EXITS);
    	}
    	//通过订单找到商品id
    	long goodsId=order.getGoodsId();
    	//通过商品id找到商品（通过商品Service）
    	GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
    	
    	OrderDetailVo vo=new OrderDetailVo();
    	vo.setGoods(goods);
    	vo.setOrder(order);
    	
    	return Result.success(vo);
    	
	}
}
