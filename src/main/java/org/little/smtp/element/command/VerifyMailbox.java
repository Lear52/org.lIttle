package org.little.smtp.element.command;

import org.little.smtp.commonSMTP;
import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class VerifyMailbox extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(VerifyMailbox.class);

        public VerifyMailbox(){
               super(SmtpCommand.VRFY);
        }
        public VerifyMailbox(CharSequence checkNotNull) {
               super(SmtpCommand.VRFY,checkNotNull);
        }
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               boolean isValid = commonSMTP.get().verifyUser(parameters.get(0).toString());

               SmtpResponse reply;
               if(isValid) reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               else        reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");

               logger.trace("reply:"+reply);
               return reply;
        }

}
