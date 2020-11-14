package org.little.smtp.util.command;

import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.smtp.util.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


/**
 * some kind of PING
 * @author thomas
 *
 */
public class NoOperation extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(NoOperation.class);

        public NoOperation(){
               this.command    = SmtpCommand.NOOP;
        }

        @Override
        public CharSequence getCommandVerb() {
                return "NOOP";
        }
        /*
        //@Override
        public SmtpSrvResponse processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());
               SmtpSrvResponse reply = new SmtpSrvResponse(SmtpSrvResponseStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }
        */
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               String log_str="";
               for(int i=0;i< parameters.size();i++)log_str+=parameters.get(i)+" ";
               logger.trace(log_str);

               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }

}
