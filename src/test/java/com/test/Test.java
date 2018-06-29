package com.test;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

public class Test {
  
    @SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

    	 int i = 1_123_123;
    	 
    	 Collections.sort(new ArrayList<String>(), (a,b)-> a.compareTo(b));
    	 
    	 Comparator<String> com = (a,b) -> a.compareTo(b);
    	 
    	 
    	 Converter<String, Boolean> converter = "java"::startsWith;
    	 Boolean converted = converter.convert("Java");
    	 System.out.println(converted);    // "J"
    	 
    	 try {
    		 List<String> list = Lists.newArrayList();
        	 
        	 for(i = 1;i < 100000;i++) {
        		 list.add("kuojian21kuojian21kuojian21kuojian21kuojian21kuojian21kuojian21kuojian21kuojian21" + i);
        		 if(i % 10000 == 0 || i >= 40000000) {
        			 Logger.getLogger(Test.class).info(i);
        		 }
        	 }
        	 for(String s : list) {
        		 System.out.println(s);
        	 }
    	 }catch(Throwable t) {
    		 Logger.getLogger(Test.class).error("", t);
    	 }
    	 
    	 HttpURLConnection con = (HttpURLConnection) new URL(
                 "https://www.baidu.com").openConnection();
    	 con.setConnectTimeout(100);
         con.setReadTimeout(100);
    	 System.out.println("提醒自投服务器有新任务，{}"  + con.getResponseCode());
    }
    
}