package org.little.imap.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;

import org.little.imap.SessionContext;
//import io.netty.handler.codec.imap.ImapCommand;
import org.little.imap.command.ImapCommand;
import org.little.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class ImapCommandHandler extends SimpleChannelInboundHandler<ImapCommand> {

       private static final Logger logger = LoggerFactory.getLogger(ImapCommandHandler.class);
       

       public ImapCommandHandler() {       
              logger.trace("create ImapCommandHandler");
       }

       @Override
       protected void channelRead0(ChannelHandlerContext ctx, ImapCommand request) throws Exception {

              logger.trace("begin channelRead0 get ImapCommand:"+request);

              SessionContext sessionContext = ctx.channel().attr(SessionContext.ATTRIBUTE_KEY).get();

              ArrayList<ImapResponse> response=request.doProcess(sessionContext); 
              
              ByteBuf buf = ctx.alloc().buffer();
              for(int i=0;i<response.size();i++) {
                  logger.trace("ImapResponse["+i+"]:"+response.get(i));
                  response.get(i).write(buf);
                  buf.writeByte('\r');
                  buf.writeByte('\n');
              }
              ctx.writeAndFlush(buf);
              logger.trace("end channelRead0 ImapCommand:"+request);

       }
}
