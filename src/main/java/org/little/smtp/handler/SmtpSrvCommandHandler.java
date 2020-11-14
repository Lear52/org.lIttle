package org.little.smtp.handler;
               
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.little.smtp.util.SmtpSessionContext;
import org.little.smtp.util.command.Content;
import org.little.smtp.util.SmtpRequestBuilder;
import org.little.smtp.commonSMTP;
import org.little.smtp.util.SmtpAuthTransaction;
import org.little.smtp.util.SmtpCommand;
import org.little.smtp.util.SmtpMailTransaction;
import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.util.CharSequenceComparator;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.stringParser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

/**
 * rfc5321 SMTP server
 *
 * https://tools.ietf.org/html/rfc5321
 * @author thomas
 *
 */
public class SmtpSrvCommandHandler extends ChannelInboundHandlerAdapter {

       private static Logger logger = LoggerFactory.getLogger(SmtpSrvCommandHandler.class);
       @Override
       public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
               ByteBuf frame = (ByteBuf) msg;
               //logger.trace("channelRead");
               Attribute<SmtpSessionContext> sessionStarted = ctx.channel().attr(SmtpSessionContext.ATTRIBUTE_KEY);
               if(sessionStarted == null) {
                   logger.error("!isSessionContext(ctx)");
                   return;
               }
               SmtpSessionContext sessionContext = ctx.channel().attr(SmtpSessionContext.ATTRIBUTE_KEY).get();
               if(sessionContext == null) {
                   logger.error("!isSessionContext(ctx)");
                   return;
               }

               if(sessionContext.mailTransaction==null) {
                      channelReadRequest(ctx,sessionContext,frame);
               }
               else
               if(sessionContext.mailTransaction.mail_content==null) {
                       channelReadRequest(ctx,sessionContext,frame);
               }
               else {
                       channelReadContext(ctx,sessionContext,frame);
               }
               
               
       }
       public void channelReadContext(ChannelHandlerContext ctx,SmtpSessionContext sessionContext, ByteBuf buffer) throws Exception {
              if(buffer.readableBytes() >= 1 && buffer.getByte(buffer.readerIndex()) == '.') {
                  if(buffer.readableBytes() > 1) {
                     buffer.readByte(); // consume '.' if there are other characters on the line
                  }
               }

               // is the line a single dot i.e. end of DATA?
               if(buffer.readableBytes() == 1 && buffer.getByte(buffer.readerIndex()) == '.') {
                  SmtpResponse reply;
                  logger.trace("get request end send date (.) request.command:"+sessionContext.mailTransaction.mail_content.command());

         
                  if(commonSMTP.get().isProxy()==false){
                    // process command
                    reply = sessionContext.mailTransaction.mail_content.processCommand(sessionContext, ctx);
                    if(reply != null) {
                       ctx.writeAndFlush(reply);
                    }
                  }  
                  else{
                     if(sessionContext.client!=null){
                        // send request to backend
                        logger.trace("0 write request to client channel");
                        reply = sessionContext.mailTransaction.mail_content.filterCommand();
                        if(reply==null) sessionContext.client.getChannel().writeAndFlush(sessionContext.mailTransaction.mail_content);
                        else            ctx.writeAndFlush(reply);
                        logger.trace("1 write request to client channel");
                        //
                    }
                  }

                  logger.trace("end mail trasaction");
    
                  sessionContext.resetMailTransaction();

              } 
              else {

                 int  len=buffer.readableBytes();
                 //logger.trace("addDataLine:"+len);
              
                 byte [] b=new byte [len+2];
                 if(len>0)buffer.readBytes(b,0,len);
                 b[len]='\r';
                 b[len+1]='\n';

                 sessionContext.mailTransaction.mail_content.add(new String(b)); 
              }

       }
       public void channelReadRequest(ChannelHandlerContext ctx,SmtpSessionContext sessionContext, ByteBuf frame) throws Exception {
              SmtpRequest smtp_request = null;

              logger.trace("channelRead size:"+frame.readableBytes());

              if(frame.readableBytes() < 4) {
                 logger.error("readableBytes < 4");
                 throw new UnknownCommandException("readableBytes < 4");
              }

              CharSequence line = frame.readCharSequence(frame.readableBytes(), StandardCharsets.US_ASCII);

              logger.trace("channelRead line:"+line.toString());
              //------------------------------------------------------------------------------------------------------- 
              // parse line request
              stringParser            cmds=new stringParser(line.toString()," \r\n");
              ArrayList<CharSequence> list_cmd=cmds.getList();

              //------------------------------------------------------------------------------------------------------- 
              SmtpResponse reply=null;
              if(sessionContext.authTransaction!=null){
                  logger.trace("auth transaction is not null session auth:"+sessionContext.authTransaction.isAuth());
                  //get login and passwd for request AUTH       
                  if(sessionContext.authTransaction.isAuth()==false) {
                     //logger.trace("session not auth");
                     //--------------------------------------------------------------------------------------------------------
                     if(sessionContext.authTransaction.isTypePlain()) {

                            sessionContext.authTransaction.setPlain(list_cmd.get(0));
                            
                        logger.trace("get request for AUTH PLAIN "+list_cmd.get(0));
                        
                            if(commonSMTP.get().isProxy()==false) {
                           if(sessionContext.authTransaction.isAuth()){
                                  reply = new SmtpResponse(SmtpResponseStatus.R235, "2.7.0 Authentication successful");                                        
                           }
                           else{
                              reply = new SmtpResponse(SmtpResponseStatus.R530, "5.7.0  Authentication required");
                           }
                            }
                            else {
                                    smtp_request = SmtpRequestBuilder.empty(list_cmd);          
                            }
                     }        
                     else
                     //--------------------------------------------------------------------------------------------------------
                     if(sessionContext.authTransaction.isTypeLogin()){
                        logger.trace("get command AUTH LOGIN "+list_cmd.get(0));

                        if(sessionContext.authTransaction.isLogin()==false) {
                           // get login
                           sessionContext.authTransaction.setLogin(list_cmd.get(0));
                           smtp_request = SmtpRequestBuilder.empty(list_cmd);
                           
                           logger.trace("set login"+sessionContext.authTransaction.getLogin());
                           if(commonSMTP.get().isProxy()==false) {
                              reply = new SmtpResponse(SmtpResponseStatus.R334, "UGFzc3dvcmQA"); // Password
                           }
                           else {
                                  smtp_request = SmtpRequestBuilder.empty(list_cmd);
                           }
                        } 
                        else{
                                // get login
                            sessionContext.authTransaction.setPasswd(list_cmd.get(0)); 
                            logger.trace("set passwd");

                            if(commonSMTP.get().isProxy()==false) {
                               if(sessionContext.authTransaction.isAuth()){
                                  reply = new SmtpResponse(SmtpResponseStatus.R235, "2.7.0 Authentication successful");
                               }
                               else{
                                  reply = new SmtpResponse(SmtpResponseStatus.R530, "5.7.0  Authentication required");
                               }
                                }   
                            else {
                                    smtp_request = SmtpRequestBuilder.empty(list_cmd);
                            }
                        }
                     }
                     //--------------------------------------------------------------------------------------------------------
                     else{
                         smtp_request=null;
                         logger.error("Received unknown command:"+list_cmd.get(0));
                         // unknown auth type
                         reply = new SmtpResponse(SmtpResponseStatus.R500, "WAT?");
                     }
                  }
                  else {
                        logger.trace("session is auth !");
                  }
                } // end sessionContext.authTransaction.isAuth()==false
               
                String cmd="";
                if(smtp_request==null && reply==null){
                   cmd=list_cmd.get(0).toString();list_cmd.remove(0);
                   
                   validateCommand(cmd);
                   validateCommandOrder(sessionContext, cmd);/* order-independent commands: "The NOOP, HELP, EXPN, VRFY, and RSET commands can be used at any time during a session"*/
                   smtp_request = SmtpRequestBuilder.get().getCommand(cmd);
                   
                   if(smtp_request == null) {
                      logger.error("Received unknown command:"+cmd);
                      // unknown command
                      reply = new SmtpResponse(SmtpResponseStatus.R500, "WAT?");
                   }
                   else {
                       if(smtp_request.command() == SmtpCommand.AUTH) {
                          SmtpAuthTransaction authTx = new SmtpAuthTransaction();
                          authTx.setType(list_cmd.get(0));
                          sessionContext.authTransaction = authTx;
                          logger.trace("set session  auth object");
                       }
                       else
                       if(smtp_request.command() == SmtpCommand.MAIL) {
                          SmtpMailTransaction mailTx = new SmtpMailTransaction();
                          mailTx.setFrom(list_cmd.get(0));
                          sessionContext.mailTransaction = mailTx;
                          logger.trace("set session  mail  object");
                       }
                       else
                       if(smtp_request.command() == SmtpCommand.DATA) {
                          if(sessionContext.mailTransaction!=null)sessionContext.mailTransaction.mail_content= new Content(); 
                          logger.trace("set session  content  object");
                       }
                       smtp_request.setParam(list_cmd); 
                   }
                   
                }
                
                if(reply!=null) {
                    ctx.writeAndFlush(reply);
                    return;
                }
                
                
                //------------------------------------------------------------------------------------------
                if(commonSMTP.get().isProxy()==false){
                   // process command
                   reply = smtp_request.processCommand(sessionContext, ctx);
                   logger.trace("processCommand");
                   if(reply != null) {
                      ctx.writeAndFlush(reply);
                   }
                }
                else{
                     if(sessionContext.client!=null){
                        // send request to backend
                        logger.trace("write response to client channel");
                        reply = smtp_request.filterCommand();
                        if(reply==null) sessionContext.client.getChannel().writeAndFlush(smtp_request);
                        else {
                              // no send to backend
                              ctx.writeAndFlush(reply);
                        }
                        //
                    }
                }
                //------------------------------------------------------------------------------------------
                sessionContext.lastCmd = cmd;
       }

       //private boolean isSessionContext(ChannelHandlerContext ctx) {
       //         Attribute<SmtpSessionContext> sessionStarted = ctx.channel().attr(SmtpSessionContext.ATTRIBUTE_KEY);

       //         return sessionStarted.get() != null;
       //}

       private static final CharSequence[] ALWAYS_ALLOWED_COMMANDS = new String[] {"HELO", "EHLO", "RSET"};

        /**
         * is the command allowed in the current state
         * @param ctx
         * @param cmd
         */
       private boolean validateCommandOrder(SmtpSessionContext ctx, CharSequence cmd) {
                if(Arrays.stream(ALWAYS_ALLOWED_COMMANDS).anyMatch(c -> CharSequenceComparator.equals(c, cmd))) return true;

                if(ctx.lastCmd == null) {
                        return false;
                }

                // is a mail transaction going on already
                if(ctx.mailTransaction != null) {
                   if(CharSequenceComparator.equals(cmd, "RCPT") && CharSequenceComparator.equals(ctx.lastCmd, "MAIL") ||
                      CharSequenceComparator.equals(cmd, "RCPT") && CharSequenceComparator.equals(ctx.lastCmd, "RCPT") ||
                      CharSequenceComparator.equals(cmd, "DATA") && CharSequenceComparator.equals(ctx.lastCmd, "RCPT")
                     ) {
                           return true;
                   }
                   //throw new IllegalStateException();
                   return false;
                }
                return true;
       }

       private static final CharSequence[] VALID_COMMANDS = new String[] {"HELO", "EHLO", "MAIL", "RSET", "VRFY", "EXPN", "NOOP", "QUIT" , "AUTH"};

        /**
         * is this an valid SMTP command?
         * @param cmd
         */
       private boolean validateCommand(CharSequence cmd) {
                logger.trace("validateCommand cmd:"+cmd.toString());

                return Arrays.stream(VALID_COMMANDS).anyMatch(c -> CharSequenceComparator.equals(c, cmd));
       }

}
