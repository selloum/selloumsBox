package com.mana.miaosha.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import com.mana.miaosha.domin.MiaoshaOrder;
import com.mana.miaosha.domin.OrderInfo;


@Mapper
public interface OrderDao {
	
	 /**
     * 对于多个参数来说，每个参数之前都要加上@Param注解，
     * 要不然会找不到对应的参数进而报错
     */
	@Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

	@Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
			+ "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
	//        数据库中列名称       domin对象中与数据库对应的参数名称   参数类型                          查出上次插入的id号
	@SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
	public long insert(OrderInfo orderInfo);
	
	@Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
	public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

	@Select("select * from order_info where id = #{orderId}")
	public OrderInfo getOrderById(@Param("orderId")long orderId);
	

	
}
