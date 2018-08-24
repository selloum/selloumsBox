package com.mana.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}
	
	private static final String salt="1a2b3c4d";
	
	//明文密码做2次MD5
	//明文密码+固定salt
	//防止用户的明文密码在网络上传输
	public static String inputPassToFormPass(String inputPass) {
		String str=""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
		return md5(str);
		
	}
	
	//对网络上接收的第一次md5的密码+随机salt
	//做第二次MD5
	public static String formPassToDBPass(String formPass,String salt) {
		String str=""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
		return md5(str);
		
	}
	
	public static String inputPassToDbPass(String inputPass,String saltDB) {
		String formPass=inputPassToFormPass(inputPass);
		String dbPass=formPassToDBPass(formPass, saltDB);
		return dbPass;
	}
	
	public static void main(String[] args) {
		System.out.println(inputPassToDbPass("123456","1a2b3c4d"));//12123456c3

	}
}
