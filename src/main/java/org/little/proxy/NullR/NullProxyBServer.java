package org.little.proxy.NullR;

import java.net.InetSocketAddress;

import org.little.proxy.commonProxy;
import org.little.proxy.NullR.handler.NullProxyBInitializer;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutor;

public class NullProxyBServer extends  NullProxy_Server{

       private static final Logger logger = LoggerFactory.getLogger(NullProxyBServer.class);
      
       private   EventLoopGroup    event_group1;
       private   ChannelGroup      list_channel;
       private   int               error_connect;
       private   int               count_connect;
       private   int               count_wait;

       private   int               max_wait;

       /**
        *
        */
       public NullProxyBServer(){
              event_group1        = null;
              error_connect       = 0;
              count_connect       = 0;
              count_wait          = 0;
              max_wait            = 3;
       }
      
       public synchronized void   setWait      () {count_wait++;                                 }
       public synchronized void   setConnect   () {count_connect++;count_wait--; error_connect=0;}    
       public synchronized void   setDisconnect() {count_connect--;                              }    
       public synchronized void   setError     () {error_connect++;                              }    
       public int    getMaxWait   () {return max_wait;     }
       public int    getWait      () {return count_wait;   }
       public int    getConnect   () {return count_connect;}
       public int    getError     () {return error_connect;}
       
       public EventLoopGroup getEventGroup() {return event_group1;}

       private static ChannelFuture startChannel(NullProxyBServer  server){
      
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

                  //if(true){
                     client_boot_strap1.option (ChannelOption.SO_SNDBUF, 1024 * 32768);
                     client_boot_strap1.option (ChannelOption.SO_RCVBUF, 1024 * 32768);
                  //}
                  //else{
                  //   client_boot_strap1.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(4096));
                  //   client_boot_strap1.option(ChannelOption.ALLOCATOR       , PooledByteBufAllocator.DEFAULT);
                  //}  

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


       //public ChannelFuture reopenChannel(Channel old_channel){
       public void reopenChannel(Channel old_channel){
              ChannelFuture ch_ret;
              if(old_channel!=null)list_channel.remove(old_channel);

              if(getWait()>getMaxWait()) {
                 return ; 
                      //return null; 
              }

              try {
                  ch_ret=startChannel(this);
              } 
              catch (Exception ex) {
                     Except e=new Except("run client ",ex);
                     logger.error("error ex:"+e);
                     return ;
                     //return null;
              }
              list_channel.add(ch_ret.channel());
              logger.trace("all:"+(getWait()+getConnect())+" channel wait:"+getWait()+" connect:"+getConnect());

              //return ch_ret;
              return ;
       }

      
       @Override
       public void run(){
      
              logger.trace("begin run()");
              event_group1 = new NioEventLoopGroup();
              //list_pair    = new NullProxyPair();  
              list_channel = new DefaultChannelGroup("pull_channel", new DefaultEventExecutor()) ;
              logger.trace("reverse server:"+commonProxy.get().getReverseHost()+":"+commonProxy.get().getReversePort());
      
              try {
                  while(this.isRun()) {             
                     for(int i=0;i<getMaxWait();i++){
                         reopenChannel(null);
                     }
                     list_channel.newCloseFuture().sync();
                     logger.trace("reopen channels");
                  }  
              } 
              catch (Exception ex) {
                     Except e=new Except("run client ",ex);
                     logger.error("error ex:"+e);
              }
              finally {
                  logger.trace("finally");
                  stop();
                  logger.trace("end run()");
              }
      
       }

       @Override
       public void stop(){
              logger.trace("stop group");
              if(event_group1  !=null)event_group1.shutdownGracefully();
              event_group1  = null;
              super.stop();
       }
      
       public static void main(String[] args) {
              NullProxyBServer server=new NullProxyBServer();
              run_main(server,args); 
       }
}
