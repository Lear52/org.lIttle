package org.little.mail.smtp.handler;

import java.util.ArrayDeque;

import org.little.mail.smtp.element.SmtpRequest;
import org.little.mail.smtp.element.SmtpResponse;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SmtpClnCommandHandler extends ChannelInboundHandlerAdapter {
       private static final Logger      logger = LoggerFactory.getLogger(SmtpClnCommandHandler.class);
       private ArrayDeque<SmtpRequest>  queue;
       private SmtpRequest              request;
       private Channel                  in_channel;
       private Channel                  out_channel;

       public SmtpClnCommandHandler() {
              logger.trace("constructor SmtpCommandHandler");

              queue=new ArrayDeque<SmtpRequest>();
              request=null;
              in_channel=null;
              out_channel=null;
       }

       public void addQueue(SmtpRequest request) {
              queue.addLast(request);
       }
       public void setChannel(Channel ch){out_channel=ch;}
     
       @Override
       public void channelActive(ChannelHandlerContext ctx) {

              in_channel = ctx.channel();

              logger.trace("start channel client");
              if(!queue.isEmpty()) {
                 request=queue.pollFirst();
                 //---------------------------------------------------------------------------------------------------
                 writeRequest(request);
                 logger.trace("write request:"+request);
                 //---------------------------------------------------------------------------------------------------
             }

       }
       
       @Override
       public void channelRead(final ChannelHandlerContext ctx, Object msg) {

              logger.trace("channelRead object:"+msg.getClass().getName());

              //logger.trace("channel id:"+in_channel.id().asShortText()+" id:"+ctx.channel().id().asShortText());
          
              if(msg instanceof SmtpResponse) {
                 SmtpResponse res=(SmtpResponse)msg; 

                 if(request!=null)request.addResponse(res);

                 if(out_channel!=null){
                    logger.trace("prewrite response to server channel");
                    out_channel.writeAndFlush(res).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
                                       if (future.isSuccess()) {
                                           logger.trace("write response");
                                           out_channel.read();
                                       } else {
                                           logger.trace("SmtpCommandHandler  disconnect");
                                           future.channel().close();
                                           clouse_in_channel();
                                       }
                                       }
                                }
                    );
                    logger.trace("write response to server channel");
                   
                 }
                 logger.trace("read Response:"+res);
              }
              
                   
       }
       @Override
       public void channelReadComplete(ChannelHandlerContext ctx) {
              logger.trace("channelReadComplete");
              //logger.trace("channel id:"+in_channel.id().asShortText()+" id:"+ctx.channel().id().asShortText());

              //in_channel = ctx.channel();
              //------------------------------------------------------------
              request=null;
              if(out_channel==null){
              //------------------------------------------------------------
                 if(!queue.isEmpty()) {
                    request=queue.poll();
                
                    logger.trace("request:"+request);
                    //---------------------------------------------------------------------------------------------------
                    writeRequest(request);
                    //---------------------------------------------------------------------------------------------------
                 }
                 else{
                     logger.trace("queue is clear chanel disconnect");
                     if(in_channel.isActive()) {
                        in_channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                     }
                 }
              }
           
       }
       public void writeRequest(SmtpRequest _request) {
              //logger.trace("writeRequest:"+_request);
              in_channel.writeAndFlush(_request).addListener(new ChannelFutureListener() {
                  @Override
                  public void operationComplete(ChannelFuture future) {
                      if (future.isSuccess()) {
                          logger.trace("SmtpCommandHandler write ");
                          in_channel.read();
                      } else {
                          logger.trace("SmtpCommandHandler  disconnect");
                          future.channel().close();
                          clouse_out_channel();
                      }
                  }
              }
              );
              //logger.trace("writeRequest:"+_request+" Ok");

       }
       private void clouse_in_channel() {
              if(in_channel!=null) {
                 if(in_channel.isActive()) {
                    in_channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                 }
              }
       }
       private void clouse_out_channel() {
              if(out_channel!=null) {
                 if(out_channel.isActive()) {
                    out_channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                 }
              }
       }
       
       @Override
       public void channelInactive(ChannelHandlerContext ctx) {
              
              logger.trace("channelInactive");
              clouse_out_channel();
       }
     
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
              Except _e=new Except("SmtpCommandHandler.exception",cause);
              logger.trace("channelRead  e:"+_e);
       }

}

