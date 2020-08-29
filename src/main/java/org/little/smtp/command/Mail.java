package org.little.smtp.command;

import java.util.ArrayList;

import org.little.smtp.MailTransaction;
import org.little.smtp.SessionContext;
import org.little.smtp.util.SmtpCommandReply;
import org.little.smtp.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Mail extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(Mail.class);

        @Override
        public CharSequence getCommandVerb() {
                return "MAIL";
        }

        @Override
        public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
               if(argument==null)logger.trace(getCommandVerb().toString());
               else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

               MailTransaction mailTx = new MailTransaction();
               logger.trace("begin mail transaction");
               mailTx.setFrom(argument);

                /*
                 * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
                 *   "7BIT" / "8BITMIME"
                 * and add it to the mailTransaction
                 */
               ctxMailSession.mailTransaction = mailTx;

               SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }
    	@Override
        public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
               String log_str="";
               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);

               MailTransaction mailTx = new MailTransaction();
               logger.trace("begin mail transaction");
               mailTx.setFrom(list_cmd.get(1));

             /*
              * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
              *   "7BIT" / "8BITMIME"
              * and add it to the mailTransaction
              */
               ctxMailSession.mailTransaction = mailTx;

               SmtpCommandReply reply = new SmtpCommandReply(SmtpReplyStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }

}
