package org.little.smtpsrv.command;

import java.util.ArrayList;

import org.little.smtpsrv.MailTransaction;
import org.little.smtpsrv.SessionContext;
import org.little.smtpsrv.util.SmtpCommandReply;
import org.little.smtpsrv.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Recipent extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(Recipent.class);

	@Override
	public CharSequence getCommandVerb() {
		return "RCPT";
	}

	@Override
	public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {

                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

                //Path.parse(argument);
                MailTransaction mailTx = ctxMailSession.mailTransaction;
                mailTx.addTo(argument);

                SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");

                logger.trace("reply:"+reply);
                return reply;
        }
        @Override
        public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
                String log_str="";
                for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
                logger.trace(log_str);
                
                //Path.parse(argument);
                
                MailTransaction mailTx = ctxMailSession.mailTransaction;
                String to="";
                for(int i=1;i< list_cmd.size();i++)to+=list_cmd.get(i)+" ";
                
                mailTx.addTo(to);
                
                SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");

                logger.trace("reply:"+reply);
                return reply;
        }

}
