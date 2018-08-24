package com.mana.miaosha.util;

import java.util.UUID;


//通用唯一识别码 
//目的，是让分布式系统中的所有元素，都能有唯一的辨识资讯，而不需要透过中央控制端来做辨识资讯的指定
//保证对在同一时空中的所有机器都是唯一的
public class UUIDUtil {
	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");//原生uuid带—,所以将其去除
	}
}
