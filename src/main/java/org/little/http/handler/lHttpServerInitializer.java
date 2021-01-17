package org.little.http.handler;

import org.little.http.commonHTTP;
import org.little.ssl.SSLHandlerProvider;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
//import io.netty.handler.codec.http.HttpServerCodec;
//import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;


public class lHttpServerInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  looger = LoggerFactory.getLogger(lHttpServerInitializer.class);

       public lHttpServerInitializer() {}

       @Override
       public void initChannel(SocketChannel ch) {
           looger.trace("HttpServer Initializer pipeline");
           ChannelPipeline pipeline = ch.pipeline();

           if(commonHTTP.get().getCfgSSL().isSSL()){
              SslHandler ssl_handler=SSLHandlerProvider.getSSLHandler(commonHTTP.get().getCfgSSL());
              pipeline.addLast("SSLHandel"     ,ssl_handler);
           }

           if(commonHTTP.get().getCfgServer().isDumpLog())pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

           pipeline.addLast("httpSession",new SessionInitiationHandler());
           //------------------------------------------------------------------------
           int maxInitialLineLength=10240; 
           int maxHeaderSize       =20480;
           int maxChunkSize        =8192;

           pipeline.addLast("httptimeout",new ReadTimeoutHandler(30));

           pipeline.addLast("httpDecoder",new HttpRequestDecoder(maxInitialLineLength,maxHeaderSize,maxChunkSize));
           pipeline.addLast("httpEncoder",new HttpResponseEncoder());
           //pipeline.addLast("httpCoder",new HttpServerCodec());
           //pipeline.addLast("httpExHandler",new HttpServerExpectContinueHandler());

           //pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));/**/
           pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
           //pipeline.addLast("compressor",new HttpContentCompressor());

           //------------------------------------------------------------------------
           pipeline.addLast("httpHandel",new lHttpServerHandler());

           looger.trace("HttpServer Initializer pipeline ok");

       }
}
