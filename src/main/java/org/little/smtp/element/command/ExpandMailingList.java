package org.little.smtp.element.command;

import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class ExpandMailingList extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(ExpandMailingList.class);

        public ExpandMailingList(){
               super(SmtpCommand.EXPN);
        }

        public ExpandMailingList(CharSequence checkNotNull) {
               super(SmtpCommand.EXPN,checkNotNull);
       }

        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R502, "TODO");

               logger.trace("reply:"+reply);
               return reply;
        }

}
