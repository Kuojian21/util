package com.java.kj.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class KjNio {

	static Selector selector;
	static {
		try {
			KjNio.selector = Selector.open();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (KjNio.selector.isOpen()) {
						Set<SelectionKey> keys = KjNio.selector.selectedKeys();
						Iterator<SelectionKey> iterator = keys.iterator();
						while (iterator.hasNext()) {
							try {
								SelectionKey key = iterator.next();
								if (key.isAcceptable()) {
									ServerSocketChannel server = (ServerSocketChannel) key.channel();
									server.register(KjNio.selector, SelectionKey.OP_ACCEPT);
									SocketChannel channel = server.accept();
									channel.configureBlocking(false);
									Action action = (Action) key.attachment();
									channel.register(KjNio.selector, SelectionKey.OP_READ,
											action);
								} else if (key.isConnectable()) {
									SocketChannel channel = (SocketChannel) key.channel();
									channel.configureBlocking(false);
									Holder holder = (Holder) key.attachment();
									channel.register(KjNio.selector, SelectionKey.OP_READ,
											holder);
									holder.semaphore.release();
								} else if (key.isReadable()) {
									SocketChannel channel = (SocketChannel) key.channel();
									Holder holder = (Holder) key.attachment();
									holder.action.handle(holder);
									channel.register(KjNio.selector, SelectionKey.OP_READ, holder);
								} else if (key.isWritable()) {
									SocketChannel channel = (SocketChannel) key.channel();
									Holder holder = (Holder) key.attachment();
									channel.write(holder.writer);
									holder.writer = null;
									holder.semaphore.release();
								}
								iterator.remove();
							} catch (Exception e) {
								e.printStackTrace();
							} 
						}
					}
				}
			});
			thread.setDaemon(false);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static interface Action {
		void handle(Holder holder) throws Exception;
	}

	public static class Holder {
		final SocketChannel channel;
		final Semaphore semaphore;
		ByteBuffer writer;
		Action action;

		Holder(SocketChannel channel){
			this.channel = channel;
			this.semaphore = new Semaphore(1);
		}
		
		Holder(SocketChannel channel, Action action) {
			this.channel = channel;
			this.action = action;
			this.semaphore = new Semaphore(1);
		}
	}

}
