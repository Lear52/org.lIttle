package org.little.smtp.util.command;

import java.util.ArrayList;

import org.little.smtp.util.SmtpSessionContext;
import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


/**
 * some kind of PING
 * @author thomas
 *
 */
public class ExpandMailingList extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(ExpandMailingList.class);

        public ExpandMailingList(){
               this.command    = SmtpCommand.EXPN;
        }

	@Override
	public CharSequence getCommandVerb() {
		return "EXPN";
	}
	/*
	//@Override
	public SmtpSrvResponse processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

               SmtpSrvResponse reply = new SmtpSrvResponse(SmtpSrvResponseStatus.R502, "TODO");

               logger.trace("reply:"+reply);
               return reply;
	}
	*/
	@Override
	public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<CharSequence> list_cmd) {
               String log_str="";

               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);
               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R502, "TODO");

               logger.trace("reply:"+reply);
        return reply;
	}

}
