package org.little.smtpcln.handler;

import java.util.ArrayDeque;

import org.little.smtpcln.rr.SmtpElement;
import org.little.smtpcln.rr.SmtpResponse;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SmtpCommandHandler extends ChannelInboundHandlerAdapter {
       private static final Logger        logger = LoggerFactory.getLogger(SmtpCommandHandler.class);
       private ArrayDeque<SmtpElement>  queue;
       private int index;

       public SmtpCommandHandler() {
              logger.trace("constructor SmtpCommandHandler");
              queue=new ArrayDeque<SmtpElement>();
              index=1;
       }

       public void addQueue(SmtpElement request) {
                  request.setID(index); 
                  index++;
                  queue.addLast(request);
       }
     
       @Override
       public void channelActive(ChannelHandlerContext ctx) {

              //SmtpRequest request=SmtpRequests.ehlo("localhost");

              Channel in_channel = ctx.channel();
              logger.trace("channelActive SmtpCommandHandler ");
              if(!queue.isEmpty()) {
                 SmtpElement request=queue.pollFirst();
              
                 logger.trace("request:"+request+" id:"+request.getID());

                 in_channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                     @Override
                     public void operationComplete(ChannelFuture future) {
                         if (future.isSuccess()) {
                             logger.trace("SmtpCommandHandler write ");
                             ctx.channel().read();
                         } else {
                             logger.trace("SmtpCommandHandler  disconnect");
                             future.channel().close();
                         }
                     }
                 }
                 );
             }
             

       }
       
       @Override
       public void channelRead(final ChannelHandlerContext ctx, Object msg) {

              logger.trace("channelRead object:"+msg.getClass().getName());
          
              if(msg instanceof SmtpResponse) {
            	  SmtpResponse res=(SmtpResponse)msg; 
                  logger.trace("read Response:"+res);
             
              }
              

       }
       @Override
       public void channelReadComplete(ChannelHandlerContext ctx) {
           logger.trace("channelReadComplete");

           Channel in_channel = ctx.channel();
           if(!queue.isEmpty()) {
              SmtpElement request=queue.poll();

              logger.trace("request:"+request+" id:"+request.getID());
           
              in_channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                  @Override
                  public void operationComplete(ChannelFuture future) {
                      if (future.isSuccess()) {
                          logger.trace("SmtpCommandHandler write ");
                          ctx.channel().read();
                      } else {
                          logger.trace("SmtpCommandHandler  disconnect");
                          future.channel().close();
                      }
                  }
              }
              );
           }
           else{
               logger.trace("queue is clear chanel disconnect");
               if(in_channel.isActive()) {
                  in_channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
               }
           }
           
       }
       
       
       @Override
       public void channelInactive(ChannelHandlerContext ctx) {
              
           logger.trace("channelInactive");
       }
     
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
              Except _e=new Except("SmtpCommandHandler.exception",cause);
              logger.trace("channelRead  e:"+_e);
       }

}

