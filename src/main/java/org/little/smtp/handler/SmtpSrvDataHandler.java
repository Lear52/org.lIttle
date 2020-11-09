package org.little.smtp.handler;

import org.little.smtp.util.SmtpSessionContext;
import org.little.smtp.util.SmtpResponse;
import org.little.smtp.util.SmtpResponseStatus;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * rfc5321 SMTP server - Handle data lines
 *
 * https://tools.ietf.org/html/rfc5321
 * @author thomas
 *
 */
public class SmtpSrvDataHandler extends ChannelInboundHandlerAdapter {

       private static Logger logger = LoggerFactory.getLogger(SmtpSrvDataHandler.class);

       @Override
       public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
              ByteBuf            bbLine         = (ByteBuf) msg;
              SmtpSessionContext sessionContext = ctx.channel().attr(SmtpSessionContext.ATTRIBUTE_KEY).get();

              transformLine(bbLine);

              // is the line a single dot i.e. end of DATA?
              if(bbLine.readableBytes() == 1 && bbLine.getByte(bbLine.readerIndex()) == '.') {
                 //if so, switch back to command handler
                 ctx.pipeline().replace(this, "smptInCommand", new SmtpSrvCommandHandler());

                 logger.trace("replace smptInCommand new SmtpCommandHandler()");

                 boolean rc = sessionContext.mailTransaction.mailFinished();
                 //FIXME: reset mailtransaction?!
                 sessionContext.mailTransaction = null;

                 logger.trace("sessionContext.mailTransaction.mailFinished() rc:"+rc);

                 if(rc) {
                    Object reply = new SmtpResponse(SmtpResponseStatus.R250, "OK");
                    ctx.writeAndFlush(reply);
                 } else {
                    Object reply = new SmtpResponse(SmtpResponseStatus.R450, "FAILED");
                    ctx.writeAndFlush(reply);
                 }
              } else {
                 sessionContext.mailTransaction.addDataLine(bbLine.copy()); //TODO: copy or retain?!
              }
       }

       private void transformLine(ByteBuf line) {
               if(line.readableBytes() >= 1 && line.getByte(line.readerIndex()) == '.') {
                       if(line.readableBytes() > 1) {
                          line.readByte(); // consume '.' if there are other characters on the line
                       }
               }
       }
}

