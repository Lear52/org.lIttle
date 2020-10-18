package org.little.http.handler;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpX509ServerInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  looger = LoggerFactory.getLogger(HttpX509ServerInitializer.class);
       //private lHttpRequest      req

       public HttpX509ServerInitializer() {}

       //public HttpX509ServerInitializer(lHttpRequest _req) {req=_req;}


       @Override
       public void initChannel(SocketChannel ch) {
           looger.trace("HttpX509Server Initializer pipeline");
           ChannelPipeline pipeline = ch.pipeline();
           pipeline.addLast("httpSession",new SessionInitiationHandler());
           pipeline.addLast("httpDecoder",new HttpRequestDecoder());
           pipeline.addLast("httpEncoder",new HttpResponseEncoder());
           //pipeline.addLast("compressor",new HttpContentCompressor());
           pipeline.addLast("httpHandel",new HttpX509ServerHandler());

           looger.trace("HttpX509Server Initializer pipeline ok");

       }
}