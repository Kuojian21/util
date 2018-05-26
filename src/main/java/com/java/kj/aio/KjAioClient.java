package com.java.kj.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class KjAioClient {

	private final GenericObjectPool<AsynchronousSocketChannel> pool;

	public KjAioClient(String ip, int port) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(-1);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<AsynchronousSocketChannel>(
				new BasePooledObjectFactory<AsynchronousSocketChannel>() {
					@Override
					public PooledObject<AsynchronousSocketChannel> wrap(AsynchronousSocketChannel channel) {
						return new DefaultPooledObject<AsynchronousSocketChannel>(channel) {
							@Override
							public void invalidate() {
								try {
									channel.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								super.invalidate();
							}
						};
					}

					@Override
					public AsynchronousSocketChannel create() throws Exception {
						AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
						CountDownLatch latch = new CountDownLatch(1);
						channel.connect(new InetSocketAddress(ip, port), null, new CompletionHandler<Void, Void>() {
							@Override
							public void completed(Void result, Void attachment) {
								latch.countDown();
							}

							@Override
							public void failed(Throwable exc, Void attachment) {
								latch.countDown();
							}
						});
						latch.await();
						return channel;

					}
				}, config);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				KjAioClient.this.pool.close();
			}
		});
	}
	
	
	public static class Holder {
		public final AsynchronousSocketChannel channel;
		public final ByteBuffer reader;
		public final Semaphore semaphore;

		public Holder(AsynchronousSocketChannel channel, int rsize) {
			this.channel = channel;
			this.reader = ByteBuffer.allocate(rsize);
			this.semaphore = new Semaphore(1);
		}
	}
	
	public CompletionHandler<Integer, Holder> WRITER = new CompletionHandler<Integer, Holder>() {

		@Override
		public void completed(Integer result, Holder attachment) {
			attachment.semaphore.release();
		}

		@Override
		public void failed(Throwable exc, Holder attachment) {
			attachment.semaphore.release();
		}

	};

	public static interface Action {
		void request(AsynchronousSocketChannel channel);
	}

	public void request(Action action) {
		AsynchronousSocketChannel channel = null;
		try {
			channel = pool.borrowObject();
			action.request(channel);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channel != null) {
				pool.returnObject(channel);
			}
		}
	}
}
