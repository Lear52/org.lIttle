package org.little.imap;

import org.little.imap.handler.ImapServerlInitializer;
//import org.little.ssl.SSLHandlerProvider0;
import org.little.ssl.SSLHandlerProvider;
import org.little.util.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class ImapServer  implements Runnable {
       private static final Logger logger = Logger.getLogger(ImapServer.class);
       private int port;
       private EventLoopGroup workerGroup;
       private EventLoopGroup bossGroup;


       public ImapServer(int port) {
              this.port = port;
       }
       public int start() {
               new Thread(this).start();
               return 0;
       }
        
       public void stop() {
               try {
                       if(workerGroup!=null)workerGroup.shutdownGracefully().sync();
                       if(bossGroup  !=null)bossGroup  .shutdownGracefully().sync();
               } catch (InterruptedException e) {
                       logger.error("close HTTPServer", e);
               }
               finally {
                 bossGroup = null; 
                 workerGroup = null;
               }
       
       }

       @Override
       public void run(){
              //if(commonIMAP.get().getCfgSSL().isSSL()){
              //   SSLHandlerProvider0.initSSLContext();
              //}

              SSLHandlerProvider.initSSLContext(commonIMAP.get().getCfgSSL());

              bossGroup   = new NioEventLoopGroup(1);
              workerGroup = new NioEventLoopGroup();
              try {
                   ServerBootstrap server_boot_strap = new ServerBootstrap();
                   server_boot_strap.group       (bossGroup, workerGroup);
                   server_boot_strap.channel     (NioServerSocketChannel.class);
                   server_boot_strap.handler     (new LoggingHandler(LogLevel.DEBUG));
                   server_boot_strap.childHandler(new ImapServerlInitializer());
                   server_boot_strap.childOption (ChannelOption.AUTO_READ, true);

                   logger.trace("run server port:"+port);

                   server_boot_strap.bind(port).sync().channel().closeFuture().sync();
              } 
              catch (Exception e) {
                  logger.error("run server", e);
              } 
              finally {
                    stop();
              }
       }

       public static void main(String[] args) {
              System.setProperty("java.net.preferIPv4Stack","true");
              logger.trace("Set java property:java.net.preferIPv4Stack=true");
              ImapServer s=new ImapServer(1143);
              s.run();
       }
}
