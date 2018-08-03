package com.test.rpc.zeroice;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Communicator communicator = Util.initialize(args);
		ObjectPrx base = communicator.stringToProxy("Hello:default -p 10000");
		HelloPrx hello = HelloPrx.checkedCast(base);
		hello.sayHello("zkj,hello world");
	}

}
