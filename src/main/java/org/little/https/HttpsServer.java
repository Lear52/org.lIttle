package org.little.https;

import org.little.util.LogManager;
import org.little.util.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;


public final class HttpsServer {
    private static final Logger logger = LogManager.getLogger(HttpsServer.class);

    private HttpsServer() { }

    public static void start(SslContext sslCtx, int localPort) throws InterruptedException {
        // Configure the server.
        EventLoopGroup bossGroup   = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new HttpsHandlerlInitializer(sslCtx));

            Channel ch = b.bind(localPort).sync().channel();
            logger.info("Open your web browser and navigate to https://127.0.0.1:{}/", localPort);

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
