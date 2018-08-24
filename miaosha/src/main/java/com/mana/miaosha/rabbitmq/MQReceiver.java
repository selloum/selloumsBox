package com.mana.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mana.miaosha.domin.MiaoshaOrder;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.GoodsService;
import com.mana.miaosha.service.MiaoshaService;
import com.mana.miaosha.service.MiaoshaUserService;
import com.mana.miaosha.service.OrderService;
import com.mana.miaosha.vo.GoodsVo;

@Service
public class MQReceiver {
	
	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	
	@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
	public void receive(String message) {
		log.info("receive message:"+message);//在接收消息的时候，就可以将字符串转换为对象了，然后对对象进行处理
		MiaoshaMessage mm  = RedisService.stringToBean(message, MiaoshaMessage.class);
		MiaoshaUser user = mm.getUser();
		long goodsId = mm.getGoodsId();
		
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		return;
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return;
    	}
    	//减库存 下订单 写入秒杀订单
    	miaoshaService.miaosha(user, goods);
	}
	
//	@RabbitListener(queues=MQConfig.QUEUE)
//	public void receive(String message) {
//		log.info("receive message :"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
//	public void receiveTopic1(String message) {
//		log.info("topic queue1 message :"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
//	public void receiveTopic2(String message) {
//		log.info("topic queue2 message :"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.HEADER_QUEUE)
//	public void receiveHeaderQueue(byte[] message) {
//		log.info(" header  queue message:"+new String(message));
//	}
}
