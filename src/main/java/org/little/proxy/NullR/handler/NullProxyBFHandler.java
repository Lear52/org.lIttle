package org.little.proxy.NullR.handler;
       
import java.net.InetSocketAddress;

import org.little.proxy.commonProxy;
import org.little.proxy.NullR.NullProxyBServer;
import org.little.util.Except;
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
import io.netty.channel.socket.nio.NioSocketChannel;

public class NullProxyBFHandler extends ChannelInboundHandlerAdapter {
       private static final Logger logger = LoggerFactory.getLogger(NullProxyBFHandler.class);

       private Channel          out_channel;
       private Channel          in_channel;
       private NullProxyBServer server;
      
       public NullProxyBFHandler(NullProxyBServer  server) {
              this.out_channel=null;
              this.in_channel=null;
              this.server=server;
       }

       @Override
       public void channelActive(ChannelHandlerContext ctx) throws Exception {
              super.channelActive(ctx);
              Channel _in_channel = ctx.channel();
              String _id          =_in_channel.id().asShortText();
              _in_channel.read();
              if(out_channel!=null)out_channel.read();
              logger.trace("channelActive:"+_id);
              server.setWait();
       }

       private void channelNewPair(Channel _in_channel, Object msg) {
               if(in_channel!=null)return;
               Bootstrap              client_boot_strap2;
               ChannelFuture          ch_state;

               in_channel=_in_channel;

               //logger.trace("create new pair connection for channel:"+in_channel.id().asShortText());

               String                 remote_host=commonProxy.get().getHosts().getDefaultHost();
               int                    remote_port=commonProxy.get().getHosts().getDefaultPort();
               client_boot_strap2     = new Bootstrap();
               client_boot_strap2.group(in_channel.eventLoop());
               // 
               client_boot_strap2.channel(NioSocketChannel.class);
               client_boot_strap2.handler(new NullProxyBBHandler(in_channel));
               client_boot_strap2.option (ChannelOption.TCP_NODELAY, true);
               client_boot_strap2.option (ChannelOption.AUTO_READ, false);
               client_boot_strap2.option (ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);
               client_boot_strap2.option (ChannelOption.SO_SNDBUF, 1024 * 32768);
               client_boot_strap2.option (ChannelOption.SO_RCVBUF, 1024 * 32768);
               client_boot_strap2.option (ChannelOption.SO_KEEPALIVE, true);
             
               if("*".equals(commonProxy.get().getCfgServer().getLocalClientBind())){
                  ch_state = client_boot_strap2.connect(new InetSocketAddress(remote_host, remote_port));
               }
               else{
                  ch_state = client_boot_strap2.connect(new InetSocketAddress(remote_host, remote_port),new InetSocketAddress(commonProxy.get().getCfgServer().getLocalClientBind(), 0));
               }
               ch_state.addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
                                    logger.trace("ChannelFuture:"+future.isSuccess());
                                    if (future.isSuccess()) {
                                        out_channel=future.channel();
                                        logger.trace("connection complete start connection for channel(out):"+out_channel.id().asShortText() +" pair chanel(in):"+in_channel.id().asShortText());
                                        channelRead(msg);
                                        server.setConnect();
                                        server.reopenChannel(null);
                                        //in_channel.read();/**/
                                    } 
                                    //else {
                                    //    in_channel.close();
                                    //    logger.trace("Close the connection ");
                                    //}
                                }
                            }
               );
               //logger.trace("create new pair connection for channel:"+in_channel.id().asShortText()+ " Ok");

       }
       private void channelRead(Object msg) {
               logger.trace("channelRead channel:"+in_channel.id().asShortText() +" -> "+out_channel.id().asShortText());
               if(out_channel.isActive()){
                  //------------------------------------------------------------------------------------------------------------------
                  out_channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                       @Override
                       public void operationComplete(ChannelFuture future) {
                              if (future.isSuccess()) {
                                  logger.trace("channelwrite channel:"+in_channel.id().asShortText() +" -> "+out_channel.id().asShortText());
                                  in_channel.read();
                           } 
                           else {
                               future.channel().close();
                           }
                       }
                   });
                  //------------------------------------------------------------------------------------------------------------------
               }

       }
       
       @Override
       public void channelRead(final ChannelHandlerContext ctx, Object msg) {

              if(out_channel==null){
                 channelNewPair(ctx.channel(),msg);
              }
              else{
                 channelRead(msg); 
              }
       }

       @Override
       public void channelInactive(ChannelHandlerContext ctx) throws Exception {
              Channel _in_channel = ctx.channel();
              String _id          =_in_channel.id().asShortText();

              logger.trace("channelInactive:"+_id);

              if(out_channel != null) {
                 closeOnFlush(out_channel);
                 out_channel=null;
                 //logger.trace("disconnect");
              }
              //server.getListPair().delFront(_in_channel);
              //super.channelInactive(ctx);
              server.setDisconnect();

              server.reopenChannel(_in_channel);
              
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
              if(ch.isActive()){
                  ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
              }
       }

}
