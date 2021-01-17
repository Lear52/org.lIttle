package org.little.smtp.handler;
               
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.little.smtp.commonSMTP;
import org.little.smtp.element.SmtpAuthTransaction;
import org.little.smtp.element.SmtpCommand;
import org.little.smtp.element.SmtpMailTransaction;
import org.little.smtp.element.SmtpRequest;
import org.little.smtp.element.SmtpRequestBuilder;
import org.little.smtp.element.SmtpResponse;
import org.little.smtp.element.SmtpResponseStatus;
import org.little.smtp.element.SmtpSessionContext;
import org.little.smtp.element.command.Content;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string.charsequenceComparator;
import org.little.util.string.stringParser;

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

              if(sessionContext.mailTransaction==null){
                 // read smtp  command
                 channelReadRequest(ctx,sessionContext,frame);
              }
              else
              if(sessionContext.mailTransaction.mail_content==null) {
                 // read mail header in mail trasaction
                 channelReadRequest(ctx,sessionContext,frame);
              }
              else {
                 // read mail body 
                 channelReadContext(ctx,sessionContext,frame);
              }
               
               
       }
    
       private void channelReadContext(ChannelHandlerContext ctx,SmtpSessionContext sessionContext, ByteBuf buffer) throws Exception {

              if(buffer.readableBytes() >= 1 && buffer.getByte(buffer.readerIndex()) == '.') {
                  if(buffer.readableBytes() > 1) {
                     buffer.readByte(); // consume '.' if there are other characters on the line
                  }
               }

               // is the line a single dot i.e. end of DATA?
               if(buffer.readableBytes() == 1 && buffer.getByte(buffer.readerIndex()) == '.') {
                  SmtpResponse reply;
                  logger.trace("get request end send date (.) request.command:"+sessionContext.mailTransaction.mail_content.getCommand());
         
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
              
                 byte [] b=new byte [len+2];
                 if(len>0)buffer.readBytes(b,0,len);
                 b[len]='\r';
                 b[len+1]='\n';

                 sessionContext.mailTransaction.mail_content.add(new String(b)); 
              }

       }
       private void channelReadRequest(ChannelHandlerContext ctx,SmtpSessionContext sessionContext, ByteBuf frame) throws Exception {

              logger.trace("channelRead size:"+frame.readableBytes());

              //------------------------------------------------------------------------------------------------------- 
              // check length request
              //------------------------------------------------------------------------------------------------------- 
              if(frame.readableBytes() < 4) {
                 logger.error("readableBytes < 4");
                 //throw new UnknownCommandException("readableBytes < 4");
                 return;
              }

              //------------------------------------------------------------------------------------------------------- 
              // parse line request
              //------------------------------------------------------------------------------------------------------- 
              CharSequence line = frame.readCharSequence(frame.readableBytes(), StandardCharsets.US_ASCII);
              logger.trace("channelRead line:"+line.toString());
              stringParser            cmd_string=new stringParser(line.toString()," \r\n");
              ArrayList<CharSequence> list_cmd=cmd_string.getList();
              //------------------------------------------------------------------------------------------------------- 
              //
              //------------------------------------------------------------------------------------------------------- 
              SmtpResponse reply        = null;
              SmtpRequest  smtp_request = null;
              //------------------------------------------------------------------------------------------------------- 
              //
              //------------------------------------------------------------------------------------------------------- 
              if(sessionContext.authTransaction!=null){
                 // begin auth
                 logger.trace("auth transaction is not null session auth:"+sessionContext.authTransaction.isAuth());
                 //get login and passwd for request AUTH       
                 if(sessionContext.authTransaction.isAuth()==false) {
                    logger.trace("session not auth");
                    //--------------------------------------------------------------------------------------------------------
                    //
                    //--------------------------------------------------------------------------------------------------------
                    if(sessionContext.authTransaction.isTypePlain()) {
                       // after get  AUTH PLAIN
                       sessionContext.authTransaction.setPlain(list_cmd.get(0));

                       logger.trace("get request for AUTH PLAIN "+list_cmd.get(0)+" auth:"+sessionContext.authTransaction.isAuth());
                        
                       if(commonSMTP.get().isProxy()) { 
                          sessionContext.authTransaction.setAuth(true);
                          smtp_request = SmtpRequestBuilder.empty(list_cmd); // pack data to  smtp_request;        
                       }
                       else{
                          sessionContext.authTransaction.checkUser();
                          if(sessionContext.authTransaction.isAuth()){
                             reply = new SmtpResponse(SmtpResponseStatus.R235, "2.7.0 Authentication successful");                                        
                          }
                          else{
                             reply = new SmtpResponse(SmtpResponseStatus.R530, "5.7.0  Authentication required");
                          }
                       }
                    }        
                    else
                    //--------------------------------------------------------------------------------------------------------
                    if(sessionContext.authTransaction.isTypeLogin()){
                       // after get  AUTH LOGIN
                        logger.trace("get request AUTH LOGIN "+list_cmd.get(0));

                        if(sessionContext.authTransaction.isLogin()==false) {
                           // get login
                           sessionContext.authTransaction.setLogin(list_cmd.get(0));
                           smtp_request = SmtpRequestBuilder.empty(list_cmd);
                           
                           logger.trace("set login:"+sessionContext.authTransaction.getLogin());

                           if(commonSMTP.get().isProxy()) {
                              smtp_request = SmtpRequestBuilder.empty(list_cmd);
                           }
                           else {
                              reply = new SmtpResponse(SmtpResponseStatus.R334, "UGFzc3dvcmQA"); // Password
                           }
                        } 
                        else{
                            // get passwd
                            sessionContext.authTransaction.setPasswd(list_cmd.get(0)); 
                            logger.trace("set passwd");

                            if(commonSMTP.get().isProxy()) {
                               sessionContext.authTransaction.setAuth(true);
                               smtp_request = SmtpRequestBuilder.empty(list_cmd);
                            }
                            else{
                               sessionContext.authTransaction.checkUser(); 
                               if(sessionContext.authTransaction.isAuth()){
                                  reply = new SmtpResponse(SmtpResponseStatus.R235, "2.7.0 Authentication successful");
                               }
                               else{
                                  reply = new SmtpResponse(SmtpResponseStatus.R530, "5.7.0  Authentication required");
                               }
                            }
                        }
                    }
                    else
                    //--------------------------------------------------------------------------------------------------------
                    if(sessionContext.authTransaction.isTypePreset()){
                    }
                    //--------------------------------------------------------------------------------------------------------
                    else{
                         // expected date for "LOGIN" or "PLAIN" but received something that did not expect
                         smtp_request=null;
                         logger.error("Received unknown command:"+list_cmd.get(0));
                         // unknown auth type
                         reply = new SmtpResponse(SmtpResponseStatus.R500, "WAT?");
                    }
                 }
                 else { //sessionContext.authTransaction.isAuth()==true
                        logger.trace("session is auth !");
                 }
              } // end sessionContext.authTransaction!=null
              else{

              } // end sessionContext.authTransaction==null
               

              if(smtp_request==null && reply==null){

                 smtp_request = SmtpRequestBuilder.make(list_cmd);
                   
                 if(smtp_request == null) {
                    logger.error("Received unknown command:"+line);
                    // unknown command
                    reply = new SmtpResponse(SmtpResponseStatus.R500, "WAT?");
                 }
                 else {
                      boolean is_valid;
                      if(sessionContext.authTransaction==null){
                         is_valid=validatePreCommand(smtp_request);
                         if(is_valid==false){
                            logger.info("session requared auth");
                            reply = new SmtpResponse(SmtpResponseStatus.R530, "5.7.0  Authentication required");
                            ctx.writeAndFlush(reply);
                            return;
                         }
                      }
                      else{
                         is_valid=validateCommand(smtp_request);
                         if(is_valid){
                            is_valid=validateCommandOrder(sessionContext, smtp_request);/* order-independent commands: "The NOOP, HELP, EXPN, VRFY, and RSET commands can be used at any time during a session"*/
                         }
                      }
                     
                      logger.trace("command:"+smtp_request.getCommand()+" is validate:"+is_valid);

                     if(smtp_request.getCommand() == SmtpCommand.AUTH) {
                        SmtpAuthTransaction authTx = new SmtpAuthTransaction();
                        sessionContext.authTransaction = authTx;
                        authTx.setType(smtp_request.getParameters().get(0));
                        logger.trace("set session auth object");
                     }
                     else
                     if(smtp_request.getCommand() == SmtpCommand.MAIL) {
                        SmtpMailTransaction mailTx = new SmtpMailTransaction();
                        sessionContext.mailTransaction = mailTx;
                        mailTx.setFrom(smtp_request.getParameters().get(0));
                        logger.trace("set session mail object");
                     }
                     else
                     if(smtp_request.getCommand() == SmtpCommand.DATA) {
                        if(sessionContext.mailTransaction!=null)sessionContext.mailTransaction.mail_content= new Content(); 
                        logger.trace("set session content object");
                     }
                     
                 }
                   
              }
                
              if(reply!=null) {
                 ctx.writeAndFlush(reply);
                 return;
              }
                
                
              //------------------------------------------------------------------------------------------
              if(commonSMTP.get().isProxy()==false || smtp_request.getCommand() == SmtpCommand.STLS){
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
              sessionContext.lastCmd = line;
       }


       private static final CharSequence[] ALWAYS_ALLOWED_COMMANDS = new String[] {"HELO", "EHLO", "RSET"};

        /**
         * is the command allowed in the current state
         * @param ctx
         * @param cmd
         */
       private boolean validateCommandOrder(SmtpSessionContext ctx, SmtpRequest req) {
    	       String cmd=req.getCommand().getName().toString();
               if(Arrays.stream(ALWAYS_ALLOWED_COMMANDS).anyMatch(c -> charsequenceComparator.equals(c, cmd))) return true;

               if(ctx.lastCmd == null) {
                  return false;
               }

               // is a mail transaction going on already
               if(ctx.mailTransaction != null) {
                   if(charsequenceComparator.equals(cmd, "RCPT") && charsequenceComparator.equals(ctx.lastCmd, "MAIL") ||
                      charsequenceComparator.equals(cmd, "RCPT") && charsequenceComparator.equals(ctx.lastCmd, "RCPT") ||
                      charsequenceComparator.equals(cmd, "DATA") && charsequenceComparator.equals(ctx.lastCmd, "RCPT")
                     ) {
                           return true;
                   }
                   //throw new IllegalStateException();
                   return false;
               }
               return true;
       }

       private static final CharSequence[] VALID_COMMANDS     = new String[] {"HELP","HELO", "EHLO", "MAIL", "RSET", "VRFY", "EXPN", "NOOP", "QUIT" , "AUTH"};
       private static final CharSequence[] VALID_PRE_COMMANDS = new String[] {"HELP","HELO", "EHLO", "RSET", "NOOP", "QUIT", "AUTH"};

        /**
         * is this an valid SMTP command?
         * @param cmd
         */
       private boolean validateCommand(SmtpRequest req) {
    	   String cmd=req.getCommand().getName().toString();
               logger.trace("validateCommand cmd:"+cmd);

               return Arrays.stream(VALID_COMMANDS).anyMatch(c -> charsequenceComparator.equals(c, cmd));
       }
       private boolean validatePreCommand(SmtpRequest req) {
               String cmd=req.getCommand().getName().toString();
               logger.trace("validateCommand cmd:"+cmd);

               return Arrays.stream(VALID_PRE_COMMANDS).anyMatch(c -> charsequenceComparator.equals(c, cmd));
       }

}
