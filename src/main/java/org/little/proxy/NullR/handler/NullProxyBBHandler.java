package org.little.proxy.NullR.handler;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NullProxyBBHandler extends ChannelInboundHandlerAdapter{
       private static final Logger logger = LoggerFactory.getLogger(NullProxyBBHandler.class);

       private Channel out_channel;
      
       public NullProxyBBHandler(Channel out_channel) {
           this.out_channel=out_channel;
       }
       
       @Override
       public void channelActive(ChannelHandlerContext ctx) throws Exception {
              super.channelActive(ctx);
              Channel _in_channel = ctx.channel();
              String _id          =_in_channel.id().asShortText();
              _in_channel.read();
              if(out_channel!=null)out_channel.read();
              logger.trace("channelActive:"+_id);
       }
       @Override
       public void channelRead(final ChannelHandlerContext ctx, Object msg) {
           //logger.trace("channelRead channel:"+ctx.channel().id().asShortText() +" -> "+out_channel.id().asShortText());
           if(out_channel.isActive()){
              //------------------------------------------------------------------------------------------------------------------
              out_channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                   @Override
                   public void operationComplete(ChannelFuture future) {
                       if (future.isSuccess()) {
                           //logger.trace("channelwrite channel:"+ctx.channel().id().asShortText() +" -> "+out_channel.id().asShortText());
                           ctx.channel().read();
                       } else {
                           future.channel().close();
                       }
                   }
               });
              //------------------------------------------------------------------------------------------------------------------
           }
       }
      


       @Override
       public void channelInactive(ChannelHandlerContext ctx) {
              Channel _in_channel = ctx.channel();
              String _id          =_in_channel.id().asShortText();
              logger.trace("channelInactive:"+_id);
              if (out_channel != null) {
                  closeOnFlush(out_channel);
                  out_channel=null;
                  logger.trace("disconnect ");
              }
       }
      
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
              Channel _in_channel = ctx.channel();
              String _id          =_in_channel.id().asShortText();
              Except ex=new Except("channel:"+_id,cause);
              logger.error(" ex:"+ex);
              closeOnFlush(_in_channel);
       }
      
       /**
        * Closes the specified channel after all queued write requests are flushed.
        */
       static void closeOnFlush(Channel ch) {
              String _id=ch.id().asShortText();
              logger.trace("closeOnFlush channel id:"+_id);
              if (ch.isActive()) {
                 ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
              }
       }

}
