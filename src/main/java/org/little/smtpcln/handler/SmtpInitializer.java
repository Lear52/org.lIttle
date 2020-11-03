package org.little.smtpcln.handler;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class SmtpInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  looger = LoggerFactory.getLogger(SmtpInitializer.class);
       private SmtpCommandHandler   command;
       public SmtpInitializer() {command=new SmtpCommandHandler();}

       @Override
       public void initChannel(SocketChannel ch) {
              ch.pipeline().addLast("snmpLog"      , new LoggingHandler(LogLevel.INFO));
              ch.pipeline().addLast("smptRequest"  , new SmtpRequestEncoder());
              ch.pipeline().addLast("smtpResponse" , new SmtpResponseDecoder(1024));
              ch.pipeline().addLast("ChunkedWrite" , new ChunkedWriteHandler());
              ch.pipeline().addLast("smtpCommand"  , command);
       }

       public SmtpCommandHandler  getCommandHandler(){return command;}
}
