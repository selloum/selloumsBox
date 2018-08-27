package com.mana.ioc;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.core.ReflectUtils;

/**
 * 容器简单实现
 * @author mana
 * 
 * 装配时步骤如下：
 * 1.判断是否使用了自定义命名的对象（是：根据name查找bean)
 * 2.判断是否使用了Class类型Bean(是：根据Class查找Bean,如果查找不到则创建一个无参构造函数的Bean)
 *
 */
@SuppressWarnings("unchecked")
public class SampleContainer implements Container{

	/**
	 * 保存所有bean对象，格式为com.xxx.person:@52x2xa
	 * 将所有的对象存储在 beans 中，用 beanKeys 维护名称和对象的关系。
	 */
	private Map<String, Object> beans;
	
	/**
	 * 存储bean和name的关系
	 * 将所有Bean的名称存储在 beanKeys 这个map中，
	 */
	private Map<String, String> beanKeys;
	
	
	public SampleContainer() {
		this.beans=new ConcurrentHashMap<String,Object>();
		this.beanKeys=new ConcurrentHashMap<String, String>();
	}
	
	@Override
	public <T> T getBean(Class<T> clazz) {
		String name=clazz.getName();
		Object object=beans.get(name);
		if(object!=null) {
			return (T)object;
		}
		return null;
	}

	@Override
	public <T> T getBeanByName(String name) {
		String className=beanKeys.get(name);
		Object object=beans.get(className);
		if(object!=null) {
			return (T)object;
		}
		return null;
	}

	@Override
	public Object registerBean(Object bean) {
		String name=bean.getClass().getName();
		beanKeys.put(name, name);
		beans.put(name, bean);
		return bean;
	}

	@Override
	public Object registerBean(Class<?> clazz) {
		String name=clazz.getName();
		beanKeys.put(name, name);
		Object bean =ReflectUtils.newInstance(clazz);
		beans.put(name, bean);
		return bean;
	}

	@Override
	public Object registerBean(String name, Object bean) {
		String className=bean.getClass().getName();
		beanKeys.put(name, className);
		beans.put(className, bean);
		
		return bean;
	}

	@Override
	public void remove(Class<?> clazz) {
		String className=clazz.getName();
		if(className!=null&&!className.equals("")) {
			beanKeys.remove(className);
			beans.remove(className);
			}
		
	}

	@Override
	public void removeByName(String name) {
		String className=beanKeys.get(name);
		if(className!=null&&!className.equals("")) {
			beans.remove(className);
			beanKeys.remove(name);
		}
		
	}

	@Override
	public Set<String> getBeanNames() {
		return beanKeys.keySet();
	}

	@Override
	public void initWired() {
		Iterator<Entry<String, Object>> iterator=beans.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, Object> entry=(Map.Entry<String, Object>)iterator.next();
			Object object=entry.getValue();
			injection(object);
		}
		
	}

	/**
	 * 注入对象
	 * @param object
	 */
	public void injection(Object object) {
		//所有字段
		try {
			Field[] fields=object.getClass().getDeclaredFields();
			for(Field field:fields) {
				///需要注入的字段
				Autowired autoWired = field.getAnnotation(Autowired.class);
				if(autoWired!=null) {
					
					//要注入的字段
					Object autoWiredField=null;
					
					String name=autoWired.name();
					if(!name.equals("")) {
						String className=beanKeys.get(name);
						if(className!=null&&!className.equals("")) {
							autoWiredField=beans.get(className);
						}
						if(autoWiredField==null) {
							throw new RuntimeException("Unable to load"+name);
						}
						
					}else {
						if(autoWired.value()==Class.class) {
							autoWiredField=recursiveAssembly(field.getType());	
						}else {
							//指定装配的类
							autoWiredField=this.getBean(autoWired.value());
							if(autoWiredField!=null) {
								autoWiredField=recursiveAssembly(field.getType());
							}
						}
					}
					if(autoWiredField==null) {
						throw new RuntimeException("Unable to load"+field.getType().getCanonicalName());
					}
					boolean accessible=field.isAccessible();
					field.setAccessible(true);
					field.set(object, autoWiredField);
					field.setAccessible(accessible);
				}
				
			}
		}catch (SecurityException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	private Object recursiveAssembly(Class<?> clazz) {
		if(null!=clazz) {
			return this.registerBean(clazz);
		}
		return null;
	}


}
