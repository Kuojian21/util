package com.java.kj.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;


public class KjNioServer extends KjNio{

	private ServerSocketChannel server;

	private KjNioServer(int port, Action action) throws IOException {
		try {
			server = ServerSocketChannel.open();  
	        server.configureBlocking(false);  
	        server.socket().bind(new InetSocketAddress(port));
	        server.register(selector, SelectionKey.OP_ACCEPT); 
		} finally {
			if (this.server != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							KjNioServer.this.server.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	public static KjNioServer bind(int port, Action action) {
		try {
			return new KjNioServer(port, action);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
