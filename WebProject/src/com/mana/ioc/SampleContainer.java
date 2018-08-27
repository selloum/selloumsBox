package com.mana.ioc;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.core.ReflectUtils;

/**
 * ������ʵ��
 * @author mana
 * 
 * װ��ʱ�������£�
 * 1.�ж��Ƿ�ʹ�����Զ��������Ķ����ǣ�����name����bean)
 * 2.�ж��Ƿ�ʹ����Class����Bean(�ǣ�����Class����Bean,������Ҳ����򴴽�һ���޲ι��캯����Bean)
 *
 */
@SuppressWarnings("unchecked")
public class SampleContainer implements Container{

	/**
	 * ��������bean���󣬸�ʽΪcom.xxx.person:@52x2xa
	 * �����еĶ���洢�� beans �У��� beanKeys ά�����ƺͶ���Ĺ�ϵ��
	 */
	private Map<String, Object> beans;
	
	/**
	 * �洢bean��name�Ĺ�ϵ
	 * ������Bean�����ƴ洢�� beanKeys ���map�У�
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
	 * ע�����
	 * @param object
	 */
	public void injection(Object object) {
		//�����ֶ�
		try {
			Field[] fields=object.getClass().getDeclaredFields();
			for(Field field:fields) {
				///��Ҫע����ֶ�
				Autowired autoWired = field.getAnnotation(Autowired.class);
				if(autoWired!=null) {
					
					//Ҫע����ֶ�
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
							//ָ��װ�����
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
