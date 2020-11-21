package org.little.proxy.Null;

import org.little.proxy.util.commonProxy;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NullProxyInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger      LOG = LoggerFactory.getLogger(NullProxyInitializer.class);

       public NullProxyInitializer() {
              LOG.trace("constructor NullProxyInitializer");
       }
      
       @Override
       public void initChannel(SocketChannel channel) {

           NullProxyFrontendHandler    front_handel = new NullProxyFrontendHandler();
           ChannelPipeline             pipeline = channel.pipeline();

           if(commonProxy.get().isDumpLog())pipeline.addLast("NullLog",new LoggingHandler(LogLevel.DEBUG));
           pipeline.addLast("NullFrontHandler",front_handel);

           LOG.trace("initChannel create pipeline");
       }

}
