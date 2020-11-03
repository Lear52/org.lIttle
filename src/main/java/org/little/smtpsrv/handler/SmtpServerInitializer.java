package org.little.smtpsrv.handler;

import org.little.smtpsrv.commonSMTP;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SmtpServerInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  looger = LoggerFactory.getLogger(SmtpServerInitializer.class);

       public SmtpServerInitializer() {}

       @Override
       public void initChannel(SocketChannel ch) {
              ch.pipeline().addLast("log"            , new LoggingHandler(LogLevel.INFO));
              ch.pipeline().addLast("smtpOutReply"   , new SmtpReplyEncoder());
              ch.pipeline().addLast("smptInSession"  , new SessionInitiationHandler());
              ch.pipeline().addLast("smtpInLine"     , new DelimiterBasedFrameDecoder(commonSMTP.get().getDefaultCommandLen(), true, Delimiters.lineDelimiter()[0]));
              ch.pipeline().addLast("smptInCommand"  , new SmtpCommandHandler());
              ch.pipeline().addLast("exceptionLogger", new ExceptionLogger());
       }
}
