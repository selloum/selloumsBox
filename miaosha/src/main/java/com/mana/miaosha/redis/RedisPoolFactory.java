package com.mana.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {
	
	@Autowired
	RedisConfig redisConfig;
	   
	@Bean  //注入到spring容器中
	 public JedisPool JedisPoolFactory() {
		 JedisPoolConfig poolConfig=new JedisPoolConfig();
		 poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
		 poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
		 poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
		 JedisPool jp=new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort(),
				 redisConfig.getTimeout()*1000,redisConfig.getPassword(),0);
		 return jp;
	 }
}
