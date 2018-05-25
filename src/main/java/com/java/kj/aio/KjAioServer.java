package com.java.kj.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class KjAioServer {

	private static final ConcurrentMap<Integer, Action> ACTIONS = Maps.newConcurrentMap();

	private static CompletionHandler<AsynchronousSocketChannel, Action> ACCEPT = new CompletionHandler<AsynchronousSocketChannel, Action>(){

		@Override
		public void completed(AsynchronousSocketChannel channel, Action attachment) {
			channel.read(ByteBuffer.allocate(4096), attachment, READ);
			
		}

		@Override
		public void failed(Throwable exc, Action attachment) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private static CompletionHandler<Integer, Action> READ = new CompletionHandler<Integer, Action>(){

		@Override
		public void completed(Integer result, Action attachment) {
			
			
		}

		@Override
		public void failed(Throwable exc, Action attachment) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private static CompletionHandler<AsynchronousSocketChannel, Action> WRITER = new CompletionHandler<AsynchronousSocketChannel, Action>(){

		@Override
		public void completed(AsynchronousSocketChannel result, Action attachment) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void failed(Throwable exc, Action attachment) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public static interface Action {
		Object action(Object data);
	}

	public void bind(int port, Action action) {
		if (ACTIONS.putIfAbsent(port, action) != null) {
			return;
		}

		AsynchronousServerSocketChannel server = null;
		try {
			server = AsynchronousServerSocketChannel.open();
			server.bind(new InetSocketAddress(port));
			server.accept(action, ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}

	}

	public void read(final AsynchronousSocketChannel channel) {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer buffer) {
				new Exception().printStackTrace();
				System.out.println("result:" + result);
				String str = new String(buffer.array());
				System.out.println("str:" + str);
				if (channel.isOpen()) {
					write(channel, ByteBuffer.wrap((str + "1").getBytes()));
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {

			}

		});
	}

	public void write(final AsynchronousSocketChannel channel, ByteBuffer buffer) {
		channel.write(buffer, null, new CompletionHandler<Integer, Void>() {
			@Override
			public void completed(Integer result, Void attachment) {
				System.out.println("result:" + result);
				new Exception().printStackTrace();
				if (channel.isOpen()) {
					read(channel);
				}
			}

			@Override
			public void failed(Throwable exc, Void attachment) {

			}
		});
	}

}
