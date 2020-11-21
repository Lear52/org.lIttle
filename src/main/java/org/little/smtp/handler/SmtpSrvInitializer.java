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

              if(commonSMTP.get().isDumpLog())ch.pipeline().addLast("smtpSLog", new LoggingHandler(LogLevel.DEBUG));
              ch.pipeline().addLast("smtpSSutReply"   , new SmtpSrvReplyEncoder());
              ch.pipeline().addLast("smptSInitSession", new SmtpSrvInitSession());
              ch.pipeline().addLast("smtpSInLine"     , new DelimiterBasedFrameDecoder(commonSMTP.get().getDefaultCommandLen(), true, Delimiters.lineDelimiter()[0]));
              ch.pipeline().addLast("smptSInCommand"  , new SmtpSrvCommandHandler());
              ch.pipeline().addLast("exceptionSLogger", new SmtpSrvExceptionLogger());
              logger.trace("smtp server init");
       }
}
