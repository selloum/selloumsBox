package com.mana.miaosha.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import com.alibaba.druid.util.StringUtils;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.redis.GoodsKey;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.result.Result;
import com.mana.miaosha.service.GoodsService;
import com.mana.miaosha.service.MiaoshaUserService;
import com.mana.miaosha.util.SpringWebContextUtil;
import com.mana.miaosha.vo.GoodsDetailVo;
import com.mana.miaosha.vo.GoodsVo;




@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	@Autowired
	ApplicationContext applicationContext;
	
	//rest Api json
	 @RequestMapping(value="/to_list", produces="text/html")
	    @ResponseBody  //MiaoshaUser传入使用config
	    //每个页面都需要用到user对象，user对象需要根据token从redis数据库中取出。
	    public String list(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user) {
	    	model.addAttribute("user", user);
	    	//取缓存
	    	////从redis数据库中获取数据
	    	String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
	    	if(!StringUtils.isEmpty(html)) {
	    		return html;
	    	}
	    	List<GoodsVo> goodsList = goodsService.listGoodsVo();
	    	model.addAttribute("goodsList", goodsList);
//	    	 return "goods_list";
	    	SpringWebContextUtil ctx = new SpringWebContextUtil(request,response,
	    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
	    	//手动渲染
	    	html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
	    	if(!StringUtils.isEmpty(html)) {
	    		redisService.set(GoodsKey.getGoodsList, "", html);//无论任何人访问，都是同意页面，粒度最大
	    	}
	    	return html;
	    }
	
	@RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
	@ResponseBody                          //url缓存，不同goodsId对应页面不同
	//针对不同的商品，对应着不同的商品详情，
	//不同的商品详情是根据url中的特定字符来区分的，
	//比如商品的id，在缓存的时候要缓存进去。
	public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
    		@PathVariable("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	
    	//取缓存                                                                                           goodsid要存入缓存
    	String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
    	if(!StringUtils.isEmpty(html)) {
    		return html;
    	}
    	//手动渲染
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	model.addAttribute("goods", goods);
    	
    	long startAt = goods.getStartDate().getTime();
    	long endAt = goods.getEndDate().getTime();
    	long now = System.currentTimeMillis();
    	
    	int miaoshaStatus = 0;
    	int remainSeconds = 0;
    	if(now < startAt ) {//秒杀还没开始，倒计时
    		miaoshaStatus = 0;
    		remainSeconds = (int)((startAt - now )/1000);
    	}else  if(now > endAt){//秒杀已经结束
    		miaoshaStatus = 2;
    		remainSeconds = -1;
    	}else {//秒杀进行中
    		miaoshaStatus = 1;
    		remainSeconds = 0;
    	}
    	model.addAttribute("miaoshaStatus", miaoshaStatus);
    	model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";
    	
    	SpringWebContextUtil ctx = new SpringWebContextUtil(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
    	html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
    	if(!StringUtils.isEmpty(html)) {
    		redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
    	}
    	return html;
    }
	/**
	 *
	 * **/
	
	@RequestMapping(value="/detail/{goodsId}")  
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
    		@PathVariable("goodsId")long goodsId) {
    	model.addAttribute("user", user);

    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	model.addAttribute("goods", goods);
    	
    	long startAt = goods.getStartDate().getTime();
    	long endAt = goods.getEndDate().getTime();
    	long now = System.currentTimeMillis();
    	
    	int miaoshaStatus = 0;
    	int remainSeconds = 0;
    	if(now < startAt ) {//秒杀还没开始，倒计时
    		miaoshaStatus = 0;
    		remainSeconds = (int)((startAt - now )/1000);
    	}else  if(now > endAt){//秒杀已经结束
    		miaoshaStatus = 2;
    		remainSeconds = -1;
    	}else {//秒杀进行中
    		miaoshaStatus = 1;
    		remainSeconds = 0;
    }
    	//页面静态化
    	GoodsDetailVo vo=new GoodsDetailVo();
    	vo.setGoods(goods);
    	vo.setUser(user);
    	vo.setMiaoshaStatus(miaoshaStatus);
    	vo.setRemainSeconds(remainSeconds);
    	
    	return Result.success(vo);
	}
}

	

