package com.mana.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mana.miaosha.dao.GoodsDao;
import com.mana.miaosha.domin.MiaoshaGoods;
import com.mana.miaosha.vo.GoodsVo;

@Service
public class GoodsService {
	
	@Autowired
	GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	public boolean reduceStock(GoodsVo goods) {
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		int ret=goodsDao.reduceStock(g);
		return ret>0;
	}
	
	
	
}
