package com.mana.miaosha.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.mana.miaosha.domin.MiaoshaGoods;
import com.mana.miaosha.vo.GoodsVo;

@Mapper
public interface GoodsDao {
	
	@Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id")
	public List<GoodsVo> listGoodsVo();

	@Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
	public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

	
	//解决超卖问题，从数据库层面解决，通过唯一索引，指定唯一用户和唯一商品id
	@Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId}")
	public int reduceStock(MiaoshaGoods g);
	
}


