package org.little.http.handler;

import org.little.http.lHttpApp;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

public class SessionInitiationHandler extends ChannelInboundHandlerAdapter {

        private static Logger logger = LoggerFactory.getLogger(SessionInitiationHandler.class);

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

                lHttpRequest            http_request   = lHttpApp.create();

                Attribute<lHttpRequest> sessionStarted = ctx.channel().attr(lHttpRequest.ATTRIBUTE_KEY);

                sessionStarted.set(http_request);

                logger.trace("HttpServer Start new session:"+ctx.channel().id().asShortText());

        }

}
