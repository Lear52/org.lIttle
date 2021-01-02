package org.little.http.handler;

import org.little.http.commonHTTP;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class lHttpServerInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  looger = LoggerFactory.getLogger(lHttpServerInitializer.class);

       public lHttpServerInitializer() {}

       @Override
       public void initChannel(SocketChannel ch) {
           looger.trace("HttpX509Server Initializer pipeline");
           ChannelPipeline pipeline = ch.pipeline();

           if(commonHTTP.get().isDumpLog())pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

           pipeline.addLast("httpSession",new SessionInitiationHandler());
           pipeline.addLast("httpDecoder",new HttpRequestDecoder());
           pipeline.addLast("httpEncoder",new HttpResponseEncoder());
           //pipeline.addLast("compressor",new HttpContentCompressor());
           pipeline.addLast("httpHandel",new lHttpServerHandler());

           looger.trace("HttpX509Server Initializer pipeline ok");

       }
}
