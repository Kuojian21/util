package com.test.rpc.zeroice;

import com.zeroc.Ice.Current;

public class HelloImpl implements Hello {

	@Override
	public void sayHello(String s, Current current) {
		System.out.println(s);

	}

}
