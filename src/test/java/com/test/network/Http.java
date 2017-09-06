package com.test.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Http {

	public static void main(String[] args) throws MalformedURLException, IOException{
		URLConnection conn = new URL("https://www.lmlc.com").openConnection();
		System.out.println(conn);
	}
	
}
