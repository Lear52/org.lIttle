package org.little.smtp.util.command;

import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.smtp.util.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class MailData extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(MailData.class);

        public MailData(){
               this.command    = SmtpCommand.DATA;
        }

        //@Override
        //public CharSequence getCommandVerb() {
        //        return "DATA";
        //}
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
                //String log_str="";
                //for(int i=0;i< parameters.size();i++)log_str+=parameters.get(i)+" ";
                //logger.trace(log_str);
                logger.trace(toString());

                SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R354, "Start mail input; end with <CRLF>.<CRLF>");
                //logger.trace("reply:"+reply);

                return reply;
        }

}
