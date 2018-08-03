package com.test.rpc.zeroice;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class Server {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Communicator communicator = Util.initialize(args);
		ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("HelloAdapter", "default -p 10000");
		Hello hello = new HelloImpl();
		adapter.add(hello, Util.stringToIdentity("Hello"));
		adapter.activate();
		communicator.waitForShutdown();
	}

}
