package org.little.http;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpX509ServerInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  looger = LoggerFactory.getLogger(HttpX509ServerInitializer.class);

       public HttpX509ServerInitializer() {}

       @Override
       public void initChannel(SocketChannel ch) {
           looger.trace("HttpX509Server Initializer");
           ChannelPipeline pipeline = ch.pipeline();
           pipeline.addLast("decoder",new HttpRequestDecoder());
           pipeline.addLast("encoder",new HttpResponseEncoder());
           //pipeline.addLast("compressor",new HttpContentCompressor());
           pipeline.addLast("handel",new HttpX509ServerHandler());
           looger.trace("HttpX509Server Initializer ok");
       }
}
