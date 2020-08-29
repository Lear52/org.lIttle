package org.little.smtp.command;

import java.util.ArrayList;

import org.little.smtp.SessionContext;
import org.little.smtp.commonSMTP;
import org.little.smtp.util.SmtpCommandReply;
import org.little.smtp.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class VerifyMailbox extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(VerifyMailbox.class);

        @Override
        public CharSequence getCommandVerb() {return "VRFY";}

        @Override
        public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {

                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

                boolean isValid = verifyUserOrMailbox(argument.toString());
                SmtpCommandReply reply;
                if(isValid) reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");
                else        reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");

                logger.trace("reply:"+reply);
                return reply;
        }
    	@Override
        public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
               String log_str="";
               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);
               boolean isValid = verifyUserOrMailbox(list_cmd.get(1));
               SmtpCommandReply reply;
               if(isValid) reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");
               else        reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");

               logger.trace("reply:"+reply);
               return reply;
        }

        private boolean verifyUserOrMailbox(String argument) {
                return commonSMTP.get().verifyUser(argument.toString());
        }
}
