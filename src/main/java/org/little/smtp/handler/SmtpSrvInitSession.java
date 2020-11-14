package org.little.smtp.handler;

import org.little.smtp.commonSMTP;
import org.little.smtp.util.SmtpSessionContext;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
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
public class SmtpSrvInitSession extends ChannelInboundHandlerAdapter {

        private static Logger logger = LoggerFactory.getLogger(SmtpSrvInitSession.class);

        /** 
         * This can wait up to 5 minutes according to spec to wait for the system load to hit a certain level
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

                SmtpSessionContext            sc             = new SmtpSessionContext();

                Attribute<SmtpSessionContext> sessionStarted = ctx.channel().attr(SmtpSessionContext.ATTRIBUTE_KEY);
                sessionStarted.set(sc);
                if(commonSMTP.get().isProxy()){
                   
                   sc.createClient(ctx);

                }
                else{
                    SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R220, commonSMTP.get().getDefaultDomain());
                    ctx.writeAndFlush(reply);
                }

                logger.trace("Start new smtp session");

        }

}
