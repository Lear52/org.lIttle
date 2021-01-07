package org.little.https;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;

public class HttpsHandlerlInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpsHandlerlInitializer(SslContext sslCtx) {
           this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {

        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpsHandler());
    }
}
