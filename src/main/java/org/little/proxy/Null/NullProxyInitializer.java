package org.little.proxy.Null;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;

public class NullProxyInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger      LOG = LoggerFactory.getLogger(NullProxyInitializer.class);
       //private GlobalChannelTrafficShapingHandler countHandler;

       public NullProxyInitializer() {
              //countHandler=null;
              LOG.trace("constructor NullProxyInitializer");
       }
       //public NullProxyInitializer(GlobalChannelTrafficShapingHandler _countHandler) {
              //countHandler=_countHandler;
       //       LOG.trace("constructor NullProxyInitializer + GlobalChannelTrafficShapingHandler");
       //}
      
       @Override
       public void initChannel(SocketChannel channel) {

           NullProxyFrontendHandler    front_handel = new NullProxyFrontendHandler();
           ChannelPipeline             new_pipeline = channel.pipeline();


           //if(countHandler!=null)new_pipeline.addLast(countHandler);
           new_pipeline.addLast(front_handel);

           LOG.trace("initChannel create pipeline");
       }

}
