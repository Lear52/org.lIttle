package org.little.smtpsrv.handler;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public class ExceptionLogger implements ChannelHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionLogger.class);

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
