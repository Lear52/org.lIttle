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

public class NullProxyFFInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger      logger = LoggerFactory.getLogger(NullProxyFFInitializer.class);
       private NullProxyPair list_pair;
       
       public NullProxyFFInitializer(NullProxyPair list_pair) {
              logger.trace("constructor NullProxyFFInitializer Front");
              this.list_pair=list_pair;
       }


       @Override
       public void initChannel(SocketChannel channel) {

              logger.trace("initChannel FRONT for:"+channel.id().asShortText());

              Channel pair_channel=list_pair.getPair4Front(channel);

              if(pair_channel==null){
                 logger.trace("FF pair_channel==null for:"+channel.id().asShortText());
                 return;
              }
              else {
                 logger.trace("FF channel:"+channel.id().asShortText()+" pair channel:"+pair_channel.id().asShortText());
              }

              NullProxyFFHandler  front_handel = new NullProxyFFHandler(pair_channel);
              NullProxyFBHandler  back_handel  = new NullProxyFBHandler(channel);
             
              ChannelPipeline     pipeline_front = channel.pipeline();
              ChannelPipeline     pipeline_back  = pair_channel.pipeline();
             
              if(commonProxy.get().getCfgServer().isDumpLog()){
                 pipeline_back.addLast ("NullLog",new LoggingHandler(LogLevel.DEBUG));
                 pipeline_front.addLast("NullLog",new LoggingHandler(LogLevel.DEBUG));
              }
             
              pipeline_back.addLast ("NullHandlerFFBack" ,back_handel);
              pipeline_front.addLast("NullHandlerFFFront",front_handel);
             
              logger.trace("initChannel create pipeline FRONT");
       }

}

