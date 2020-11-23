package org.little.smtp.element;

import org.little.smtp.SmtpClient;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * models the current mail session, i.e. connection
 *
 */
public class SmtpSessionContext {
       private static final Logger  logger = LoggerFactory.getLogger(SmtpSessionContext.class);

       public static final AttributeKey<SmtpSessionContext> ATTRIBUTE_KEY = AttributeKey.valueOf("sessionContext");

       public SmtpMailTransaction mailTransaction;
       public SmtpAuthTransaction authTransaction;
       public CharSequence        lastCmd;
       public boolean             tlsActive;


       public SmtpClient          client;


       public SmtpSessionContext(){
             resetMailTransaction();
             resetAuthTransaction();
             lastCmd=null;
             tlsActive=false;
             client   =null;
       }
       

       public void resetMailTransaction() {if(mailTransaction!=null)mailTransaction.mail_content=null;mailTransaction = null;}
       public void resetAuthTransaction() {authTransaction=null;}
       public void createClient(ChannelHandlerContext ctx) {
                
              client=new SmtpClient(ctx.channel());
                
              client.start();

              client.getChannelFuture().addListener(new ChannelFutureListener() {
                                                      @Override
                                                      public void operationComplete(ChannelFuture future) {
                                                          if(future.isSuccess()) {
                                                             ctx.channel().read();
                                                             logger.trace("connection complete start to read first data for channel");
                                                          } else {
                                                             ctx.channel().close();
                                                             logger.trace("Close the connection ");
                                                          }
                                                      }
              }
              );
                  
                  
       }

}
