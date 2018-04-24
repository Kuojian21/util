package com.test.lambda;
import java.util.function.Supplier;

import org.springframework.util.ReflectionUtils;

public class Property {
    /**
     * 发放张数
     */
    private int count;
    /**
     *  有效期
     */
    private String days;
    /**
     *  优惠券面额
     */
    private String price;
    
    
    
    public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getEachValue(int i, String fieldName){
        try {
            String str = (String) ReflectionUtils.invokeMethod(this.getClass().getMethod("get"+ fieldName), this);
            if(str.indexOf(",")==-1){
                return Integer.parseInt(str);
            }else{
                return Integer.parseInt(str.split(",")[i]);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public int getEachValue(int i, Supplier<String> supplier){
        if(supplier.get().indexOf(",")==-1){
            return Integer.parseInt(supplier.get());
        }else{
            return Integer.parseInt(supplier.get().split(",")[i]);
        }
    }
    
    public static void main(String[] args) {
    	Property config = new Property();
    	config.setCount(2);
    	config.setDays("1,2");
    	config.setPrice("3,4");
    	
    	 for (int i = 0; i < config.getCount(); i++) {
             System.out.println(config.getEachValue(i,config::getDays));
             System.out.println(config.getEachValue(i,config::getPrice));
         }
    }
    
}