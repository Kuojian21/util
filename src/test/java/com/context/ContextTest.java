package com.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ContextTest {

	public static void main(String[] args) throws NamingException {
		Context context = new InitialContext();
		context.lookup("java:/home/hello");
	}
	
}
