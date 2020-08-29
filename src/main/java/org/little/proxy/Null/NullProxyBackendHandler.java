package org.little.proxy.Null;

import org.little.proxy.util.commonProxy;
import org.little.proxy.util.statChannel;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NullProxyBackendHandler extends ChannelInboundHandlerAdapter {
       private static final Logger        LOG = LoggerFactory.getLogger(NullProxyBackendHandler.class);

       private Channel                    in_channel;
       private Channel                    out_channel;
       private statChannel                stat_channel;

       private NullProxyFrontendHandler   front_channel;

       public NullProxyBackendHandler(NullProxyFrontendHandler _front_channel,Channel _in_channel) {

              this.in_channel   =_in_channel;
              this.out_channel  =null;
              this.front_channel=_front_channel;
              this.stat_channel =null;

              LOG.trace("constructor NullProxyBackendHandler");

       }
       public void setOutChannel(Channel _out_channel) {  out_channel=_out_channel;}

       public statChannel getStatChannel() { return stat_channel; }

     
       @Override
       public void channelActive(ChannelHandlerContext ctx) {

              stat_channel=commonProxy.get().getChannel().create();

              stat_channel.setChannel(out_channel);
              stat_channel.setNeighbour(front_channel.getStatChannel());
              stat_channel.isFront(false);

              front_channel.getStatChannel().setChannel(in_channel);
              front_channel.getStatChannel().setNeighbour(stat_channel);
              front_channel.getStatChannel().isFront(true);

              LOG.trace("channelActive NullProxyBackendHandler id:"+getId());

              ctx.read();

       }
       
       @Override
       public void channelRead(final ChannelHandlerContext ctx, Object msg) {
           String msgStr = String.valueOf(msg);
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

           LOG.trace("NullProxyBackendHandler.channelRead  "+s);

           in_channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                                                             @Override
                                                             public void operationComplete(ChannelFuture future) {
                                                                 //LOG.trace("NullProxyBackendHandler write id:"+id_channel);
                                                                 if (future.isSuccess()) {
                                                                     LOG.trace("NullProxyBackendHandler write  id:"+getId());
                                                                     ctx.channel().read();
                                                                 } else {
                                                                     LOG.trace("NullProxyBackendHandler disconnect  id:"+getId());
                                                                     future.channel().close();
                                                                     commonProxy.get().getChannel().remove(stat_channel);stat_channel=null;
                                                                 }
                                                             }
                                                      }
           );
       }
       
       @Override
       public void channelInactive(ChannelHandlerContext ctx) {
              
              LOG.trace("NullProxyBackendHandler.channelInactive  id:"+getId());
              NullProxyFrontendHandler.closeOnFlush(in_channel);
              commonProxy.get().getChannel().remove(stat_channel);stat_channel=null;
       }
     
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                   Except _e=new Except("NullProxyBackendHandler.exception",cause);
              LOG.error("NullProxyBackendHandler.exception id:"+getId()+" ex:"+_e);
              //cause.printStackTrace();
              NullProxyFrontendHandler.closeOnFlush(ctx.channel());
              commonProxy.get().getChannel().remove(stat_channel);stat_channel=null;
       }

       public String getId() {if(stat_channel!=null)return stat_channel.getId();return "";}


}
