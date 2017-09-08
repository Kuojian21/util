package com.tools.network.http;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class HttpURLConnectionTool {
	public static void main(String args[]) throws Exception {
		String urlString = "https://www.baidu.com/";
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		Object obj = connection.getContent();
		System.out.println(obj);
		url = new URL(urlString);
		connection = url.openConnection();
		obj = connection.getContent();
		System.out.println(obj);
		CookieStore cookieJar = manager.getCookieStore();
		List<HttpCookie> cookies = cookieJar.getCookies();
		for (HttpCookie cookie : cookies) {
			System.out.println(cookie);
		}
	}
}