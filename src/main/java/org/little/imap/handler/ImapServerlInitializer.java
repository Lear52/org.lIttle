package org.little.imap.handler;

import org.little.imap.commonIMAP;
import org.little.ssl.SSLHandlerProvider0;
import org.little.util.Logger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

//import io.netty.handler.codec.imap.*;
//import io.netty.handler.codec.imap.server.*;

import io.netty.handler.ssl.SslHandler;


public  class ImapServerlInitializer extends ChannelInitializer<SocketChannel> {
              private static final Logger logger = Logger.getLogger(ImapServerlInitializer.class);

        @Override
        public void initChannel(SocketChannel ch) throws Exception {

                     ChannelPipeline pipeline = ch.pipeline();

                     if(commonIMAP.get().isSSL()){
                         SslHandler ssl_handler=SSLHandlerProvider0.getSSLHandler();
                         pipeline.addLast(ssl_handler);
                     }
                     if(commonIMAP.get().isDumpLog())pipeline.addLast("IMAPLog",new LoggingHandler(LogLevel.DEBUG));
                     pipeline.addLast("IMAPSession",new SessionInitiationHandler());
                     pipeline.addLast("IMAPCommandDecoder",new ImapCommandDecoder());
                     pipeline.addLast("IMAPCommandHandler",new ImapCommandHandler());

                     logger.trace("initChannel OK");

         }

}


