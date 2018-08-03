package com.test.rpc.hsf;
 
 
public class HelloWorldServiceImpl implements HelloWorldService
{
	public String sayHello(String name) {
		return "hello "+name;
	}
 
}