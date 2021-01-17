package org.little.http;
              
import org.little.http.handler.lHttpServerInitializer;
import org.little.ssl.SSLHandlerProvider;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class lHttpServer implements Runnable {
       private static final Logger  logger = LoggerFactory.getLogger(lHttpServer.class);

       private EventLoopGroup workerGroup;
       private EventLoopGroup bossGroup;

       public lHttpServer(){
               bossGroup = null; 
               workerGroup = null;
               logger.trace("HttpX509Server.constructor");
       }
       public int start() {
              logger.trace("HttpX509Server.start()");
              new Thread(this).start();
              return 0;
       }
       
       public void stop() {
              try {
                   if(workerGroup!=null)workerGroup.shutdownGracefully().sync();
                   if(bossGroup  !=null)bossGroup  .shutdownGracefully().sync();
              } catch (InterruptedException e) {
                      logger.error("close httpServer", e);
              }
              finally {
                bossGroup = null; 
                workerGroup = null;
              }
              logger.trace("HttpX509Server.stop()");

       }
       @Override
       public void run() {
              logger.trace("HttpX509Server.run()");
              
              if(commonHTTP.get().getCfgSSL().isSSL()){
                 SSLHandlerProvider.initSSLContext(commonHTTP.get().getCfgSSL());
              }

              bossGroup   = new NioEventLoopGroup(1);
              workerGroup = new NioEventLoopGroup();

              try {
                 ServerBootstrap server_boot_strap = new ServerBootstrap();

                 server_boot_strap.group(bossGroup, workerGroup);
                 server_boot_strap.channel(NioServerSocketChannel.class);
                 server_boot_strap.handler(new LoggingHandler(LogLevel.INFO));

                 server_boot_strap.childHandler(new lHttpServerInitializer());
             
                 server_boot_strap.option(ChannelOption.SO_REUSEADDR, true);
                 server_boot_strap.option(ChannelOption.SO_SNDBUF, 262144); 
                 server_boot_strap.option(ChannelOption.SO_RCVBUF, 262144); 
                 server_boot_strap.option(ChannelOption.SO_BACKLOG, 200); 
                 server_boot_strap.option(ChannelOption.SO_TIMEOUT, 10000); 
                 server_boot_strap.childOption(ChannelOption.SO_KEEPALIVE, true);

                 ChannelFuture ch_ret;
                 if("*".equals(commonHTTP.get().getCfgServer().getLocalServerBind())) ch_ret = server_boot_strap.bind(commonHTTP.get().getCfgServer().getPort());
                 else                                                                 ch_ret = server_boot_strap.bind(commonHTTP.get().getCfgServer().getLocalServerBind(),commonHTTP.get().getCfgServer().getPort());

                 logger.trace("Open your web browser and navigate to " + "http://127.0.0.1:" + commonHTTP.get().getCfgServer().getPort() + "/index.html");
                 ch_ret=ch_ret.sync();
                 ch_ret.channel().closeFuture().sync();
             
              } 
              catch (Exception e) {
                    logger.error("run httpServer", e);
              } 
              finally {
                 stop();
              }


       }

       public static void main(String[] args){
              lHttpServer server=new lHttpServer();
              server.start();
              server.stop();
       }

}
