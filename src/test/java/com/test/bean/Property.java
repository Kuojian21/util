package com.test.bean;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

public class Property {

	public void test() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		PropertyUtils.describe(new Object());
	}
	
}
