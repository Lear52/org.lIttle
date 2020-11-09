package org.little.smtp.util.command;

import java.util.ArrayList;

import org.little.smtp.commonSMTP;
import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpSessionContext;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class VerifyMailbox extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(VerifyMailbox.class);

        public VerifyMailbox(){
               this.command    = SmtpCommand.VRFY;
        }
        @Override
        public CharSequence getCommandVerb() {return "VRFY";}
        /*
        //@Override
        public SmtpSrvResponse processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {

                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

                boolean isValid = verifyUserOrMailbox(argument.toString());
                SmtpSrvResponse reply;
                if(isValid) reply = new SmtpSrvResponse(SmtpSrvResponseStatus.R250, "OK");
                else        reply = new SmtpSrvResponse(SmtpSrvResponseStatus.R250, "OK");

                logger.trace("reply:"+reply);
                return reply;
        }
        */
    	@Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<CharSequence> list_cmd) {
               String log_str="";
               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);
               boolean isValid = verifyUserOrMailbox(list_cmd.get(1).toString());
               SmtpResponse reply;
               if(isValid) reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               else        reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");

               logger.trace("reply:"+reply);
               return reply;
        }

        private boolean verifyUserOrMailbox(String argument) {
                return commonSMTP.get().verifyUser(argument.toString());
        }
}
