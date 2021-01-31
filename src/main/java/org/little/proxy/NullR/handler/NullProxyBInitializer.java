package org.little.proxy.NullR.handler;

import org.little.proxy.commonProxy;
import org.little.proxy.NullR.NullProxyBServer;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NullProxyBInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger      logger = LoggerFactory.getLogger(NullProxyBInitializer.class);
       private NullProxyBServer  server;

       public NullProxyBInitializer(NullProxyBServer server) {
              this.server=server;
              logger.trace("constructor NullProxyBFInitializer Front");
       }


       @Override
       public void initChannel(SocketChannel channel) {

           logger.trace("initChannel FRONT for:"+channel.id().asShortText());

           NullProxyBFHandler front_handel = new NullProxyBFHandler(server);
           ChannelPipeline    pipeline     = channel.pipeline();

           if(commonProxy.get().getCfgServer().isDumpLog())pipeline.addLast("NullLog",new LoggingHandler(LogLevel.DEBUG));

           pipeline.addLast("NullFrontBFHandler",front_handel);

           logger.trace("initChannel create pipeline FRONT");
       }
       /*
       public static ChannelFuture startChannel(NullProxyBServer  server){
      
              try {
                  Bootstrap     client_boot_strap1 = new Bootstrap();
                  ChannelFuture ch_ret1;
             
                  client_boot_strap1.group(server.getEventGroup());
                  client_boot_strap1.channel(NioSocketChannel.class);
                  client_boot_strap1.handler(new NullProxyBInitializer(server));
                  client_boot_strap1.option (ChannelOption.TCP_NODELAY, true);
                  client_boot_strap1.option (ChannelOption.AUTO_READ, false);
                  client_boot_strap1.option (ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);//30000
                  client_boot_strap1.option (ChannelOption.SO_KEEPALIVE, true);

                  if(true){
                     client_boot_strap1.option (ChannelOption.SO_SNDBUF, 1024 * 32768);
                     client_boot_strap1.option (ChannelOption.SO_RCVBUF, 1024 * 32768);
                  }else{
                     client_boot_strap1.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(4096));
                     client_boot_strap1.option(ChannelOption.ALLOCATOR       , PooledByteBufAllocator.DEFAULT);
                  }  

                  int    reversePort=commonProxy.get().getReversePort();
                  String reverseHost=commonProxy.get().getReverseHost();
             
                  if("*".equals(commonProxy.get().getCfgServer().getLocalClientBind())){
                       ch_ret1 = client_boot_strap1.connect(new InetSocketAddress(reverseHost, reversePort));
                  }
                  else{
                       ch_ret1 = client_boot_strap1.connect(new InetSocketAddress(reverseHost, reversePort),new InetSocketAddress(commonProxy.get().getCfgServer().getLocalClientBind(), 0));
                  }
             
                  ch_ret1.sync();
                   //
                  Channel channel1;
                  channel1 = ch_ret1.channel();
             
                  logger.trace("get server channel ("+channel1.id().asShortText()+")");
                  
                  return ch_ret1;
              } 
              catch (Exception ex) {
                     Except e=new Except("create client",ex);
                     logger.error("error ex:"+e);
              }
              return null;
      
       }
        */

}
