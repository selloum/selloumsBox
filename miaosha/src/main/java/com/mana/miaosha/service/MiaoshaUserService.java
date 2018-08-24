package com.mana.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mana.miaosha.util.MD5Util;
import com.mana.miaosha.util.UUIDUtil;
import com.alibaba.druid.util.StringUtils;
import com.mana.miaosha.domin.MiaoshaUser;
import com.mana.miaosha.exception.GlobalException;
import com.mana.miaosha.redis.MiaoshaUserKey;
import com.mana.miaosha.redis.RedisService;
import com.mana.miaosha.result.CodeMsg;
import com.mana.miaosha.vo.LoginVo;
import com.mana.miaosha.dao.MiaoshaUserDao;

@Service
public class MiaoshaUserService {

	//某个service只能引用自己的DAO，或者引用其他的service，不能引用其他的DAO
	//若在别的service中引用自己的DAO，则会绕出缓存，若调用service，则service中可能调用缓存
	//
	public static final String COOK1_TOKEN_NAME="token1";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	
	//对象缓存  id缓存
	public MiaoshaUser getById(long id) {
		
		//取缓存
		/*
		 * getById()通过id来获取对象，获取的同时将数据放入缓存（redis）
		 * getById在两个方法中被调用。login()和updatePassword()。
		 * login方法中从mysql数据库的miaosha_user表中查找指定手机号的用户。
		 * updatePassword是取出指定id的用户并进行密码的更改。
		 */
		MiaoshaUser user=redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
		if(user!=null) {
			return user;
		}
		
		//取数据库
		//（则这时缓存当中没有数据）
		user= miaoshaUserDao.getById(id);
		if(user!=null) {////从数据库中取出来之后，需要放到redis中，即缓存中。
			redisService.set(MiaoshaUserKey.getById, ""+id,user);//将数据写入缓存当中
		}
		return user;
	}
	
	
	
	//对象缓存
	// 把数据库更新，然后使缓存失效
	//若先删除缓存，则进行取操作，获得旧缓存，再更新数据库，则缓存不一致
	public boolean updatePassword(String token ,long id,String passwordNew) {
		//取user对象
		MiaoshaUser user=getById(id);  //取user
		if(user==null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		
		//更新数据库!!!!
		MiaoshaUser toBeUpdate=new MiaoshaUser();	//创建对象
		toBeUpdate.setId(id);
		//MD5处理明文密码
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew, user.getSalt()));
		//为何新建对象更新？  提高性能效率，只更新相关信息
		miaoshaUserDao.update(toBeUpdate);
		//处理缓存,更新缓存，防止出现数据不一致!!!!
		
		redisService.delete(MiaoshaUserKey.getById, ""+id);  //删除缓存
		user.setPassword(toBeUpdate.getPassword());    //更新密码
		redisService.set(MiaoshaUserKey.token, token,user);//更新token
		//不能删掉token，删掉后无法登陆！！！
		//先更新数据库，再更新缓存，否则出现缓存不一致错误
		
		return true;
	}
	
	//从缓存中获取token对应的MiaoshaUser
	/**
	 * 对象缓存-----token缓存
	 * @param response
	 * @param token
	 * @return
	 */
	public MiaoshaUser getByToken(HttpServletResponse response,String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		/**
		 * 通过token开获取缓存的，被UserArgumentResolver所调用。
		 * getByToken只对应一个用户，在用户的登陆成功的同时就存入了redis数据库当中了。
		 * getByToken方法只是在UserArgumentResolver类的resolveArgumen方法中被调用了，
		 * 获取token不易，该方法中经过代码对token进行了获取。主要是在自动注入user的时候用到。（controller中）
		 * 
		 */
		MiaoshaUser miaoshaUser=redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		
		//延长有效期
		if(miaoshaUser!=null) {
			addCookie(response,token, miaoshaUser);
		}
		
		return miaoshaUser;
	}
	
	public String  login(HttpServletResponse response,LoginVo loginVo) {
		if(loginVo==null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
	}
		String mobile =loginVo.getMobile();
		String formPass=loginVo.getPassword();
		
		//判断手机号是否存在
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if(user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//验证密码
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		if(!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//登陆成功，生成cookie
		String token =UUIDUtil.uuid();//uuid()是静态方法，但是每次生成的token是不一样的。
		addCookie(response,token, user);
		
		return token;
	}
	
	private void addCookie(HttpServletResponse response,String token,MiaoshaUser user) {
		
		//将用户信息存入第三方缓存中
		redisService.set(MiaoshaUserKey.token, token, user); //前缀+token+用户信息
		Cookie cookie=new Cookie(COOK1_TOKEN_NAME,token); //生成cookie
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());  //设置cookie有效期
		cookie.setPath("/"); //设置网站根目录
		response.addCookie(cookie);  //将cookie写入response
	}

}
