package com.test.multicast;

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastTest extends Thread {
	public static void main(String[] args) throws Exception {
		InetAddress group = InetAddress.getByName("230.198.112.0");
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					MulticastSocket socket = null;
					try {
						socket = new MulticastSocket(8888);
						socket.setTimeToLive(1);
						socket.joinGroup(group);
						while (true) {
							byte buff[] = new byte[8192];
							DatagramPacket packet = new DatagramPacket(buff, buff.length);
							socket.receive(packet);
							System.out.println(new String(packet.getData(), 0, packet.getLength()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						socket.close();
					}
				}
			}).start();
		}

		MulticastSocket socket = new MulticastSocket(8888);
		socket.setTimeToLive(1);
		socket.joinGroup(group);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						byte buff[] = new byte[8192];
						DatagramPacket packet = new DatagramPacket(buff, buff.length);
						socket.receive(packet);
						System.out.println(new String(packet.getData(), 0, packet.getLength()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socket.close();
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String msgs[] = { "失物招领...", "大风蓝色预警..." };
					while (true) {
						for (String msg : msgs) {
							byte buff[] = msg.getBytes();
							DatagramPacket packet = new DatagramPacket(buff, buff.length, group, 8888);
							socket.send(packet);
							sleep(2000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socket.close();
				}
			}
		}).start();
	}
}