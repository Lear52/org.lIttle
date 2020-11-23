package org.little.smtp.element.command;

import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Help extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(Help.class);

        public Help(){
               super(SmtpCommand.HELP);
        }
        public Help(CharSequence hostname){
               super(SmtpCommand.HELP,hostname);
        }
    	@Override
    	public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }

}
