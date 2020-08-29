package org.little.smtp.command;

import java.util.ArrayList;
import java.util.List;

import org.little.smtp.SessionContext;
import org.little.smtp.util.SmtpCommandReply;
import org.little.smtp.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class ExtendedHello extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(ExtendedHello.class);

        @Override
        public CharSequence getCommandVerb() {
                return "EHLO";
        }

        @Override
        public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {

                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

               ctxMailSession.resetMailTransaction();

               logger.trace("resetMailTransaction ");

               CharSequence domainOrAddressLiteral = argument;
               String greeting = "little SMTP server";

               List<String> lines = new ArrayList<String>();

               lines.add(domainOrAddressLiteral + " " + greeting);
               lines.add("SIZE" + " " + "102400");
               
               lines.add(Auth.getCommandAuth());
               

               for(SmtpCommand cmd: SmtpRegistry.get().getHelloKeywords(ctxMailSession)) {
                       CharSequence helloKeyword = cmd.getHelloKeyword(ctxMailSession);
                       if(helloKeyword != null) {
                               String line = helloKeyword.toString();
                               if(cmd.getHelloParams(ctxMailSession) != null) {
                                       for(CharSequence param: cmd.getHelloParams(ctxMailSession)) {
                                               line += " " + param;
                                       }
                               }
                               lines.add(line);
                       }
               }

               logger.info("using lines: "+lines);

               SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, lines);

               logger.trace("reply:"+reply);

               return reply;
        }
        @Override
        public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
                String log_str="";
                for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
                logger.trace(log_str);

                ctxMailSession.resetMailTransaction();

                logger.trace("resetMailTransaction ");

                String domainOrAddressLiteral="";            
                for(int i=1;i< list_cmd.size();i++) domainOrAddressLiteral = list_cmd.get(i);
                domainOrAddressLiteral += " little SMTP server";
               
                List<String> lines = new ArrayList<String>();
               
                lines.add(domainOrAddressLiteral);
                lines.add("SIZE" + " " + "102400");
                lines.add(Auth.getCommandAuth());
                
               
                for(SmtpCommand cmd: SmtpRegistry.get().getHelloKeywords(ctxMailSession)) {
                        CharSequence helloKeyword = cmd.getHelloKeyword(ctxMailSession);
                        if(helloKeyword != null) {
                                String line = helloKeyword.toString();
                                if(cmd.getHelloParams(ctxMailSession) != null) {
                                        for(CharSequence param: cmd.getHelloParams(ctxMailSession)) {
                                                line += " " + param;
                                        }
                                }
                                lines.add(line);
                        }
                }
               
                logger.info("using lines: "+lines);
               
                SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, lines);
               
                logger.trace("reply:"+reply);
               
                return reply;
            }

}
