package org.little.smtpsrv.command;

import java.util.ArrayList;

import org.little.smtpsrv.SessionContext;
import org.little.smtpsrv.util.SmtpCommandReply;
import org.little.smtpsrv.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Reset extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(Reset.class);

        @Override
        public CharSequence getCommandVerb() {
                return "RSET";
        }

        @Override
        public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());
               ctxMailSession.resetMailTransaction(); // abort any ongoing mail transaction
               SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }
        @Override
        public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
                String log_str="";
                for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
                logger.trace(log_str);
                ctxMailSession.resetMailTransaction(); // abort any ongoing mail transaction
        
                SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");
                logger.trace("reply:"+reply);
                return reply;
        }

}
