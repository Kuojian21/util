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

public class KjClient {

	private static final ClientBootstrap BOOTSTRAP;
	private static final ConcurrentMap<Integer, ConcurrentMap<Long, ResponseFuture>> HOLDERS = Maps.newConcurrentMap();
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
							try {
								Response<?> response = JSON.parseObject((String) e.getMessage(), Response.class);
								HOLDERS.get(ctx.getChannel().getId()).remove(response.getId()).set(response);
							} catch (Exception ex) {
								ex.printStackTrace();
								System.out.println(e.getMessage());
							}
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

	private final GenericObjectPool<Channel> pool;
	private final AtomicLong id;
	private final ConcurrentMap<Long, ResponseFuture> futures;
	private final int workId;

	public KjClient(int workId, String ip, int port) {
		this.id = new AtomicLong(0);
		this.futures = Maps.newConcurrentMap();
		this.workId = workId;

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(-1);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<Channel>(new BasePooledObjectFactory<Channel>() {
			@Override
			public PooledObject<Channel> wrap(Channel channel) {
				return new DefaultPooledObject<Channel>(channel) {
					@Override
					public void invalidate() {
						channel.close();
						HOLDERS.remove(channel.getId());
						super.invalidate();
					}
				};
			}

			@Override
			public Channel create() throws Exception {
				ChannelFuture future = BOOTSTRAP.connect(new InetSocketAddress(ip, port)).await();
				HOLDERS.put(future.getChannel().getId(), futures);
				return future.getChannel();

			}
		}, config);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				KjClient.this.pool.close();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R, Q> Q send(R data, boolean sync) {
		Channel holder = null;
		try {
			holder = pool.borrowObject();
			Request request = new Request(workId, id.incrementAndGet(), data);
			ResponseFuture future = new ResponseFuture();
			this.futures.put(request.getId(), future);
			holder.write(JSON.toJSONString(request)).await();
			if (sync) {
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