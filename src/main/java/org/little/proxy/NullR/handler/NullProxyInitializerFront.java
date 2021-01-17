package org.little.proxy.NullR.handler;

import org.little.proxy.commonProxy;
import org.little.proxy.NullR.NullProxyPair;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NullProxyInitializerFront extends ChannelInitializer<SocketChannel> {
       private static final Logger      logger = LoggerFactory.getLogger(NullProxyInitializerFront.class);
       private NullProxyPair list_pair;
       
       public NullProxyInitializerFront(NullProxyPair list_pair) {
              logger.trace("constructor NullProxyInitializer Front");
              this.list_pair=list_pair;
       }


       @Override
       public void initChannel(SocketChannel channel) {

    	   logger.trace("initChannel FRONT for:"+channel.id().asShortText());

    	   Channel pair_channel=list_pair.getPair4Front(channel);
    	   if(pair_channel==null)return;
    	   
           NullProxyHandler    front_handel = new NullProxyHandler(pair_channel);
           NullProxyHandler    back_handel = new NullProxyHandler(channel);

           ChannelPipeline     pipeline_front = channel.pipeline();
           ChannelPipeline     pipeline_back = pair_channel.pipeline();

           if(commonProxy.get().getCfgServer().isDumpLog())pipeline_back.addLast("NullLog",new LoggingHandler(LogLevel.DEBUG));
           if(commonProxy.get().getCfgServer().isDumpLog())pipeline_front.addLast("NullLog",new LoggingHandler(LogLevel.DEBUG));

           pipeline_back.addLast("NullHandlerBack",back_handel);
           pipeline_front.addLast("NullHandlerFront",front_handel);

           logger.trace("initChannel create pipeline FRONT");
       }

}
