package com.java.kj.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.google.common.collect.Maps;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

public class Server {

	private static final ServerBootstrap BOOTSTRAP;
	private static final ConcurrentMap<Integer, Handler> ACTIONS = Maps.newConcurrentMap();
	static {
		BOOTSTRAP = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		BOOTSTRAP.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new SimpleChannelHandler() {
					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
						ACTIONS.get(ctx.getChannel().getId()).received(ctx,
								e);
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

	public static interface Handler {
		void received(ChannelHandlerContext ctx, MessageEvent e);
	}

	public static synchronized void bind(int port, Handler handler) {
		Channel channel = BOOTSTRAP.bind(new InetSocketAddress(port));
		ACTIONS.put(channel.getId(), handler);
	}

}