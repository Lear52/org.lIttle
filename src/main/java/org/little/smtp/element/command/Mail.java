package org.little.smtp.element.command;

import java.util.List;

import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Mail extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(Mail.class);

        public Mail(){
               super(SmtpCommand.MAIL);
        }
        public Mail(List<CharSequence> parameters) {
               super(SmtpCommand.MAIL,parameters);
        }
        public Mail(SmtpCommand mail, String from) {
               super(SmtpCommand.MAIL,from);
        }
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }

}
