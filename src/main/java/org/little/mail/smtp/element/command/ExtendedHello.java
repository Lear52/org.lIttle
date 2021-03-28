package org.little.mail.smtp.element.command;

import java.util.ArrayList;
import java.util.List;

import org.little.mail.smtp.element.SmtpCommand;
import org.little.mail.smtp.element.SmtpRequest;
import org.little.mail.smtp.element.SmtpResponse;
import org.little.mail.smtp.element.SmtpResponseStatus;
import org.little.mail.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class ExtendedHello extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(ExtendedHello.class);

        public ExtendedHello(){
               super(SmtpCommand.EHLO);
        }
        public ExtendedHello(CharSequence hostname){
               super(SmtpCommand.EHLO,hostname);
        }

        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
                String log_str="";
                for(int i=0;i< parameters.size();i++)log_str+=parameters.get(i)+" ";
                logger.trace(log_str);

                ctxMailSession.resetMailTransaction();

                logger.trace("resetMailTransaction ");

                String domainOrAddressLiteral="";            
                for(int i=1;i< parameters.size();i++) domainOrAddressLiteral = parameters.get(i).toString();
                domainOrAddressLiteral += " little SMTP server";
               
                List<String> lines = new ArrayList<String>();
               
                lines.add(domainOrAddressLiteral);
                lines.add("SIZE" + " " + "102400");
                lines.add(Auth.getCommandAuth());
                
                logger.info("using lines: "+lines);
               
                SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, lines);
               
                logger.trace("reply:"+reply);
               
                return reply;
            }

}
