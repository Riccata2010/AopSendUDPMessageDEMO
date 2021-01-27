package org.mio.demo.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AroundExecutionUDP {

	public String message() default "";

	public String address();

	public int port();

	Properties prop = new Properties();

	public class Properties {

		String mes = null;

		public String getMessage() {
			return mes;
		}

		public void setMessage(String message) {
			mes = message;
		}
	}
}
