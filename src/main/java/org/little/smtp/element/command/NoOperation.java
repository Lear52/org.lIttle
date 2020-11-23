package org.little.smtp.element.command;

import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


/**
 * some kind of PING
 * @author thomas
 *
 */
public class NoOperation extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(NoOperation.class);

        public NoOperation(){
               super(SmtpCommand.NOOP);
        }

        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }

}
