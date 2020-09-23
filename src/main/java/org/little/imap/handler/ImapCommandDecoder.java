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
              READ_PARAMTERS,
              READ_APPEND
       }

       private State                 currentState;
       private AtomDecoder           atomDecoder = new AtomDecoder();
       private ImapCommandBuilder    builder;
       private ImapParameterDecoder  paramDecoder = new ImapParameterDecoder(false);
       private ImapCommand request; 

       public ImapCommandDecoder(){
              //logger.trace("create ImapCommandDecoder");
              resetNow();
       }
       @Override
       protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> requests) throws Exception {

                 logger.trace("decode currentState:"+currentState);
                
                 switch (currentState) {
                 //=====================================================================================================
                 case READ_TAG: {
                        logger.trace("begin READ_TAG");
                        String atom = atomDecoder.parse(in);
                        if (atom == null) {
                            logger.trace("atom == null currentState:"+currentState);
                            return;
                        }
                           
                        builder.tag(atom);
                        logger.trace("READ_TAG:"+atom);
                
                        char _nextByte = (char) in.getByte(in.readerIndex());
                        if (_nextByte == ' ') {
                            in.skipBytes(1);
                            currentState = State.READ_COMMAND;
                        } 
                        else {
                            CorruptedFrameException e= new CorruptedFrameException();
                            Except ex=new Except("CorruptedFrameException READ_TAG  ("+_nextByte+" no ' ')   :",e);
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
                        builder.command(atom); //logger.trace("READ_COMMAND:"+atom);
                
                        currentState = State.READ_PARAMTERS;
                        logger.trace("end READ_COMMAND");
                 }
                 case READ_PARAMTERS: {
                        logger.trace("begin READ_PARAMTERS");
                        ImapCommandParameter param = null;
                        int count=0;
                        while ((param = paramDecoder.next(ctx, in)) != null) {
                               logger.trace("paramDecoder.getState:"+paramDecoder.getState()+"param:"+param);
                               builder.addParam(param); 
                               //logger.trace("builder.addParam:"+param);
                               count++;
                        }
                        logger.trace("--- in.readableBytes():"+in.readableBytes());
                        logger.trace("end while -   add param:"+count);
                        logger.trace("paramDecoder.getState:"+paramDecoder.getState());

                        if(paramDecoder.getState() == ImapParameterDecoder.State.Ended) {
                           request = builder.build();
                           //---------------------------------------------------
                           logger.trace("build ImapCommand:"+request);
                           //r.appendBuf(in);
                           //logger.trace("append ImapCommand");
                           
                           requests.add(request);
                           
                           logger.trace("add ImapCommand:"+request.getClass().getName());
                           //===================================================
                           if(request.isAppend()==false) {
                              resetNow();
                              return;
                           }
                           else {
                              currentState = State.READ_APPEND;
                              return;
                           }
                           //break;
                        }
                        logger.trace("end READ_PARAMTERS");
                        return;
                 }
                 case READ_APPEND: {
                      if(request!=null){
                	 boolean ret=request.appendBuf(in);
                	 if(ret==true) {
                            requests.add(request);
                	    resetNow();
                	 }
                      }
                 }
                 //=====================================================================================================
                 }
                
       }

       private void resetNow() {
              builder      = new ImapCommandBuilder();
              currentState = State.READ_TAG;
              atomDecoder .reset();
              paramDecoder.reset();
              request=null;
              logger.trace("reset imap currentState:"+currentState);
       }

}
