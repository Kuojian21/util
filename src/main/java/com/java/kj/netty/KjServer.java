package com.java.kj.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class KjServer {

	private static final ServerBootstrap BOOTSTRAP;
	private static final ConcurrentMap<Integer, Action> ACTIONS = Maps.newConcurrentMap();
	private static final ExecutorService SERVICE = Executors.newFixedThreadPool(10);
	private static final ConcurrentMap<Integer, BlockingQueue<Channel>> CHANNELS = Maps.newConcurrentMap();
	private static final ConcurrentMap<Integer, Object> CACHE = Maps.newConcurrentMap();
	private static final Object EMPTY = new Object();
	static {
		BOOTSTRAP = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		BOOTSTRAP.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new SimpleChannelHandler() {

					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
						if (e.getMessage() instanceof String) {
							try {
								Request request = JSON.parseObject((String) e.getMessage(), Request.class);
								Channel channel = ctx.getChannel();
								if (channel.isConnected()) {
									if (CACHE.putIfAbsent(channel.getId(), EMPTY) == null) {
										BlockingQueue<Channel> queues = CHANNELS.get(request.getWorkId());
										if (queues == null) {
											CHANNELS.putIfAbsent(request.getWorkId(),
													new LinkedBlockingQueue<Channel>());
											queues = CHANNELS.get(request.getWorkId());
										}
										queues.put(channel);
									}
								} else {
									CACHE.remove(channel.getId());
								}

								Integer port = ((InetSocketAddress) channel.getLocalAddress()).getPort();
								SERVICE.submit(new Runnable() {
									@Override
									public void run() {
										send(request, ACTIONS.get(port).action(request.getData()));
									}
								});
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
				SERVICE.shutdown();
				try {
					SERVICE.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static interface Action {
		Object action(Object data);
	}

	public static synchronized void bind(int port, Action action) {
		BOOTSTRAP.bind(new InetSocketAddress(port));
		ACTIONS.put(port, action);
	}

	public static void send(Request request, Object data) {
		Channel channel = null;
		BlockingQueue<Channel> queues = null;
		try {
			queues = CHANNELS.get(request.getWorkId());
			channel = queues.take();
			while (!channel.isConnected()) {
				channel = queues.take();
			}
			channel.write(JSON.toJSONString(new Response<Object>(request.getId(), data))).await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (channel != null && channel.isConnected()) {
				queues.add(channel);
			}
		}
	}

	private KjServer() {

	}

}