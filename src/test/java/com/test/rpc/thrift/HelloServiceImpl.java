package com.test.rpc.thrift;

import org.apache.thrift.TException;

public class HelloServiceImpl implements Hello.Iface{

	@Override
	public String helloString(String para) throws TException {
		// TODO Auto-generated method stub
		return para;
	}

	@Override
	public int helloInt(int para) throws TException {
		// TODO Auto-generated method stub
		return para;
	}

	@Override
	public boolean helloBoolean(boolean para) throws TException {
		// TODO Auto-generated method stub
		return para;
	}

	@Override
	public void helloVoid() throws TException {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
	}

	@Override
	public String helloNull() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
