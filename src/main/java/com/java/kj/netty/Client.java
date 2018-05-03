package com.java.kj.netty;

import org.apache.commons.codec.binary.Base64;
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

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.java.kj.crypt.Decrypt;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.crypto.Cipher;

public class Client {

	private static final ClientBootstrap BOOTSTRAP;
	private static final ConcurrentMap<Integer, Handler> ACTIONS = Maps.newConcurrentMap();
	private static final ConcurrentMap<Integer,Handler> LATCHS = Maps.newConcurrentMap();
	static {
		BOOTSTRAP = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		BOOTSTRAP.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new SimpleChannelHandler() {
					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
						ACTIONS.get(ctx.getChannel().getId()).receive(ctx, e);
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
		void receive(ChannelHandlerContext ctx, MessageEvent e);
		void send(Channel channel);
	}
	
	private GenericObjectPool<Channel> pool;
	public Client(String ip, int port) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(10);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<Channel>(new BasePooledObjectFactory<Channel>() {
			@Override
			public PooledObject<Channel> wrap(Channel channel) {
				return new DefaultPooledObject<Channel>(channel) {
					@Override
				    public void invalidate() {
						channel.close();
				        super.invalidate();
				    }
				};
			}
			@Override
			public Channel create() throws Exception {
				ChannelFuture future = BOOTSTRAP.connect(new InetSocketAddress(ip, port));
				return future.getChannel();
			}
		}, config);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Client.this.pool.close();
			}
		});
	}
	
	public Future handle(Handler handler) {
		Channel channel = null;
		try {
			channel = pool.borrowObject();
			ACTIONS.put(channel.getId(), handler);
			handler.send(channel);
		} catch (Exception e) {

		} finally {
			if (channel != null) {
				pool.returnObject(channel);
			}
		}
		
		return null;
	}
	
	

}