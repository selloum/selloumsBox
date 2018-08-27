package com.mana.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.cglib.core.Local;

/**
 * @author mana
 * ʹ���˼�����Ҫ�ķ�����
 *�ֱ���ClassLoader��Class��Constructor��Method
 *ͨ����Щ������Ϳ��Լ�ӵ���Ŀ��Class�ĸ������
 *
 *�ڢٴ������ǻ�ȡ��ǰ�̵߳�ClassLoader��Ȼ��ͨ��ָ����ȫ�޶���"com.mana.ioc.Hero"װ��Hero���Ӧ�ķ���ʵ��
 *�ڢڴ�������ͨ��Hero�ķ���������ȡHero�Ĺ��캯������cons��ͨ�����캯�������newInstrance()����ʵ����Hero������Ч����ͬ��new Hero()��
 *�ڢ۴���������ͨ��Hero�ķ���������getMethod��String methodName,Class paramClass����ȡ���Ե�Setter�������󣬵�һ��������Ŀ��Class�ķ�������
 *�ڶ��������Ƿ�����εĶ������͡���ȡ�����������󣬼���ͨ��invoke��Object obj,Object param����������Ŀ����ķ������÷����ĵ�һ�������ǲ�����Ŀ�������ʵ����
 *�ڶ���������Ŀ�귽������Ρ�
 *��������ͨ�����䷽���ٿ�Ŀ�����Ԫ��Ϣ��������ǽ���Щ��Ϣ��һ�������ļ��ķ�ʽ�ṩ��
 *�Ϳ���ʹ��Java���Եķ��书�ܱ�дһ��ͨ�õĴ����������Hero�������ʵ���������ܵ��ò����ˡ�
 */
public class Test {

//	public static void main(String[] args) throws Exception{
//		//1. ͨ����װ������ȡHero�����  
//		ClassLoader loader=Thread.currentThread().getContextClassLoader();
//		Class<?> clazz=loader.loadClass("com.mana.ioc.Hero");
//		//2. ��ȡ���Ĭ�Ϲ���������ͨ����ʵ����Hero  
//		Constructor<?> constructor=clazz.getDeclaredConstructor((Class[])null);
//		Hero hero =(Hero)constructor.newInstance();
//		//3. ͨ�����䷽����������  
//		Method setBrand=clazz.getMethod("setName", String.class);
//		setBrand.invoke(hero, "С����");
//		Method setColor=clazz.getMethod("setOutfit", String.class);
//		setColor.invoke(hero, "����ħ��");
//		// 4. ���з���
//		hero.say();
//		
//		
//	}
	
	private static Container container=new SampleContainer();
	
	public static void baseTest() {
		container.registerBean(Lol.class);
		//��ʼ��ע��
		container.initWired();
		
		Lol lol=container.getBean(Lol.class);
		lol.work();
		
	}
	
	public static void iocClassTest() {
		container.registerBean(Lol2.class);
		//��ʼ��ע��
		container.initWired();
		
		Lol2 lol2=container.getBean(Lol2.class);
		lol2.work();
		
	}
	
	public static void iocNameTest() {
		container.registerBean("face", new FaceService2());
		container.registerBean(Lol3.class);
		//��ʼ��ע��
		container.initWired();
		
		Lol3 lol3=container.getBean(Lol3.class);
		lol3.work();
	}
	
	public static void main(String[] args) {
		baseTest();
//		iocClassTest();
//		iocNameTest();
	}
}
