package com.test.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

import com.beust.jcommander.internal.Lists;

public class TypeTest {

	public static List<Class<?>> types(Class<?> clazz) {
		List<Class<?>> list = Lists.newArrayList();
		Type type = clazz.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			for (Class<?> c : (Class<?>[]) ((ParameterizedType) type).getActualTypeArguments()) {
				list.add(c);
			}
		}

		for (Type t : clazz.getGenericInterfaces()) {
			if (t instanceof ParameterizedType) {
				for (Type c : ((ParameterizedType) t).getActualTypeArguments()) {
					list.add((Class<?>) c);
				}
			}
		}

		return list;
	}

	public static void main(String[] args) {
		List<Class<?>> clazzes = types(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return 0;
			}
		}.getClass());
		for (Class<?> clazz : clazzes) {
			System.out.println(clazz.getName());
		}
	}

}
