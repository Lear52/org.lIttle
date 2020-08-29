package org.little.smtp.handler;

import org.little.smtp.SessionContext;
import org.little.smtp.commonSMTP;
import org.little.smtp.util.SmtpCommandReply;
import org.little.smtp.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

/**
 * rfc5321 SMTP server - "3.1 Session Initiation"
 *
 * https://tools.ietf.org/html/rfc5321
 * @author thomas
 *
 */
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

                SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R220, commonSMTP.get().getDefaultDomain());

                ctx.writeAndFlush(reply);

                logger.trace("channelActive Start new session");

        }

}
