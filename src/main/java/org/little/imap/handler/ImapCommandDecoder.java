package org.little.imap.handler;

import java.util.List;

import org.little.imap.command.AtomDecoder;
import org.little.imap.command.ImapCommand;
import org.little.imap.command.ImapCommandBuilder;
import org.little.imap.command.ImapCommandParameter;
import org.little.imap.command.ImapParameterDecoder;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;


public class ImapCommandDecoder extends ByteToMessageDecoder {

       private static final Logger logger = LoggerFactory.getLogger(ImapCommandDecoder.class);

       private enum State {
              READ_TAG,
              READ_COMMAND, 
              READ_PARAMTERS
       }

       private State                 currentState;
       private AtomDecoder           atomDecoder = new AtomDecoder();
       private ImapCommandBuilder    builder;
       private ImapParameterDecoder  paramDecoder = new ImapParameterDecoder(false);

       public ImapCommandDecoder(){
              logger.trace("create ImapCommandDecoder");
              resetNow();
       }
       @Override
       protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                 logger.trace("decode currentState:"+currentState);
                
                 switch (currentState) {
                 case READ_TAG: {
                        logger.trace("begin READ_TAG");
                        String atom = atomDecoder.parse(in);
                        if (atom == null) {
                            logger.trace("atom == null currentState:"+currentState);
                            return;
                        }
                           
                        builder.tag(atom);logger.trace("READ_TAG:"+atom);
                
                        if (in.getByte(in.readerIndex()) == ' ') {
                            in.skipBytes(1);
                            currentState = State.READ_COMMAND;
                        } else {
                            CorruptedFrameException e= new CorruptedFrameException();
                            Except ex=new Except("READ_TAG:",e);
                            logger.error("ex:"+ex);
                            throw e;
                        }
                        logger.trace("end READ_TAG");
                 }
                 case READ_COMMAND: {
                        logger.trace("begin READ_COMMAND");
                        String atom = atomDecoder.parse(in);
                        if (atom == null) {
                            logger.trace("atom == null currentState:"+currentState);
                            return;
                        }
                
                        atomDecoder.reset();
                        builder.command(atom); logger.trace("READ_COMMAND:"+atom);
                
                        currentState = State.READ_PARAMTERS;
                        logger.trace("end READ_COMMAND");
                 }
                 case READ_PARAMTERS: {
                        logger.trace("begin READ_PARAMTERS");
                        ImapCommandParameter param = null;
                        int count=0;
                        while ((param = paramDecoder.next(ctx, in)) != null) {
                               builder.addParam(param); 
                               logger.trace("builder.addParam:"+param);
                               count++;
                        }
                        logger.trace("add param:"+count);

                        if (paramDecoder.getState() == ImapParameterDecoder.State.Ended) {

                               ImapCommand r = builder.build();
                               //---------------------------------------------------
                               logger.trace("build ImapCommand:"+r);
                               out.add(r);
                               logger.trace("add ImapCommand:"+r.getClass().getName());
                               //===================================================
                               resetNow();
                               //break;
                        }
                        logger.trace("end READ_PARAMTERS");
                 }
                 }
                
       }

       private void resetNow() {
              builder      = new ImapCommandBuilder();
              atomDecoder.reset();
              currentState = State.READ_TAG;
              paramDecoder.reset();

              logger.trace("reset imap currentState:"+currentState);
       }

}
