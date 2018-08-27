package com.mana.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.cglib.core.Local;

/**
 * @author mana
 * 使用了几个重要的反射类
 *分别是ClassLoader、Class、Constructor和Method
 *通过这些反射类就可以间接调用目标Class的各项功能了
 *
 *在①处，我们获取当前线程的ClassLoader，然后通过指定的全限定类"com.mana.ioc.Hero"装载Hero类对应的反射实例
 *在②处，我们通过Hero的反射类对象获取Hero的构造函数对象cons，通过构造函数对象的newInstrance()方法实例化Hero对象，其效果等同于new Hero()。
 *在③处，我们又通过Hero的反射类对象的getMethod（String methodName,Class paramClass）获取属性的Setter方法对象，第一个参数是目标Class的方法名；
 *第二个参数是方法入参的对象类型。获取方法反射对象后，即可通过invoke（Object obj,Object param）方法调用目标类的方法，该方法的第一个参数是操作的目标类对象实例；
 *第二个参数是目标方法的入参。
 *第三步是通过反射方法操控目标类的元信息，如果我们将这些信息以一个配置文件的方式提供，
 *就可以使用Java语言的反射功能编写一段通用的代码对类似于Hero的类进行实例化及功能调用操作了。
 */
public class Test {

//	public static void main(String[] args) throws Exception{
//		//1. 通过类装载器获取Hero类对象  
//		ClassLoader loader=Thread.currentThread().getContextClassLoader();
//		Class<?> clazz=loader.loadClass("com.mana.ioc.Hero");
//		//2. 获取类的默认构造器对象并通过它实例化Hero  
//		Constructor<?> constructor=clazz.getDeclaredConstructor((Class[])null);
//		Hero hero =(Hero)constructor.newInstance();
//		//3. 通过反射方法设置属性  
//		Method setBrand=clazz.getMethod("setName", String.class);
//		setBrand.invoke(hero, "小鱼人");
//		Method setColor=clazz.getMethod("setOutfit", String.class);
//		setColor.invoke(hero, "爆裂魔杖");
//		// 4. 运行方法
//		hero.say();
//		
//		
//	}
	
	private static Container container=new SampleContainer();
	
	public static void baseTest() {
		container.registerBean(Lol.class);
		//初始化注入
		container.initWired();
		
		Lol lol=container.getBean(Lol.class);
		lol.work();
		
	}
	
	public static void iocClassTest() {
		container.registerBean(Lol2.class);
		//初始化注入
		container.initWired();
		
		Lol2 lol2=container.getBean(Lol2.class);
		lol2.work();
		
	}
	
	public static void iocNameTest() {
		container.registerBean("face", new FaceService2());
		container.registerBean(Lol3.class);
		//初始化注入
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
