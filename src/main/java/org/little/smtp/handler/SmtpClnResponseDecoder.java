package org.little.smtp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.little.util.LoggerFactory;
import org.little.smtp.util.SmtpResponse;
import org.little.util.Logger;

/**
 * Decoder for SMTP responses.
 */
public class SmtpClnResponseDecoder extends LineBasedFrameDecoder {
       private static final Logger        logger = LoggerFactory.getLogger(SmtpClnResponseDecoder.class);

       private List<CharSequence> details;
      
       /**
        * Creates a new instance that enforces the given {@code maxLineLength}.
        */
       public SmtpClnResponseDecoder(int maxLineLength) {
           super(maxLineLength);
       }
      
       @Override
       protected SmtpResponse decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
                 ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
                 if (frame == null) {
                     // No full line received yet.
                     return null;
                 }
                 logger.trace("begin decode");
                 try {
                     final int readable    = frame.readableBytes();
                     final int readerIndex = frame.readerIndex();
                     if (readable == 0) { 
                         logger.trace("return SmtpResponse:null");
                         return null;
                     }
                     if (readable < 3) {
                         throw newDecoderException(buffer, readerIndex, readable);
                     }
                     final int code = parseCode(frame);
                     final int separator = frame.readByte();
                     final CharSequence detail = frame.isReadable() ? frame.toString(CharsetUtil.US_ASCII) : null;
                
                     List<CharSequence> details = this.details;
                
                     switch (separator) {
                     case ' ':
                         // Marks the end of a response.
                         this.details = null;
                         if (details != null) {
                             if (detail != null) {
                                 details.add(detail);
                             }
                         } else {
                             if (detail == null) {
                                 details = Collections.emptyList();
                             } else {
                                 details = Collections.singletonList(detail);
                             }
                         }
                         SmtpResponse res=new SmtpResponse(code, details);
                         logger.trace("end decode");
                         logger.trace("return SmtpResponse:"+res);
                         return res;
                     case '-':
                         // Multi-line response.
                         if (detail != null) {
                             if (details == null) {
                                 // Using initial capacity as it is very unlikely that we will receive a multi-line response
                                 // with more then 3 lines.
                                 this.details = details = new ArrayList<CharSequence>(4);
                             }
                             details.add(detail);
                         }
                         break;
                     default:
                         throw newDecoderException(buffer, readerIndex, readable);
                     }
                 } 
                 finally {
                     frame.release();
                 }
                 logger.trace("end decode");
                 logger.trace("return SmtpResponse:null");
                 return null;
       }
      
       private static DecoderException newDecoderException(ByteBuf buffer, int readerIndex, int readable) {
           return new DecoderException("Received (size:"+readable+") invalid line: '" + buffer.toString(readerIndex, readable, CharsetUtil.US_ASCII) + '\'');
       }
      
       /**
        * Parses the io.netty.handler.codec.smtp code without any allocation, which is three digits.
        */
       private static int parseCode(ByteBuf buffer) {
           final int first  = parseNumber(buffer.readByte()) * 100;
           final int second = parseNumber(buffer.readByte()) * 10;
           final int third  = parseNumber(buffer.readByte());
           return first + second + third;
       }
      
       private static int parseNumber(byte b) {
           return Character.digit((char) b, 10);
       }
}
