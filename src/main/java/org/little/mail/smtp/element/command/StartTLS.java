package org.little.mail.smtp.element.command;

import org.little.mail.smtp.commonSMTP;
import org.little.mail.smtp.element.SmtpCommand;
import org.little.mail.smtp.element.SmtpRequest;
import org.little.mail.smtp.element.SmtpResponse;
import org.little.mail.smtp.element.SmtpResponseStatus;
import org.little.mail.smtp.element.SmtpSessionContext;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.ssl.SSLHandlerProvider;


//import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.ssl.SslContext;
//import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
//import io.netty.handler.ssl.SslProvider;
//import io.netty.handler.ssl.util.InsecureTrustManagerFactory;


/**
 * STARTTLS extension
 * https://tools.ietf.org/html/rfc3207
 * @author thomas
 *
 */
public class StartTLS extends SmtpRequest {

        private static final Logger logger = LoggerFactory.getLogger(StartTLS.class);

        public StartTLS(){
               super(SmtpCommand.STLS);
        }
        public CharSequence getHelloKeyword(SmtpSessionContext ctx) {
                if(!ctx.tlsActive)return "STARTTLS";
                return null;
        }
        //private SslHandler SslHandlerBuilder(ByteBufAllocator buf){
        //
        //       SslContext        sslCtx         =null;
        //       SslContextBuilder context_builder=null;
        //       try {
        //          File _certificate=new File("certificate3.pem");
        //          File _privatekey=new File("privateKey.key");
        //          //File _trastcert=new File("rootCA.pem");
        //            
        //          context_builder = SslContextBuilder.forServer(_certificate, _privatekey);
        //          context_builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        //          context_builder.sslProvider(SslProvider.JDK);
        //          sslCtx = context_builder.build();
        //
        //          /*
        //             File   key         = new File(SmtpConfig.getTlsKeyFile());
        //             String keyPassword = SmtpConfig.getTlsKeyPassword();
        //             File trustStore    = new File(SmtpConfig.getTlsTrustStoreFile());
        //
        //             SslContext sslCtx = SslContextBuilder.forServer(key, trustStore, keyPassword).build();
        //          */
        //          return new SslHandler(sslCtx.newEngine(buf), true);
        //
        //       } catch(Exception e) {
        //             logger.error("Failed to establish TLS!", e);
        //             return null;
        //       }
        //
        //}

        @Override
        public SmtpResponse   processCommand(SmtpSessionContext ctxMailSession, ChannelHandlerContext ctxChannel) {
               logger.trace(toString());

               if(parameters.size() > 1){
                   SmtpResponse reply=new SmtpResponse(SmtpResponseStatus.R501, "Syntax error (no parameters allowed)");
                   logger.trace("reply:"+reply);
                   return reply;
               }

               //SslContext        sslCtx         =null;
               //SslContextBuilder context_builder=null;
               try {
                  //ByteBufAllocator d = ctxChannel.alloc();
                  //SslHandler ssl_handler= SslHandlerBuilder(d);
                  ///*
                  //File _certificate=new File("certificate3.pem");
                  //File _privatekey=new File("privateKey.key");
                  ////File _trastcert=new File("rootCA.pem");
                  //  
                  //context_builder = SslContextBuilder.forServer(_certificate, _privatekey);
                  //context_builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                  //context_builder.sslProvider(SslProvider.JDK);
                  //sslCtx = context_builder.build();
                  //
                  //
                  //  // File   key         = new File(SmtpConfig.getTlsKeyFile());
                  //  // String keyPassword = SmtpConfig.getTlsKeyPassword();
                  //  // File trustStore    = new File(SmtpConfig.getTlsTrustStoreFile());
                  //  //
                  //  // SslContext sslCtx = SslContextBuilder.forServer(key, trustStore, keyPassword).build();
                  //
                  //ctxChannel.pipeline().addFirst(new SslHandler(sslCtx.newEngine(ctxChannel.alloc()), true));
                  //*/
                  //ctxChannel.pipeline().addFirst("SSL",ssl_handler);

                  SslHandler ssl_handler=SSLHandlerProvider.getSSLHandler(commonSMTP.get().getCfgSSL());
                  if(ssl_handler!=null){
                     ctxChannel.pipeline().addLast("SSLHandel"     ,ssl_handler);
                  }

                  
                  ctxMailSession.resetMailTransaction();
                  ctxMailSession.tlsActive = true; // TODO: wait for handshake to finish, otherwise abort connection

                  SmtpResponse reply=new SmtpResponse(SmtpResponseStatus.R220, "Ready to start TLS");

                  logger.trace("reply:"+reply);
                  return reply;
               } catch(Exception e) {
                     logger.error("Failed to establish TLS!", e);
                     SmtpResponse reply=new SmtpResponse(SmtpResponseStatus.R454, "TLS not available due to temporary reason");
                     logger.trace("reply:"+reply);
                     return reply;
               }
        }

}
