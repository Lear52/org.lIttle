package org.little.mail.smtp.element.command;

import org.little.mail.smtp.element.SmtpAuthTransaction;
import org.little.mail.smtp.element.SmtpCommand;
import org.little.mail.smtp.element.SmtpRequest;
import org.little.mail.smtp.element.SmtpResponse;
import org.little.mail.smtp.element.SmtpResponseStatus;
import org.little.mail.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Auth extends SmtpRequest{

        private static final Logger logger = LoggerFactory.getLogger(Auth.class);

        public Auth(){
               super(SmtpCommand.AUTH);
        }

        public Auth(CharSequence... parameters) {
               super(SmtpCommand.AUTH,parameters);
        }

        public static String getCommandAuth() {
                return "AUTH PLAIN LOGIN";
                //return "AUTH PLAIN";
        }
        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {

               logger.trace(toString());

               SmtpResponse reply=null;
               /*
                * TODO: process new optional parameter from 8bit-MIMEtransport (rfc1652):
                *   "7BIT" / "8BITMIME"
                * and add it to the mailTransaction
                */
              ctxMailSession.authTransaction=new SmtpAuthTransaction();
              ctxMailSession.authTransaction.setType(parameters.get(0));

              if(ctxMailSession.authTransaction.isTypePlain()){
                 if(parameters.size()>1){

                    ctxMailSession.authTransaction.setPlain(parameters.get(1));
                    ctxMailSession.authTransaction.checkUser();

                    logger.trace("SMTP:inline Plain:"+parameters.get(1)+" SMTP:isAuth:"+ctxMailSession.authTransaction.isAuth());

                    if(ctxMailSession.authTransaction.isAuth()){
                       reply = new SmtpResponse(SmtpResponseStatus.R235, "2.7.0 Authentication successful");
                    }
                    else{
                       reply = new SmtpResponse(SmtpResponseStatus.R530, "5.7.0  Authentication required");
                    }

                 }
                 else {
                    reply = new SmtpResponse(SmtpResponseStatus.R334, "");
                 }
              }
              else
              if(ctxMailSession.authTransaction.isTypeLogin()){
                 reply = new SmtpResponse(SmtpResponseStatus.R334,"VXNlciBOYW1lAA=="); // "User Name"
              }
              else{
                  reply = new SmtpResponse(SmtpResponseStatus.R500, "AUTH?");
              }
              
              logger.trace("SMTP:reply:"+reply.toString());
            
              return reply;
                    
        }

}
