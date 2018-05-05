package com.test.netty;

import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.java.kj.netty.Server;

public class ServerTest {

	public static void main(String[] args) {
		Map<String, String> map = Maps.newHashMap();
		map.put("1", "Red");
		map.put("2", "white");
		map.put("3", "blue");
		Server.bind(8888, new Server.Action() {
			@Override
			public Object action(Object data) {
				String r = map.get(data);
				return Strings.isNullOrEmpty(r) ? "kuojian" : r;
			}
		});
	}

}
