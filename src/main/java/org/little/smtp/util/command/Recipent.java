package org.little.smtp.util.command;

import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpMailTransaction;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.smtp.util.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Recipent extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(Recipent.class);

        public Recipent(){
               this.command    = SmtpCommand.RCPT;
        }
	//@Override
	//public CharSequence getCommandVerb() {
	//	return "RCPT";
	//}
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               // String log_str="";
               // for(int i=0;i< parameters.size();i++)log_str+=parameters.get(i)+" ";
               // logger.trace(log_str);
               logger.trace(toString());
                
               SmtpMailTransaction mailTx = ctxMailSession.mailTransaction;
               String to="";
               for(int i=0;i< parameters.size();i++)to+=parameters.get(i)+" ";
               mailTx.addTo(to);
               
               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");

               logger.trace("reply:"+reply);
               return reply;
        }

}
