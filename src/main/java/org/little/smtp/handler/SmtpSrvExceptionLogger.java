package org.little.smtp.handler;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public class SmtpSrvExceptionLogger implements ChannelHandler {

       private static final Logger logger = LoggerFactory.getLogger(SmtpSrvExceptionLogger.class);

       @Override
       public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
       }

       @Override
       public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
       }

       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
              logger.error("something went wrong!", cause);
       }

}
