package com.mana.miaosha.result;

import org.springframework.util.concurrent.SuccessCallback;

public class Result<T> {
	//json格式
	private int code;
	private String msg;
	private T data;
	
	
	private Result(T data) {
		// TODO Auto-generated constructor stub
		this.code=0;
		this.msg="success";
		this.data=data;
		
	}

	private Result(CodeMsg cm) {
		if(cm==null) {
			return ;
		}
		this.msg=cm.getMsg();
		this.code=cm.getCode();
		
	}

	//成功时候调用
	public static <T> Result<T> success(T data){
		return new Result<T>(data);
	}
	
	//失败时候调用
	public static <T> Result<T> error(CodeMsg cm){
		return new Result<T>(cm);
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
	
}
