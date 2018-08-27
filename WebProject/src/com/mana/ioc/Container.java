package com.mana.ioc;

import java.util.Set;

public interface Container {

	/**
	 * ����Class��ȡBean
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(Class<T> clazz);
	
	/**
	 * ��������Name��ȡBean
	 * @param name
	 * @return
	 */
	public <T> T getBeanByName(String name);
	
	/**
	 * ע��һ��Bean��������
	 * @param bean
	 * @return
	 */
	public Object registerBean(Object bean);
	
	/**
	 * ע��һ��Class��������
	 * @param clazz
	 * @return
	 */
	public Object registerBean(Class<?> clazz);
	
	/**
	 * ע��һ�������Ƶ�Bean��������
	 * @param name
	 * @param bean
	 * @return
	 */
	public Object registerBean(String name,Object bean);
	
	/**
	 * ����Classɾ��һ��Bean
	 * @param clazz
	 */
	public void remove(Class<?> clazz);
	/**
	 * ��������ɾ��һ��Bean
	 * @param name
	 */
	public void removeByName(String name);
	
	/**
	 * ��������bean��������
	 * @return
	 */
	public Set<String> getBeanNames();
	
	/**
	 * ��ʼ��װ��
	 */
	public void initWired();
	
}
