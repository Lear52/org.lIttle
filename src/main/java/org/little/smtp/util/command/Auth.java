package org.little.smtp.util.command;

import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.smtp.util.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Auth extends SmtpRequest{

        private static final Logger logger = LoggerFactory.getLogger(Auth.class);

        public Auth(){
               this.command    = SmtpCommand.AUTH;
        }


        public static String getCommandAuth() {
        	return "AUTH PLAIN LOGIN";
        	//return "AUTH PLAIN";
        }
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {

               String log_str="";
               for(int i=0;i< parameters.size();i++)log_str+=parameters.get(i)+" ";
               logger.trace(log_str);

               SmtpResponse reply=null;
               /*
               if(ctxMailSession.authTransaction!=null)
               if(ctxMailSession.authTransaction.isAuth()) {
                   logger.error("SMTP:already authenticated :"+log_str);
                   reply = new SmtpResponse(SmtpResponseStatus.R503, "already authenticated");
                   return reply;
               }
               
               
               SmtpAuthTransaction authTx = new SmtpAuthTransaction();

               logger.trace("SMTP:create auth transaction");
               authTx.setType(parameters.get(0));
               logger.trace("SMTP:isTypeLogin():"+authTx.isTypeLogin()+" SMTP:isTypePlain():"+authTx.isTypePlain());
               ctxMailSession.authTransaction = authTx;
               */
               /*
                * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
                *   "7BIT" / "8BITMIME"
                * and add it to the mailTransaction
                */
              
              if(ctxMailSession.authTransaction.isTypePlain()){
                 if(parameters.size()>1){

                    logger.trace("SMTP:inline Plain:"+parameters.get(2));
                    ctxMailSession.authTransaction.setPlain(parameters.get(1));
                    logger.trace("SMTP:isAuth:"+ctxMailSession.authTransaction.isAuth());

                    if(ctxMailSession.authTransaction.isAuth()){
                       reply = new SmtpResponse(SmtpResponseStatus.R235, "2.7.0 Authentication successful");
                    }
                    else{
                       reply = new SmtpResponse(SmtpResponseStatus.R530, "5.7.0  Authentication required");
                    }

                 }
                 else reply = new SmtpResponse(SmtpResponseStatus.R334, "");
              }
              else
              if(ctxMailSession.authTransaction.isTypeLogin())reply = new SmtpResponse(SmtpResponseStatus.R334,"VXNlciBOYW1lAA=="); // "User Name"
              else                                            reply = new SmtpResponse(SmtpResponseStatus.R500, "AUTH?");
              
              logger.trace("SMTP:reply:"+reply.toString());
            
              return reply;
                    
        }

}
