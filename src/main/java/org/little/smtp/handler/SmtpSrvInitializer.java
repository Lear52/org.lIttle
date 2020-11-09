package org.little.smtp.handler;

import org.little.smtp.commonSMTP;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SmtpSrvInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger  logger = LoggerFactory.getLogger(SmtpSrvInitializer.class);

       public SmtpSrvInitializer() {}

       @Override
       public void initChannel(SocketChannel ch) {
              ch.pipeline().addLast("log"            , new LoggingHandler(LogLevel.INFO));
              ch.pipeline().addLast("smtpOutReply"   , new SmtpSrvReplyEncoder());
              ch.pipeline().addLast("smptInitSession", new SmtpSrvInitSession());
              ch.pipeline().addLast("smtpInLine"     , new DelimiterBasedFrameDecoder(commonSMTP.get().getDefaultCommandLen(), true, Delimiters.lineDelimiter()[0]));
              ch.pipeline().addLast("smptInCommand"  , new SmtpSrvCommandHandler());
              ch.pipeline().addLast("exceptionLogger", new SmtpSrvExceptionLogger());
              logger.trace("smtp server init");
       }
}
