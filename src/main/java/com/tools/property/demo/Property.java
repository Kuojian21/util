package com.tools.property.demo;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.alibaba.fastjson.JSON;

public class Property {
	public static class A{
		private String a = "a";
		private String b = "b";
		public String getA() {
			return a;
		}
		public void setA(String a) {
			this.a = a;
		}
		public String getB() {
			return b;
		}
		public void setB(String b) {
			this.b = b;
		}
	}
	
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		System.out.println(JSON.toJSONString(PropertyUtils.describe(new A())));
	}
	
}
