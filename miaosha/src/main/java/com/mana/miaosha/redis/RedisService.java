package com.mana.miaosha.redis;

import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service  //提供redis服务
public class RedisService {
	@Autowired
	JedisPool jedisPool;

	
	/**
	 * 获取当个对象
	 * */
	
	//选择前缀，键和参数的某种类型
	//使用前缀区分不同模块数据
 public <T> T get(KeyPrefix prefix,String key, Class<T> clazz) {
	 
	 Jedis jedis=null;
	 try {
		 jedis=jedisPool.getResource();
		 //生成真正的key
		 String realKey=prefix.getPrefix()+key;
		 String str=jedis.get(realKey);  //get方法返回string类型
		 T t= stringToBean(str,clazz);  //将字符串转化为Bean对象
		 return t;
	 }finally {
		returnToPool(jedis);
	}
	 
 }
 
	/**
	 * 设置对象
	 * */
public <T> boolean set(KeyPrefix prefix,String key, T value) {
	 
	 Jedis jedis=null;
	 try {
		 jedis=jedisPool.getResource();
		 String str=beanToString(value);
		 if(str==null||str.length()<=0) {
			 return false;
		 }
		 //生成真正的key
		 String realKey=prefix.getPrefix()+key;  //类名拼上prefix+key
		 int seconds=prefix.expireSeconds();  //设置有效期
		 if(seconds<=0) {
			 jedis.set(realKey,str);
		 }else {
			jedis.setex(realKey, seconds, str);  //setex=set+expire
		}

		 return true;
	 }finally {
		returnToPool(jedis);
	}
	 
 }

/**
 * 判断key是否存在
 * */
public <T> boolean exists(KeyPrefix prefix, String key) {
	 Jedis jedis = null;
	 try {
		 jedis =  jedisPool.getResource();
		//生成真正的key
		 String realKey  = prefix.getPrefix() + key;
		return  jedis.exists(realKey);
	 }finally {
		  returnToPool(jedis);
	 }
}

/**
 * 增加值
 * */
public <T> Long incr(KeyPrefix prefix, String key) {
	 Jedis jedis = null;
	 try {
		 jedis =  jedisPool.getResource();
		//生成真正的key
		 String realKey  = prefix.getPrefix() + key;
		return  jedis.incr(realKey);
	 }finally {
		  returnToPool(jedis);
	 }
}

/**
 * 减少值
 * */
public <T> Long decr(KeyPrefix prefix, String key) {
	 Jedis jedis = null;
	 try {
		 jedis =  jedisPool.getResource();   //返回一个jedis
		//生成真正的key
		 String realKey  = prefix.getPrefix() + key;
		return  jedis.decr(realKey);
	 }finally {
		  returnToPool(jedis);
	 }
}

//删除
public boolean delete(KeyPrefix prefix, String key) {
	 Jedis jedis = null;
	 try {
		 jedis =  jedisPool.getResource();
		//生成真正的key
		 String realKey  = prefix.getPrefix() + key;
		long ret= jedis.del(realKey);
		return ret>0;
	 }finally {
		  returnToPool(jedis);
	 }
}
 
 public static <T> String beanToString(T value) {
	// TODO Auto-generated method stub
	if(value==null) {
		return null;
	}
	
	Class<?> clazz=value.getClass();  //获取value的类型
	
	if(clazz==int.class||clazz==Integer.class) {
		return ""+value;
	}else if(clazz==String.class) {
		return (String)value;
	}else if(clazz==long.class||clazz==Long.class) {
		return ""+value;
	}else {
		return JSON.toJSONString(value);   //转化为string类型
	}
	
}

@SuppressWarnings("unchecked")  //不出警告
public static <T> T stringToBean(String str,Class<T> clazz) { //使用fastjson
	// TODO Auto-generated method stub
	if(str==null||str.length()<=0||clazz==null) {
		return null;
	}
	
	if(clazz==int.class||clazz==Integer.class) {
		return (T)Integer.valueOf(str);
	}else if(clazz==String.class) {
		return (T)str;
	}else if(clazz==long.class||clazz==Long.class) {
		return (T)Long.valueOf(str);
	}else {
		return JSON.toJavaObject(JSON.parseObject(str), clazz); //string转化为json类型，在转化为clazz类型的对象
	}
}

private void returnToPool(Jedis jedis) {
	// TODO Auto-generated method stub
	if(jedis !=null) {   
		jedis.close(); //返回池中，
	}
}


}
