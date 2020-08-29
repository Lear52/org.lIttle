package org.little.smtp.command;

import java.util.ArrayList;

import org.little.smtp.SessionContext;
import org.little.smtp.util.SmtpCommandReply;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class EightBitMime extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(EightBitMime.class);
	
	@Override
	public CharSequence getHelloKeyword(SessionContext ctx) {
		return "8BITMIME";
	}

	@Override
	public CharSequence getCommandVerb() {
		return null;
	}

	@Override
	public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

               throw new IllegalStateException();
	}
	@Override
	public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
        String log_str="";
		for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
		logger.trace(log_str);

		throw new IllegalStateException();

	}

}
