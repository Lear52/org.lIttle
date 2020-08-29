package org.little.smtp.handler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.little.smtp.SessionContext;
import org.little.smtp.command.SmtpCommand;
import org.little.smtp.util.SmtpCommandReply;
import org.little.smtp.command.SmtpRegistry;
import org.little.smtp.util.SmtpReplyStatus;
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
public class SmtpCommandHandler extends ChannelInboundHandlerAdapter {

        private static Logger logger = LoggerFactory.getLogger(SmtpCommandHandler.class);

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf frame = (ByteBuf) msg;

                logger.trace("channelRead");

                if(!isSessionContext(ctx)) {
                    /*FIXME: is this state even possible? */
                    logger.trace("!isSessionContext(ctx)");

                    /* we did receive an command before we send an initial greetings reply */
                    /* what to do here? is it possible to send a reply here?*/
                    return;
                }

                logger.trace("channelRead size:"+frame.readableBytes());

                if(frame.readableBytes() < 4) {
                   throw new UnknownCommandException("readableBytes < 4");
                }

                SessionContext sessionContext = ctx.channel().attr(SessionContext.ATTRIBUTE_KEY).get();

                CharSequence line = frame.readCharSequence(frame.readableBytes(), StandardCharsets.US_ASCII);

                logger.trace("channelRead line:"+line.toString());

                //int iac = line.length();
                //for(int i = 0, n = line.length(); i < n; i++) {
                //     if(line.charAt(i) == ' ') {
                //       iac = i;
                //        break;
                //    }
                //}

                //CharSequence cmd      = line.subSequence(0, iac);
                //CharSequence argument = line.length() > iac ? line.subSequence(iac, line.length()) : null;
                //if(argument==null)logger.trace("channelRead cmd:"+cmd.toString()+" arg:null");
                //else              logger.trace("channelRead cmd:"+cmd.toString()+" arg:"+argument.toString());

                stringParser cmds=new stringParser(line.toString()," \r\n");
                ArrayList<String> list_cmd=cmds.getList();
                {
                	//logger.trace("cmd:"+list_cmd.get(0));
                	//for(int i=0;i<list_cmd.size();i++)logger.trace("arg:"+list_cmd.get(i));
                	
                }

                if(sessionContext.authTransaction!=null){
                   logger.trace("auth transaction is not null");
                   logger.trace("session auth:"+sessionContext.authTransaction.isAuth());
                   //get command AUTH	
                   if(sessionContext.authTransaction.isAuth()==false) {
                      logger.trace("session not auth");
                      if(sessionContext.authTransaction.isTypePlain()) {
                         logger.trace("get command AUTH PLAIN");
                    	 sessionContext.authTransaction.setPlain(list_cmd.get(0));
                    	 if(sessionContext.authTransaction.isAuth()){
                            Object reply = new SmtpCommandReply(SmtpReplyStatus.R235, "2.7.0 Authentication successful");
                            ctx.writeAndFlush(reply);
                         }
                         else{
                            Object reply = new SmtpCommandReply(SmtpReplyStatus.R530, "5.7.0  Authentication required");
                            ctx.writeAndFlush(reply);
                         }
                    	 return; 
                      }	 
                      else
                      if(sessionContext.authTransaction.isTypeLogin()){
                         logger.trace("get command AUTH LOGIN");
                    	 if(sessionContext.authTransaction.isLogin()==false) {
                            sessionContext.authTransaction.setLogin(list_cmd.get(0)); 
                            logger.trace("set login");
                            Object reply = new SmtpCommandReply(SmtpReplyStatus.R334, "UGFzc3dvcmQA"); // Password
                            ctx.writeAndFlush(reply);
                            return;
                         } 
                         else {
                             sessionContext.authTransaction.setPasswd(list_cmd.get(0)); 
                             logger.trace("set passwd");
                             //Object reply = new SmtpCommandReply(SmtpReplyStatus.R235, "authenticated.");//"2.7.0 Authentication successful");
                             //ctx.writeAndFlush(reply);
                    	     if(sessionContext.authTransaction.isAuth()){
                                Object reply = new SmtpCommandReply(SmtpReplyStatus.R235, "2.7.0 Authentication successful");
                                ctx.writeAndFlush(reply);
                             }
                             else{
                                Object reply = new SmtpCommandReply(SmtpReplyStatus.R530, "5.7.0  Authentication required");
                                ctx.writeAndFlush(reply);
                             }
                             return;
                         }
                               
                      }
                      logger.error("Received unknown command:"+list_cmd.get(0));
                      // unknown command
                      Object reply = new SmtpCommandReply(SmtpReplyStatus.R500, "WAT?");
                      ctx.writeAndFlush(reply);
                      return;
                   }
                   logger.trace("session is auth !");

                  
                }
                // AUTH is OK


                //validateCommand(cmd);
                validateCommand(list_cmd.get(0));

                /* order-independent commands:
                 * "The NOOP, HELP, EXPN, VRFY, and RSET commands can be used at any time during a session"
                 */
                //validateCommandOrder(sessionContext, cmd);
                validateCommandOrder(sessionContext, list_cmd.get(0));

                /* process command */
                //SmtpCommand smtpCmd = SmtpRegistry.get().getCommand(cmd.toString());
                SmtpCommand smtpCmd = SmtpRegistry.get().getCommand(list_cmd.get(0));

                if(smtpCmd == null) {
                   logger.error("Received unknown command:"+list_cmd.get(0));
                   // unknown command
                   Object reply = new SmtpCommandReply(SmtpReplyStatus.R500, "WAT?");
                   ctx.writeAndFlush(reply);
                   return;
                }

                //SmtpCommandReply reply = smtpCmd.processCommand(sessionContext, ctx, argument);
                SmtpCommandReply reply = smtpCmd.processCommand(sessionContext, ctx, list_cmd);
                if(reply != null) {
                   ctx.writeAndFlush(reply);
                }

                sessionContext.lastCmd = list_cmd.get(0);//cmd;
        }

        /**
         * was this session started at all?
         * @param ctx
         * @return 
         */
        private boolean isSessionContext(ChannelHandlerContext ctx) {
                Attribute<SessionContext> sessionStarted = ctx.channel().attr(SessionContext.ATTRIBUTE_KEY);

                return sessionStarted.get() != null;
        }

        private static final CharSequence[] ALWAYS_ALLOWED_COMMANDS = new String[] {"HELO", "EHLO", "RSET"};

        /**
         * is the command allowed in the current state
         * @param ctx
         * @param cmd
         */
        private void validateCommandOrder(SessionContext ctx, CharSequence cmd) {
                if(Arrays.stream(ALWAYS_ALLOWED_COMMANDS).anyMatch(c -> CharSequenceComparator.equals(c, cmd))) return;

                if(ctx.lastCmd == null) {
                        return;
                }

                // is a mail transaction going on already
                if(ctx.mailTransaction != null) {
                   if(CharSequenceComparator.equals(cmd, "RCPT") && CharSequenceComparator.equals(ctx.lastCmd, "MAIL") ||
                      CharSequenceComparator.equals(cmd, "RCPT") && CharSequenceComparator.equals(ctx.lastCmd, "RCPT") ||
                      CharSequenceComparator.equals(cmd, "DATA") && CharSequenceComparator.equals(ctx.lastCmd, "RCPT")
                     ) {
                           return;
                   }
                   throw new IllegalStateException();
                }
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
