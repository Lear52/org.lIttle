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


public class Recipent extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(Recipent.class);

        public Recipent(){
               super(SmtpCommand.RCPT);
        }
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());
                
               
               StringBuilder to=new StringBuilder();
               for(int i=0;i< parameters.size();i++)to.append(parameters.get(i)).append(" ");

               if(ctxMailSession!=null) {
                   SmtpMailTransaction mailTx = ctxMailSession.mailTransaction;
                   if(mailTx!=null) mailTx.addTo(to.toString());
               }
               
               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");

               logger.trace("reply:"+reply);
               return reply;
        }

}
