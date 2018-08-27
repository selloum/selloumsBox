package com.mana.ioc;

import java.util.Set;

public interface Container {

	/**
	 * 根据Class获取Bean
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(Class<T> clazz);
	
	/**
	 * 根据名称Name获取Bean
	 * @param name
	 * @return
	 */
	public <T> T getBeanByName(String name);
	
	/**
	 * 注册一个Bean到容器中
	 * @param bean
	 * @return
	 */
	public Object registerBean(Object bean);
	
	/**
	 * 注册一个Class到容器中
	 * @param clazz
	 * @return
	 */
	public Object registerBean(Class<?> clazz);
	
	/**
	 * 注册一个带名称的Bean到容器中
	 * @param name
	 * @param bean
	 * @return
	 */
	public Object registerBean(String name,Object bean);
	
	/**
	 * 根据Class删除一个Bean
	 * @param clazz
	 */
	public void remove(Class<?> clazz);
	/**
	 * 根据名称删除一个Bean
	 * @param name
	 */
	public void removeByName(String name);
	
	/**
	 * 返回所有bean对象名称
	 * @return
	 */
	public Set<String> getBeanNames();
	
	/**
	 * 初始化装配
	 */
	public void initWired();
	
}
