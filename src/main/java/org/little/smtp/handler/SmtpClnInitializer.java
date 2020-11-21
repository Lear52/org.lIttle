package org.little.smtp.handler;

import org.little.smtp.commonSMTP;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class SmtpClnInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  logger = LoggerFactory.getLogger(SmtpClnInitializer.class);
       private SmtpClnCommandHandler   command;

       public SmtpClnInitializer() {command=new SmtpClnCommandHandler();}

       @Override
       public void initChannel(SocketChannel ch) {
              if(commonSMTP.get().isDumpLog())ch.pipeline().addLast("smtpCLog"      , new LoggingHandler(LogLevel.DEBUG));
              ch.pipeline().addLast("smtpCRequest"  , new SmtpClnRequestEncoder());
              ch.pipeline().addLast("smtpCResponse" , new SmtpClnResponseDecoder(1024));
              ch.pipeline().addLast("ChunkedWrite"  , new ChunkedWriteHandler());
              ch.pipeline().addLast("smtpCCommand"  , command);

              logger.trace("client pipeline init");
       }

       public SmtpClnCommandHandler  getCommandHandler(){return command;}
}
