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
public class Quit extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(Quit.class);

        public Quit(){
               super(SmtpCommand.QUIT);
        }
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R221, "BYE");
               ctxChannel.writeAndFlush(reply);
               ctxChannel.close();

               logger.trace("reply:"+reply);

               return null;
        }

}
