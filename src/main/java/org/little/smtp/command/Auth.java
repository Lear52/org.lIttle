package org.little.smtp.command;

import java.util.ArrayList;

import org.little.smtp.AuthTransaction;
import org.little.smtp.SessionContext;
import org.little.smtp.util.SmtpCommandReply;
import org.little.smtp.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Auth extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(Auth.class);

        @Override
        public CharSequence getCommandVerb() {
                return "AUTH";
        }
        public static String getCommandAuth() {
        	//return "AUTH PLAIN LOGIN";
        	return "AUTH PLAIN";
        }

        @Override
        public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel,CharSequence argument) {
               if(argument==null)logger.trace("SMTP:command:"+getCommandVerb().toString());
               else              logger.trace("SMTP:command:"+getCommandVerb().toString()+" "+argument.toString());

               AuthTransaction authTx = new AuthTransaction();
               logger.trace("SMTP:begin auth transaction");
               authTx.setType(argument);
               logger.trace("SMTP:type auth:"+argument.toString());
               logger.trace("SMTP:isTypeLogin():"+authTx.isTypeLogin());
               logger.trace("SMTP:isTypePlain():"+authTx.isTypePlain());

                /*
                 * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
                 *   "7BIT" / "8BITMIME"
                 * and add it to the mailTransaction
                 */
               ctxMailSession.authTransaction = authTx;
               SmtpCommandReply reply;
               if(authTx.isTypeLogin())reply = new SmtpCommandReply(SmtpReplyStatus.R334,"VXNlciBOYW1lAA=="); // "User Name"
               else
               if(authTx.isTypePlain())reply = new SmtpCommandReply(SmtpReplyStatus.R334, "");
               else                    reply = new SmtpCommandReply(SmtpReplyStatus.R500, "AUTH?");
               logger.trace("SMTP:reply:"+reply.toString());
               
               /*
               else{
                  logger.error("already authenticated :"+cmd);
                  // unknown command
                  Object reply = new SmtpCommandReply(SmtpReplyStatus.R503, "already authenticated");
                  ctx.writeAndFlush(reply);
                  return;
               }
               */
               
               
               return reply;
        }
        @Override
        public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
               String log_str="";
               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);
               SmtpCommandReply reply;
               if(ctxMailSession.authTransaction!=null)
               if(ctxMailSession.authTransaction.isAuth()) {
                   logger.error("SMTP:already authenticated :"+log_str);
                   reply = new SmtpCommandReply(SmtpReplyStatus.R503, "already authenticated");
                   return reply;
               }

               AuthTransaction authTx = new AuthTransaction();
               logger.trace("SMTP:create auth transaction");
               authTx.setType(list_cmd.get(1));
               logger.trace("SMTP:isTypeLogin():"+authTx.isTypeLogin());
               logger.trace("SMTP:isTypePlain():"+authTx.isTypePlain());

             /*
              * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
              *   "7BIT" / "8BITMIME"
              * and add it to the mailTransaction
              */
              ctxMailSession.authTransaction = authTx;
              if(authTx.isTypePlain()){
                 if(list_cmd.size()>2){

                    logger.trace("SMTP:inline Plain:"+list_cmd.get(2));
                    authTx.setPlain(list_cmd.get(2));
                    logger.trace("SMTP:isAuth:"+authTx.isAuth());

                    if(ctxMailSession.authTransaction.isAuth()){
                       reply = new SmtpCommandReply(SmtpReplyStatus.R235, "2.7.0 Authentication successful");
                    }
                    else{
                       reply = new SmtpCommandReply(SmtpReplyStatus.R530, "5.7.0  Authentication required");
                    }

                 }
                 else reply = new SmtpCommandReply(SmtpReplyStatus.R334, "");
              }
              else
              if(authTx.isTypeLogin())reply = new SmtpCommandReply(SmtpReplyStatus.R334,"VXNlciBOYW1lAA=="); // "User Name"
              else                    reply = new SmtpCommandReply(SmtpReplyStatus.R500, "AUTH?");

              logger.trace("SMTP:reply:"+reply.toString());
            
            /*
            }
            */
            
            
            return reply;
                 
                    
            }

}
