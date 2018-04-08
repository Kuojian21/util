package com.test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;

public class Test {
  
    public static void main(String[] args) {

    	 int i = 1_123_123;
    	 
    	 Collections.sort(new ArrayList<String>(), (a,b)-> a.compareTo(b));
    	 
    	 Comparator<String> com = (a,b) -> a.compareTo(b);
    	 
    	 
    	 Converter<String, Boolean> converter = "java"::startsWith;
    	 Boolean converted = converter.convert("Java");
    	 System.out.println(converted);    // "J"
    	 
    	 try {
    		 List<String> list = Lists.newArrayList();
        	 
        	 for(i = 1;i < 100000000;i++) {
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
    }
    
}