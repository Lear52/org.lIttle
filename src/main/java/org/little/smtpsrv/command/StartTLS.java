package org.little.smtpsrv.command;

import java.io.File;
import java.util.ArrayList;

import org.little.smtpsrv.SessionContext;
import org.little.smtpsrv.util.SmtpCommandReply;
import org.little.smtpsrv.util.SmtpReplyStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;


/**
 * STARTTLS extension
 * https://tools.ietf.org/html/rfc3207
 * @author thomas
 *
 */
public class StartTLS extends AbstractSmtpCommand {

        private static final Logger logger = LoggerFactory.getLogger(StartTLS.class);

        @Override
        public CharSequence getHelloKeyword(SessionContext ctx) {
                if(!ctx.tlsActive)return "STARTTLS";
                return null;
        }

        @Override
        public CharSequence getCommandVerb() {
                return "STARTTLS";
        }

        @Override
        public SmtpCommandReply processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, CharSequence argument) {

                if(argument==null)logger.trace(getCommandVerb().toString());
                else              logger.trace(getCommandVerb().toString()+" "+argument.toString());

                if(argument != null){
                   SmtpCommandReply reply=new SmtpCommandReply(SmtpReplyStatus.R501, "Syntax error (no parameters allowed)");
                   logger.trace("reply:"+reply);
                   return reply;
                }

                SslContext        sslCtx         =null;
                SslContextBuilder context_builder=null;
                try {
                     File _certificate=new File("certificate3.pem");
                     File _privatekey=new File("privateKey.key");
                     //File _trastcert=new File("rootCA.pem");
                       
                     context_builder = SslContextBuilder.forServer(_certificate, _privatekey);
                     context_builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                     context_builder.sslProvider(SslProvider.JDK);
                     sslCtx = context_builder.build();

                     /*
                        File   key         = new File(SmtpConfig.getTlsKeyFile());
                        String keyPassword = SmtpConfig.getTlsKeyPassword();
                        File trustStore    = new File(SmtpConfig.getTlsTrustStoreFile());

                        SslContext sslCtx = SslContextBuilder.forServer(key, trustStore, keyPassword).build();
                     */
                     ctxChannel.pipeline().addFirst(new SslHandler(sslCtx.newEngine(ctxChannel.alloc()), true));

                     
                     ctxMailSession.resetMailTransaction();
                     ctxMailSession.tlsActive = true; // TODO: wait for handshake to finish, otherwise abort connection

                     SmtpCommandReply reply=new SmtpCommandReply(SmtpReplyStatus.R220, "Ready to start TLS");

                     logger.trace("reply:"+reply);
                     return reply;
                } catch(Exception e) {
                        logger.error("Failed to establish TLS!", e);
                        SmtpCommandReply reply=new SmtpCommandReply(SmtpReplyStatus.R454, "TLS not available due to temporary reason");
                        logger.trace("reply:"+reply);
                        return reply;
                }
        } 
        @Override
        public SmtpCommandReply   processCommand(SessionContext ctxMailSession, ChannelHandlerContext ctxChannel, ArrayList<String> list_cmd) {
               String log_str="";
               for(int i=0;i< list_cmd.size();i++)log_str+=list_cmd.get(i)+" ";
               logger.trace(log_str);
               if(list_cmd.size() > 1){
                   SmtpCommandReply reply=new SmtpCommandReply(SmtpReplyStatus.R501, "Syntax error (no parameters allowed)");
                   logger.trace("reply:"+reply);
                   return reply;
               }

               SslContext        sslCtx         =null;
               SslContextBuilder context_builder=null;
               try {
                  File _certificate=new File("certificate3.pem");
                  File _privatekey=new File("privateKey.key");
                  //File _trastcert=new File("rootCA.pem");
                    
                  context_builder = SslContextBuilder.forServer(_certificate, _privatekey);
                  context_builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                  context_builder.sslProvider(SslProvider.JDK);
                  sslCtx = context_builder.build();

                  /*
                     File   key         = new File(SmtpConfig.getTlsKeyFile());
                     String keyPassword = SmtpConfig.getTlsKeyPassword();
                     File trustStore    = new File(SmtpConfig.getTlsTrustStoreFile());

                     SslContext sslCtx = SslContextBuilder.forServer(key, trustStore, keyPassword).build();
                  */
                  ctxChannel.pipeline().addFirst(new SslHandler(sslCtx.newEngine(ctxChannel.alloc()), true));

                  
                  ctxMailSession.resetMailTransaction();
                  ctxMailSession.tlsActive = true; // TODO: wait for handshake to finish, otherwise abort connection

                  SmtpCommandReply reply=new SmtpCommandReply(SmtpReplyStatus.R220, "Ready to start TLS");

                  logger.trace("reply:"+reply);
                  return reply;
               } catch(Exception e) {
                     logger.error("Failed to establish TLS!", e);
                     SmtpCommandReply reply=new SmtpCommandReply(SmtpReplyStatus.R454, "TLS not available due to temporary reason");
                     logger.trace("reply:"+reply);
                     return reply;
               }
        }

}
