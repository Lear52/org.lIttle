package org.little.mail.imap.handler;

import org.little.mail.imap.SessionContext;
import org.little.mail.imap.command.cmd.CapabilityCommand;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

public class SessionInitiationHandler extends ChannelInboundHandlerAdapter {
        private static Logger logger = LoggerFactory.getLogger(SessionInitiationHandler.class);
        /** 
         * This can wait up to 5 minutes according to spec to wait for the system load to hit a certain level
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
               SessionContext            sc             = new SessionContext();
               Attribute<SessionContext> sessionStarted = ctx.channel().attr(SessionContext.ATTRIBUTE_KEY);
               sessionStarted.set(sc);

               String reply="* OK IMAPrev1 \r\n"
                           +"* "+CapabilityCommand.getCapabilityResponse()+" \r\n"
                           ;
               logger.trace("reply:"+reply);

               ByteBuf buf = ctx.alloc().buffer();
               ByteBufUtil.writeAscii(buf, reply);
               ctx.writeAndFlush(buf);
               logger.trace("channelActive");
        }

}
