package com.tools.zookeeper;

public interface ZKClient {
	
	void doSth();
	
	boolean isMaster();
	
	boolean isAlive();
}
