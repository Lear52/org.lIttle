package org.little.smtp.util.command;

import java.util.ArrayList;
import java.util.List;

import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.smtp.util.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class ExtendedHello extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(ExtendedHello.class);

        public ExtendedHello(){
               this.command    = SmtpCommand.EHLO;
        }

        //@Override
        //public CharSequence getCommandVerb() {
        //        return "EHLO";
        //}
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
                
/*               
                for(SmtpCommand cmd: SmtpRegistry.get().getHelloKeywords(ctxMailSession)) {
                        CharSequence helloKeyword = cmd.getHelloKeyword(ctxMailSession);
                        if(helloKeyword != null) {
                                String line = helloKeyword.toString();
                                //if(cmd.getHelloParams(ctxMailSession) != null) {
                                //        for(CharSequence param: cmd.getHelloParams(ctxMailSession)) {
                                //                line += " " + param;
                                //        }
                                //}
                                lines.add(line);
                        }
                }
  */             
                logger.info("using lines: "+lines);
               
                SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, lines);
               
                logger.trace("reply:"+reply);
               
                return reply;
            }

}
