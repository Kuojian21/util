package com.java.kj.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

public class Server {

	private static final ServerBootstrap BOOTSTRAP;
	private static final ConcurrentMap<Integer, Action> ACTIONS = Maps.newConcurrentMap();
	static {
		BOOTSTRAP = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		BOOTSTRAP.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new SimpleChannelHandler() {
					
					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
						if(e.getMessage() instanceof String) {
							Request<?> request = JSON.parseObject((String)e.getMessage(), Request.class);
							Integer port = ((InetSocketAddress)ctx.getChannel().getLocalAddress()).getPort();
							Object data = ACTIONS.get(port).action(request.getData());
							ctx.getChannel().write(JSON.toJSONString(new Response<Object>(request.getId(),data))).await();
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

	public static interface Action {
		Object action(Object data);
	}

	public static synchronized void bind(int port, Action action) {
		BOOTSTRAP.bind(new InetSocketAddress(port));
		ACTIONS.put(port, action);
	}
	
	private Server(){
		
	}

}