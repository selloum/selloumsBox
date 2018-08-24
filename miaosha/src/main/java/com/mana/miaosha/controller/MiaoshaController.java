package com.mana.miaosha.controller;


import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mana.miaosha.access.AccessLimit;
import com.mana.miaosha.domin.MiaoshaOrder;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.rabbitmq.MQSender;
import com.mana.miaosha.rabbitmq.MiaoshaMessage;
import com.mana.miaosha.redis.GoodsKey;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.GoodsService;
import com.mana.miaosha.service.MiaoshaService;
import com.mana.miaosha.service.MiaoshaUserService;
import com.mana.miaosha.service.OrderService;
import com.mana.miaosha.vo.GoodsVo;




@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	
	@Autowired
	MQSender sender;
	
	private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();
	
	/**
	 * 系统初始化   把商品数量加载到Redis
	 * */
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null) {
			return;
		}
		for(GoodsVo goods : goodsList) {
			
			//把商品数量加载到Redis
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);//先初始化 每个商品都是false 就是还有
		}
	}

	/**
	 * QPS:1306
	 * 5000 * 10
	 * QPS: 2114
	 * */
	/**
	 * 
	 * Get：幂等的  无论调用多少次，从服务端返回的结果都是一样的，并且不会对服务端的数据产生影响。

		Post：不是幂等的，向服务端提交数据，对服务端的数据造成影响 
	 * 
	 * @param model
	 * @param user
	 * @param goodsId
	 * @param path
	 * @return
	 */
    @RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model,MiaoshaUser user,
    		
    	////RequestParam可以把保单隐含提交的input标签对应name属性的元素取出	
    		@RequestParam("goodsId")long goodsId,
    		
    	/*
    	 * 1.接口改造，带上 PathVariable参数

		   2.添加接口生成地址的接口

		   3.秒杀手动请求，先验证PathVariable
		   
		 *随机生成一个字符串，作为地址加在url上，然后生成的时候
			存入 redis缓存中，根据前端请求的url获取path。
			判断与缓存中的字符串是否一致，一致就认为对的。就正常
			秒杀，否则失败。
		 *
    	 */
    		@PathVariable("path")String path) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	
    	//验证path
    	boolean check=miaoshaService.checkPath(user,goodsId,path);
    	if(!check) {
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
    		
    	}
    	
    	
    	//内存标记，减少redis访问
    	/*
    	 * 接口优化很多基于Redis的缓存操作，当并发很高的时候，
    	 * 也会给Redis服务器带来很大的负担，如果可以减少对Redis服务器的访问，也可以达到的优化的效果。
    	 * 
    	 * 可以加一个内存map,标记对应商品的库存量是否还有，
    	 * 在访问Redis之前，在map中拿到对应商品的库存量标记，
    	 * 就可以不需要访问Redis 就可以判断没有库存了。
    	 * 
    	 * 
    	 * 1.生成一个map，并在初始化的时候，将所有商品的id为键，标记false 存入map中。
    	 * 2.在预减库存之前，从map中取标记，若标记为false,说明库存，还有，
    	 * 3.预减库存，当遇到库存不足的时候，将该商品的标记置为true,表示该商品的库存不足。
    	 * 这样，下面的所有请求，将被拦截，无需访问redis进行预减库存。
    	 * 
    	 */
    	
    	// 优化 库存之后的请求不访问redis 通过判断 对应 map 的值
    	boolean over = localOverMap.get(goodsId);
    	if(over) {
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//收到请求，Redis预减库存:
    	/*
    	 * 减少对数据库的访问，之前的减库存，
    	 * 直接访问数据库，读取库存，当高并发请求到来的时候，
    	 * 大量的读取数据有可能会导致数据库的崩溃
    	 * 
    	 * 思路：

1.系统初始化的时候，将商品库存加载到Redis 缓存中保存

 2.收到请求的时候,现在Redis中拿到该商品的库存值，进行库存预减，如果减完之后库存不足，直接返回逻辑Exception

 就不需要访问数据库再去减库存了，如果库存值正确，进行下一步

 3.将请求入队，立即给前端返回一个值，表示正在排队中，然后进行秒杀逻辑，后端队列进行秒杀逻辑，前端轮询后端发来的请求，如果秒杀成功，返回秒杀，成功，不成功就返回失败。

（后端请求 单线程 出队，生成订单，减少库存，走逻辑）前端同时轮询


		1.先将所有数据读出来，初始化到缓存中，并以 stock + goodid 的形成存入Redis,

2.在秒杀的时候，先进行预减库存检测，从redis中，利用decr 减去对应商品的库存，
如果库存小于0，说明此时 库存不足，则不需要访问数据库。直接抛出异常即可
    	 */
    	long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);//10
        /*这里判断不能小于等于，因为减去之后等于 说明还有是正常范围*/
    	if(stock < 0) {
    		 localOverMap.put(goodsId, true);//没有库存就设置 对应id 商品的map 为true
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//判断是否已经秒杀到了
    	//防止一个人秒杀多个商品，判断是否已经秒杀到了  
    	//==> 到OrderService中去查找(查找生成的秒杀类型的订单，这种订单必须是一个用户和一个特价商品关联产生的，
    	//且每个用户只能产生一个)   数据库索引
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//入队 
    	//请求入队，立即返回排队中（因为现在还不知道最终会成功还是失败）
    	MiaoshaMessage mm = new MiaoshaMessage();
    	mm.setUser(user);
    	mm.setGoodsId(goodsId);
    	sender.sendMiaoshaMessage(mm);
    	return Result.success(0);//排队中
    }
    	
//    	//判断库存
//    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
//    	int stock = goods.getStockCount();
//    	if(stock <= 0) {
//    		return Result.error(CodeMsg.MIAO_SHA_OVER);
//    	}
//    	//判断是否已经秒杀到了
//    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//    	if(order != null) {
//    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
//    	}
//    	//减库存 下订单 写入秒杀订单
    //（需要在一个事物当中去做，搞一个service，做一个MiaoshaService，然后进行一个Transaction操作）
//    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
   //为什么成功了以后要返回订单呢？
    //因为我们想秒杀成功之后直接进入到订单详情页
//        return Result.success(orderInfo);
//        
//    }
    
    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * 后台轮询功能的接口：
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
    		////RequestParam可以把保单隐含提交的input标签对应name属性的元素取出
    		@RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	////根据result的三种数值范围，来判断下一步的操作
    	long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
    	return Result.success(result);
    }

    //5s内最多访问5次
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    //先请求下获取path。之后拼接成秒杀地址
    public Result<String> getMiaoshaPath(HttpServletRequest request,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId,
    		@RequestParam(value="verifyCode",defaultValue="0")int verifyCode) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    
    	//check
    	boolean check=miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
    	if(!check) {
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
    	}
    	//利用用户id，商品id拼接为key同时也是不同的路径
    	String path=miaoshaService.createMiaoshaPath(user,goodsId);
    	return Result.success(path);
    }
	
    
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    //数据公式验证码添加
    /*
     * 点击秒杀之前，先输入验证码，分散用户的请求
     * 在每次秒杀的时候，要先判断这个验证码是否正确
     * 生成数字验证码并存入redis中，判断也是从redis中取出来判断
     */
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	try {
    		BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
    		OutputStream out = response.getOutputStream();
    		ImageIO.write(image, "JPEG", out);
    		out.flush();
    		out.close();
    		return null;
    	}catch(Exception e) {
    		e.printStackTrace();
    		return Result.error(CodeMsg.MIAOSHA_FAIL);
    	}
    

    }
    
}
