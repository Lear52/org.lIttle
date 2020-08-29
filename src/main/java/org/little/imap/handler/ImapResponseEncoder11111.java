package org.little.imap.handler;

import java.util.List;

import org.little.imap.response.ImapResponse;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class ImapResponseEncoder11111 extends MessageToMessageEncoder<Object> {

       private static final Logger logger = LoggerFactory.getLogger(ImapResponseEncoder11111.class);

       @Override
       protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
                 logger.trace("encode msg:"+msg.getClass().getName()+" out List<Object> size:"+out.size());
                 if(msg instanceof ImapResponse) {
                    ImapResponse cmd = (ImapResponse) msg;
                    logger.trace("msg:"+cmd);
                    ByteBuf buf = ctx.alloc().buffer();
                    cmd.write(buf);
                    buf.writeByte('\r');
                    buf.writeByte('\n');
                    ctx.writeAndFlush(buf);
                 }
       }

}