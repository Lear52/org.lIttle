package org.little.http;
              
import org.little.http.handler.lHttpServerInitializer;
import org.little.ssl.SSLHandlerProvider;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class lHttpServer implements Runnable {
       private static final Logger  logger = LoggerFactory.getLogger(lHttpServer.class);

       private int            port;
       private EventLoopGroup workerGroup;
       private EventLoopGroup bossGroup;
       //private lHttpRequest   req;

       public lHttpServer(int _port){
               port = _port; 
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
                 ServerBootstrap b = new ServerBootstrap();
                 b.group(bossGroup, workerGroup);
                 b.channel(NioServerSocketChannel.class);
                 b.handler(new LoggingHandler(LogLevel.INFO));

                 b.childHandler(new lHttpServerInitializer());
             
                 Channel ch = b.bind(port).sync().channel();
             
                 logger.trace("Open your web browser and navigate to " + "http://127.0.0.1:" + port + "/index.html");
             
                 ch.closeFuture().sync();
             } 
             catch (Exception e) {
                    logger.error("run httpServer", e);
             } 
             finally {
                 stop();
             }


       }

       public static void main(String[] args){
              lHttpServer server=new lHttpServer(8080);
              server.start();
              server.stop();
       }

}
