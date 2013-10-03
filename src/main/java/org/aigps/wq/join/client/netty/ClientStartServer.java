package org.aigps.wq.join.client.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.aigps.wq.join.common.JobUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientStartServer {

	private final static Log log = LogFactory.getLog(ClientStartServer.class);

	public ClientStartServer(final int port) {
		JobUtil.start("client.server", new Thread(){
			@Override
			public void run() {
				startup(port);
			}
		});
	}

	private void startup(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap()
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_REUSEADDR, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("decoder", new ClientDecoder());
							pipeline.addLast("encoder", new ClientEncoder());
							pipeline.addLast("handler", new ClientHandler());
						}
					});

			log.error("启动手机客户端端口:"+port);
			b.bind(port).sync().channel().closeFuture().sync();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
