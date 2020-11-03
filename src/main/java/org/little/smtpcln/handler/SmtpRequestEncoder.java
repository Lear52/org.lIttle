package org.little.smtpcln.handler;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.little.smtpcln.rr.LastSmtpContent;
import org.little.smtpcln.rr.SmtpCommand;
import org.little.smtpcln.rr.SmtpContent;
import org.little.smtpcln.rr.SmtpRequest;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * Encoder for SMTP requests.
 */
public class SmtpRequestEncoder extends MessageToMessageEncoder<Object> {
       private static final Logger        logger = LoggerFactory.getLogger(SmtpRequestEncoder.class);
       private static final int           CRLF_SHORT = ('\r' << 8) | '\n';
       private static final byte          SP = ' ';
       private static final ByteBuf       DOT_CRLF_BUFFER = Unpooled.unreleasableBuffer(Unpooled.directBuffer(3).writeByte('.').writeByte('\r').writeByte('\n'));
      
       private boolean contentExpected;
      

       public SmtpRequestEncoder(){contentExpected=false;}

       @Override
       public boolean acceptOutboundMessage(Object msg) throws Exception {
              boolean ret=msg instanceof SmtpRequest || msg instanceof SmtpContent;
              logger.trace("msg instanceof SmtpRequest || msg instanceof SmtpContent  :"+ret);
              return ret;
       }
      
       @Override
       protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
                 logger.trace("encode msg:"+msg);

                 if(msg instanceof SmtpRequest){
                    SmtpRequest req = (SmtpRequest) msg;
                    logger.trace("begin encode msg is SmtpRequest");


                    if(contentExpected) {
                       if(req.command().equals(SmtpCommand.RSET)) {
                             contentExpected = false;
                       } 
                       else {
                             throw new IllegalStateException("SmtpContent expected");
                       }
                    }
                
                    boolean release = true;
                    ByteBuf buffer  = ctx.alloc().buffer();
                
                    try{
                        req.command().encode(buffer);
                
                        boolean notEmpty = req.command() != SmtpCommand.EMPTY;
                
                        writeParameters(req.parameters(), buffer, notEmpty);
                        ByteBufUtil.writeShortBE(buffer, CRLF_SHORT);
                
                        out.add(buffer);
                
                        release = false;
                        if(req.command().isContentExpected()){
                           //for DATA and for AUTH
                           contentExpected = true;
                        }
                        logger.trace("SmtpRequest write req:"+req);
                    } 
                    finally {
                        if (release) {
                            buffer.release();
                        }
                    }
                    logger.trace("end encode msg is SmtpRequest");
                 }
                
                 if(msg instanceof SmtpContent){
                    SmtpContent body=((SmtpContent) msg);
                    if (!contentExpected) {
                         throw new IllegalStateException("No SmtpContent expected");
                    }
                    logger.trace("begin encode msg is SmtpContent");
                
                    //ByteBuf content = body.content();
                    //out.add(content.retain());
                    //if (msg instanceof LastSmtpContent) {
                    //    out.add(DOT_CRLF_BUFFER.retainedDuplicate());
                    //    contentExpected = false;
                    //}

                    ByteBuf buffer  = ctx.alloc().buffer();
                    buffer.writeBytes(body.body);
                    buffer.writeByte('.').writeByte('\r').writeByte('\n');
                   
                    out.add(buffer);

                    logger.trace("end encode msg is SmtpContent");
                 }
       }
      
       private static void writeParameters(List<CharSequence> parameters, ByteBuf out, boolean commandNotEmpty) {
               if (parameters.isEmpty()) {
                   return;
               }
               if (commandNotEmpty) {
                   out.writeByte(SP);
               }
               if (parameters instanceof RandomAccess) {
                   final int sizeMinusOne = parameters.size() - 1;
                   for (int i = 0; i < sizeMinusOne; i++) {
                       ByteBufUtil.writeAscii(out, parameters.get(i));
                       out.writeByte(SP);
                   }
                   ByteBufUtil.writeAscii(out, parameters.get(sizeMinusOne));
               } else {
                   final Iterator<CharSequence> params = parameters.iterator();
                   for (;;) {
                       ByteBufUtil.writeAscii(out, params.next());
                       if (params.hasNext()) {
                           out.writeByte(SP);
                       } else {
                           break;
                       }
                   }
               }
       }
}

