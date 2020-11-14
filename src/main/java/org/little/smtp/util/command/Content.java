package org.little.smtp.util.command;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.smtp.util.SmtpSessionContext;
import org.little.store.ELM2lMessage;
import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.store.lMessageX509;
import org.little.store.lRoot;
import org.little.store.lStore;
import org.little.store.lUID;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;


public class Content extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(Content.class);

        public Content() {
               super(SmtpCommand.LASTCONTENT);
        }
        public Content(SmtpCommand command) {
               super(command);
        }
        public Content(SmtpCommand command, CharSequence... parameters) {
               super(command,parameters);
        }

        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               
               boolean rc = true;
               
               StringBuilder buf=new StringBuilder(); 
               for(int i=0;i< parameters.size();i++)buf.append(parameters.get(i));
               byte [] dest=buf.toString().getBytes();

               logger.trace("SMTP msg:\r\n"+new String(dest));

               //---------------------------------------------------------
               ByteArrayInputStream in_byte=new ByteArrayInputStream(dest);
               BufferedInputStream  in=new BufferedInputStream(in_byte);

               lMessage[] buf_message=ELM2lMessage.parse(in);

               if(buf_message==null){
                  logger.info("no SMTP msg");
                  rc = false;
               }
               else {
               for(int i=0;i<buf_message.length;i++){
                   if(buf_message[i]==null)continue;
                   buf_message[i]=lMessageX509.parse(buf_message[i]);
                   if(buf_message[i]==null)continue;

                   lMessage  msg  =buf_message[i];
                   String [] to   =msg.getTO();
                   String    from =msg.getFrom();
                   for(int j=0;j<to.length;j++){
                       String  store_name=to[j];
                       lStore  store     =lRoot.getStore(store_name);  if(store ==null)continue;
                       lFolder folder    =store.getInboxFolder();      if(folder==null)continue;
                       msg.setUID(lUID.get());
                       folder.save(msg);
                       folder.close();
                       store.close();
                   }
                   {
                     String  store_name=from;
                     lStore  store     =lRoot.getStore(store_name);  if(store ==null)continue;
                     lFolder folder    =store.getOutboxFolder();      if(folder==null)continue;
                     msg.setUID(lUID.get());
                     folder.save(msg);
                     folder.close();
                     store.close();

                   }

               }
               }
               logger.trace("save EML msg to XML msg");

               SmtpResponse reply;
               if(rc) {
                  reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
                  ctxChannel.writeAndFlush(reply);
               } else {
                  reply = new SmtpResponse(SmtpResponseStatus.R450, "FAILED");
                  ctxChannel.writeAndFlush(reply);
               }

               logger.trace("SMTP:reply:"+reply.toString());
            
            
            
            return reply;
                 
                    
            }

    	@Override
        public SmtpResponse   filterCommand() {
               SmtpResponse reply=null;
               //SmtpResponse reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
               return reply;

        }

}
