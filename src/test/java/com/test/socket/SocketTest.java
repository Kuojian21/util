package com.test.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTest {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ServerSocket server = null;
				try {
					server = new ServerSocket(8888);
					Socket socket = null;
					while ((socket = server.accept()) != null) {
						System.out.println("server:" + socket.getPort());
						System.out.println("server:" + socket.getLocalPort());
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (server != null) {
						try {
							server.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = null;
				try {
					while (true) {
						socket = new Socket();
						socket.connect(new InetSocketAddress(8888));
						System.out.println("client:" + socket.getPort());
						System.out.println("client:" + socket.getLocalPort());
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}
}
