package org.little.smtp.util.command;

import java.util.ArrayList;

import org.little.smtp.util.SmtpMailTransaction;
import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpSessionContext;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Mail extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(Mail.class);

        public Mail(){
               this.command    = SmtpCommand.MAIL;
        }
        @Override
        public CharSequence getCommandVerb() {
                return "MAIL";
        }
        /*
        //@Override
        public SmtpSrvResponse processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
               if(argument==null)logger.trace(getCommandVerb().toString());
               else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

               MailTransaction mailTx = new MailTransaction();
               logger.trace("begin mail transaction");
               mailTx.setFrom(argument);

               // 
               //  * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
               //  *   "7BIT" / "8BITMIME"
               //  * and add it to the mailTransaction
               
               ctxMailSession.mailTransaction = mailTx;

               SmtpSrvResponse reply = new SmtpSrvResponse(SmtpSrvResponseStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }
        */
    	@Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<CharSequence> list_cmd) {
               String log_str="";
               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);

               SmtpMailTransaction mailTx = new SmtpMailTransaction();
               logger.trace("begin mail transaction");
               mailTx.setFrom(list_cmd.get(1));

             /*
              * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
              *   "7BIT" / "8BITMIME"
              * and add it to the mailTransaction
              */
               ctxMailSession.mailTransaction = mailTx;

               SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               logger.trace("reply:"+reply);
               return reply;
        }

}