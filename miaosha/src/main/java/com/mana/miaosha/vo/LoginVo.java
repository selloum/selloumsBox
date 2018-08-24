package com.mana.miaosha.vo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.mana.miaosha.validator.IsMobile;

public class LoginVo {   //对该类JSR303校验，则该类型参数前要加@Valid

	//编写需要校验的bean  JSR303自定义校验
	@NotNull
	@IsMobile  //自定义验证器
	private String mobile;
	@NotNull
	@Length(min=32)
	private String password;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "LoginVo [mobile=" + mobile + ", password=" + password + "]";
	}
	
	
}
