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

public class NullProxyFBInitializer extends ChannelInitializer<SocketChannel> {
       private static final Logger      logger = LoggerFactory.getLogger(NullProxyFBInitializer.class);
       private NullProxyPair list_pair;
       
       public NullProxyFBInitializer(NullProxyPair list_pair) {
              logger.trace("constructor NullProxyFBInitializer Back");
              this.list_pair=list_pair;
       }

       @Override
       public void initChannel(SocketChannel channel) {

              Channel pair_channel=list_pair.getPair4Back(channel);

              logger.trace("initChannel BACK  for:"+channel.id().asShortText());
              
              if(pair_channel==null){
                 logger.trace("FB pair_channel==null for:"+channel.id().asShortText());
              return;
              }
              else {
                 logger.trace("FB channel:"+channel.id().asShortText()+" pair channel:"+pair_channel.id().asShortText());
              }
              
              NullProxyFBHandler  back_handel  = new NullProxyFBHandler(pair_channel);
              NullProxyFFHandler  front_handel = new NullProxyFFHandler(channel);
             
              ChannelPipeline     pipeline_back  = channel.pipeline();
              ChannelPipeline     pipeline_front = pair_channel.pipeline();
             
              if(commonProxy.get().getCfgServer().isDumpLog()){
                 pipeline_back.addLast ("NullLog",new LoggingHandler(LogLevel.DEBUG));
                 pipeline_front.addLast("NullLog",new LoggingHandler(LogLevel.DEBUG));
              }
             
              pipeline_back.addLast ("NullHandlerFBBack" ,back_handel);
              pipeline_front.addLast("NullHandlerFBFront",front_handel);
             
              logger.trace("initChannel create pipeline BACK");
       }

}
