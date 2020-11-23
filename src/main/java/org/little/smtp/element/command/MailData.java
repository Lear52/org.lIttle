package org.little.smtp.element.command;

import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpMailTransaction;
import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class MailData extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(MailData.class);

        public MailData(){
               super(SmtpCommand.DATA);
        }

        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               SmtpResponse reply=null;
               if(ctxMailSession!=null) {
                   SmtpMailTransaction mailTx = ctxMailSession.mailTransaction;
                   if(mailTx!=null) reply = new SmtpResponse(SmtpResponseStatus.R354, "Start mail input; end with <CRLF>.<CRLF>");
               }
               if(reply==null)reply = new SmtpResponse(SmtpResponseStatus.R550, "no mail session");

               logger.trace("reply:"+reply);

               return reply;
        }

}
