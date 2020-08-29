package org.little.smtp.command;

import java.util.ArrayList;

import org.little.smtp.SessionContext;
import org.little.smtp.util.SmtpCommandReply;
import org.little.smtp.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


/**
 * some kind of PING
 * @author thomas
 *
 */
public class ExpandMailingList extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(ExpandMailingList.class);

	@Override
	public CharSequence getCommandVerb() {
		return "EXPN";
	}

	@Override
	public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

               SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R502, "TODO");

               logger.trace("reply:"+reply);
               return reply;
	}
	@Override
	public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
               String log_str="";

               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);
               SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R502, "TODO");

               logger.trace("reply:"+reply);
        return reply;
	}

}
