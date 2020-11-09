package org.little.smtp.handler;

import java.util.ArrayDeque;

import org.little.smtp.util.SmtpRequest;
import org.little.smtp.util.SmtpResponse;
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
       //private int                      index;
       private Channel                  in_channel;
       private Channel                  out_channel;

       public SmtpClnCommandHandler() {
              logger.trace("constructor SmtpCommandHandler");
              queue=new ArrayDeque<SmtpRequest>();
              //index=1;
              request=null;
              in_channel=null;
              out_channel=null;
       }

       public void addQueue(SmtpRequest request) {
              //request.setID(index); 
              //index++;
              queue.addLast(request);
       }
       //public Channel getChannel(){return in_channel;}
     
       @Override
       public void channelActive(ChannelHandlerContext ctx) {


              in_channel = ctx.channel();

              logger.trace("channelActive SmtpCommandHandler ");
              if(!queue.isEmpty()) {
                 request=queue.pollFirst();
              
                 logger.trace("request:"+request);

                 //---------------------------------------------------------------------------------------------------
                 writeRequest(request);
                 //---------------------------------------------------------------------------------------------------
             }
             

       }
       
       @Override
       public void channelRead(final ChannelHandlerContext ctx, Object msg) {

              logger.trace("channelRead object:"+msg.getClass().getName());

              //logger.trace("channel id:"+in_channel.id().asShortText()+" id:"+ctx.channel().id().asShortText());
          
              if(msg instanceof SmtpResponse) {
            	 SmtpResponse res=(SmtpResponse)msg; 
                 if(request!=null)request.add(res);
                 if(out_channel!=null){
                    out_channel.writeAndFlush(res).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                           if (future.isSuccess()) {
                               logger.trace("write response");
                               out_channel.read();
                           } else {
                               logger.trace("SmtpCommandHandler  disconnect");
                               future.channel().close();
                           }
                     }
                 }
                 );


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
                      }
                  }
              }
              );
              //logger.trace("writeRequest:"+_request+" Ok");

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

