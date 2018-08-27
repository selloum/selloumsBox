package com.mana.ioc;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autowired {
	
	/**
	 * 
	 * @return Ҫע�������
	 */
	Class<?> value() default Class.class;
	
	/**
	 * 
	 * @return bean������
	 */
	String name() default "";

}
