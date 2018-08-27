package web.java;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainAPP {
	public static void main(String[] args) {
	      ApplicationContext context = 
	             new ClassPathXmlApplicationContext("world.xml");

	      HelloWorld obj = (HelloWorld) context.getBean("helloWorld");

	      obj.getMessage();
	   }
}
