package org.little.smtp.handler;

import org.little.smtp.commonSMTP;
import org.little.smtp.element.SmtpAuthTransaction;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
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

                SmtpSessionContext            ctxMailSession             = new SmtpSessionContext();

                Attribute<SmtpSessionContext> sessionStarted = ctx.channel().attr(SmtpSessionContext.ATTRIBUTE_KEY);
                sessionStarted.set(ctxMailSession);

                if(commonSMTP.get().getAuthRequared()==false){
                   ctxMailSession.authTransaction=new SmtpAuthTransaction(true);
                }
                else{
                   ctxMailSession.authTransaction=null;
                }
                if(commonSMTP.get().isProxy()){
                   ctxMailSession.createClient(ctx);
                }
                else{
                   SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R220, commonSMTP.get().getDefaultDomain());
                   ctx.writeAndFlush(reply);
                }

                logger.trace("Start new smtp session");

        }

}
