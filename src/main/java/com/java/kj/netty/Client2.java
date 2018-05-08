package com.java.kj.netty;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Client2 {

	private static final ClientBootstrap BOOTSTRAP;
	private static final ConcurrentMap<Integer, Holder> HOLDERS = Maps.newConcurrentMap();
	static {
		BOOTSTRAP = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		BOOTSTRAP.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new SimpleChannelHandler() {
					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
						if (e.getMessage() instanceof String) {
							Response<?> response = JSON.parseObject((String)e.getMessage(), Response.class);
							HOLDERS.get(ctx.getChannel().getId()).futures.remove(response.getId()).set(response);
						}
						super.messageReceived(ctx, e);
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
						super.exceptionCaught(ctx, e);
					}

					@Override
					public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
						super.channelConnected(ctx, e);
					}
				});
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				BOOTSTRAP.releaseExternalResources();
			}
		});
	}

	private static class Holder {
		public final AtomicLong id;
		public final Channel channel;
		public final ConcurrentMap<Long, ResponseFuture> futures;

		public Holder(Channel channel) {
			super();
			this.channel = channel;
			id = new AtomicLong(0);
			futures = Maps.newConcurrentMap();
		}

		public ResponseFuture write(Object data) throws Exception {
			Request request = new Request(id.incrementAndGet(), data);
			ResponseFuture future = new ResponseFuture();
			this.futures.put(request.getId(), future);
			this.channel.write(JSON.toJSONString(request)).await();
			return future;
		}
	}

	private final GenericObjectPool<Holder> pool;

	public Client2(String ip, int port) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(-1);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<Holder>(new BasePooledObjectFactory<Holder>() {
			@Override
			public PooledObject<Holder> wrap(Holder holder) {
				return new DefaultPooledObject<Holder>(holder) {
					@Override
					public void invalidate() {
						holder.channel.close();
						HOLDERS.remove(holder.channel.getId());
						super.invalidate();
					}
				};
			}

			@Override
			public Holder create() throws Exception {
				ChannelFuture future = BOOTSTRAP.connect(new InetSocketAddress(ip, port)).await();
				Holder holder = new Holder(future.getChannel());
				HOLDERS.put(holder.channel.getId(), holder);
				return holder;

			}
		}, config);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Client2.this.pool.close();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R, Q> Q send(R data,boolean sync) {
		Holder holder = null;
		try {
			holder = pool.borrowObject();
			ResponseFuture future =  holder.write(data);
			if(sync) {
				return (Q) future.get().getData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (holder != null) {
				pool.returnObject(holder);
			}
		}
		return null;
	}

}