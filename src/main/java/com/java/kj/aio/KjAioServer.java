package com.java.kj.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Maps;

public class KjAioServer {
	private final ConcurrentMap<Integer, Holder> holders = Maps.newConcurrentMap();
	private final Action action;
	private final AsynchronousServerSocketChannel server;
	private final ExecutorService service = Executors.newFixedThreadPool(10);

	private KjAioServer(int port, Action action) throws IOException {
		this.action = action;
		AsynchronousServerSocketChannel server = null;
		try {
			server = AsynchronousServerSocketChannel.open();
			server.bind(new InetSocketAddress(port));
			server.accept(action, ACCEPT);
		} finally {
			this.server = server;
			if (this.server != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							KjAioServer.this.server.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						service.shutdown();
					}
				});
			}
		}
	}

	public static interface Action {
		byte[] action(byte[] data);
	}

	static class Holder {
		final AsynchronousSocketChannel channel;
		final ByteBuffer reader;
		final Lock lock;

		Holder(AsynchronousSocketChannel channel, int rsize) {
			this.channel = channel;
			this.reader = ByteBuffer.allocate(rsize);
			this.lock = new ReentrantLock();
		}
	}

	private CompletionHandler<AsynchronousSocketChannel, Action> ACCEPT = new CompletionHandler<AsynchronousSocketChannel, Action>() {

		@Override
		public void completed(AsynchronousSocketChannel channel, Action attachment) {
			Holder holder = new Holder(channel, 1024);
			channel.read(holder.reader, holder, READER);
		}

		@Override
		public void failed(Throwable exc, Action attachment) {

		}

	};

	private CompletionHandler<Integer, Holder> READER = new CompletionHandler<Integer, Holder>() {

		@Override
		public void completed(Integer result, Holder attachment) {
			ByteBuffer read = attachment.reader;
			read.flip();
			byte[] data = new byte[read.limit()];
			read.get(data);
			read.rewind();
			service.submit(new Runnable() {
				@Override
				public void run() {
					KjAioServer.this.send(attachment, KjAioServer.this.action.action(data));
				}
			});
		}

		@Override
		public void failed(Throwable exc, Holder attachment) {

		}

	};

	private CompletionHandler<Integer, Holder> WRITER = new CompletionHandler<Integer, Holder>() {

		@Override
		public void completed(Integer result, Holder attachment) {
			attachment.lock.unlock();
		}

		@Override
		public void failed(Throwable exc, Holder attachment) {
			try {
				attachment.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				attachment.lock.unlock();
			}
		}

	};

	public void send(Holder holder, byte[] data) {
		holder.lock.lock();
		holder.channel.write(ByteBuffer.wrap(data), holder, WRITER);
	}

	public static KjAioServer bind(int port, Action action) {
		try {
			return new KjAioServer(port, action);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
