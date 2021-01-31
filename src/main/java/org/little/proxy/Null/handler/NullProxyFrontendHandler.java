package org.little.proxy.Null.handler;

import java.net.InetSocketAddress;

import org.little.proxy.commonProxy;
import org.little.proxy.util.statChannel;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;

public class NullProxyFrontendHandler extends ChannelInboundHandlerAdapter {
       private static final Logger      LOG = LoggerFactory.getLogger(NullProxyFrontendHandler.class);

       private Channel      out_channel;
       private statChannel  stat_channel;
      
       public NullProxyFrontendHandler() {
           stat_channel=null;
           out_channel=null;
       }
       
       public Channel     getChannel() { return out_channel; }

       public statChannel getStatChannel() { return stat_channel; }
      
       @Override
       public void channelActive(ChannelHandlerContext ctx) {
              String remoteHost;
              int    remotePort;

              remoteHost  =commonProxy.get().getHosts().getDefaultHost();
              remotePort  =commonProxy.get().getHosts().getDefaultPort();

            
              Channel                 in_channel;
              NullProxyBackendHandler back_handler;
              Bootstrap               boot_strap;
              ChannelFuture           ch_state;
            
              in_channel      = ctx.channel();

              boot_strap      = new Bootstrap();
              back_handler    = new NullProxyBackendHandler(this,in_channel);
              EventLoop in_el = in_channel.eventLoop();
            
              boot_strap.group  (in_el);
              boot_strap.channel(ctx.channel().getClass());
              boot_strap.handler(back_handler);
              boot_strap.option (ChannelOption.AUTO_READ, false);
              boot_strap.option (ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);

              stat_channel=commonProxy.get().getChannel().create(in_channel);
              //stat_channel.setChannel(in_channel);      
              stat_channel.isFront(true);               
            
              //-------------------------------------------------------------------------------------------------------------------
              if("*".equals(commonProxy.get().getCfgServer().getLocalClientBind())){
                 ch_state = boot_strap.connect(new InetSocketAddress(remoteHost, remotePort));
              }
              else{
                 ch_state = boot_strap.connect(new InetSocketAddress(remoteHost, remotePort),new InetSocketAddress(commonProxy.get().getCfgServer().getLocalClientBind(), 0));
              }
              //-------------------------------------------------------------------------------------------------------------------
              
              out_channel = ch_state.channel();
            
              back_handler.setOutChannel(out_channel);
              //-------------------------------------------------------------------------------------------------------------------
              ch_state.addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
                                    if (future.isSuccess()) {
                                        in_channel.read();
                                        LOG.trace("connection complete start to read first data for channel id:"+getId());
                                    } 
                                    else {
                                        in_channel.close();
                                        LOG.trace("Close the connection id:"+getId());
                                        commonProxy.get().getChannel().remove(stat_channel);stat_channel=null;
                                    }
                                }
                            }
              );
              //-------------------------------------------------------------------------------------------------------------------

       }
      
       @Override
       public void channelRead(final ChannelHandlerContext ctx, Object msg) {
           String msgStr = String.valueOf(msg);//io.netty.buffer.PooledUnsafeDirectByteBuf
           int iw=0;
           int ir=0;
           int ic=0;

           if(msg instanceof io.netty.buffer.AbstractByteBuf ) {
              iw=((io.netty.buffer.AbstractByteBuf)msg).writerIndex();
              ir=((io.netty.buffer.AbstractByteBuf)msg).readerIndex();
              ic=((io.netty.buffer.AbstractByteBuf)msg).capacity();
           }
           stat_channel.addIn(iw);
           String s=stat_channel.toString()+" class:"+msg.getClass().getName()+" iw:"+iw+" ir:"+ir+" ic:"+ic+" msg:"+msgStr;
           LOG.trace("NullProxyFrontendHandler.channelRead "+s);

           if(out_channel.isActive()){
              //------------------------------------------------------------------------------------------------------------------
              out_channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                   @Override
                   public void operationComplete(ChannelFuture future) {
                       if (future.isSuccess()) {
                           // was able to flush out data, start to read the next chunk
                           //LOG.trace("NullProxyFrontendHandler write ok id:"+id_channel);
                           ctx.channel().read();
                       } else {
                           future.channel().close();
                           commonProxy.get().getChannel().remove(stat_channel);stat_channel=null;
                           //LOG.trace("NullProxyFrontendHandler disconnect id:"+id_channel);
                       }
                   }
               });
              //------------------------------------------------------------------------------------------------------------------
           }
       }
      


       @Override
       public void channelInactive(ChannelHandlerContext ctx) {
           LOG.trace("channelInactive NullProxyFrontendHandler id:"+getId());
           if (out_channel != null) {
               closeOnFlush(out_channel);
               out_channel=null;
               LOG.trace("disconnect id:"+getId());
           }
           commonProxy.get().getChannel().remove(stat_channel);stat_channel=null;
       }
      
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
           LOG.trace("exception NullProxyFrontendHandler id:"+getId()+" ex:"+cause);
           commonProxy.get().getChannel().remove(stat_channel);stat_channel=null;
           closeOnFlush(ctx.channel());
       }
      
       /**
        * Closes the specified channel after all queued write requests are flushed.
        */
       static void closeOnFlush(Channel ch) {
           String _id=ch.id().asShortText();
           LOG.trace("closeOnFlush NullProxyFrontendHandle id:"+_id);
           if (ch.isActive()) {
               ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
           }
           commonProxy.get().getChannel().remove(_id);
       }

       public String getId() {if(stat_channel!=null)return stat_channel.getId();return "";}
}
