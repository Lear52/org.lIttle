package org.little.smtp;

import org.little.smtp.handler.ExceptionLogger;
import org.little.smtp.handler.SessionInitiationHandler;
import org.little.smtp.handler.SmtpCommandHandler;
import org.little.smtp.handler.SmtpReplyEncoder;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * SMTP delivery system
 * https://tools.ietf.org/html/rfc5321
 *
 * @author thomas
 *
 */
public class SmtpServer implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(SmtpServer.class);

        private int            port;
        private EventLoopGroup workerGroup;
        private EventLoopGroup bossGroup;

        public SmtpServer(int _port) {
               port        = _port; 
               bossGroup   = null; 
               workerGroup = null;
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
                       logger.error("close SmtpServer", e);
               }
               finally {
                   bossGroup   = null; 
                   workerGroup = null;
               }

        }
        @Override
        public void run() {
                workerGroup        = new NioEventLoopGroup(1);
                bossGroup          = new NioEventLoopGroup();
                LoggingHandler log = new LoggingHandler(LogLevel.INFO);
                try {
                     ServerBootstrap server_boot_strap = new ServerBootstrap();
                     server_boot_strap.group(bossGroup,workerGroup);

                     server_boot_strap.channel(NioServerSocketChannel.class);

                            //.localAddress(new InetSocketAddress(port))
                     server_boot_strap.childHandler(new ChannelInitializer<SocketChannel>() {
                                     @Override
                                     protected void initChannel(SocketChannel ch) throws Exception {
                                             ch.pipeline().addLast("log"            , log);
                                             ch.pipeline().addLast("smtpOutReply"   , new SmtpReplyEncoder());
                                             ch.pipeline().addLast("smptInSession"  , new SessionInitiationHandler());
                                             ch.pipeline().addLast("smtpInLine"     , new DelimiterBasedFrameDecoder(commonSMTP.get().getDefaultCommandLen(), true, Delimiters.lineDelimiter()[0]));
                                             ch.pipeline().addLast("smptInCommand"  , new SmtpCommandHandler());
                                             ch.pipeline().addLast("exceptionLogger", new ExceptionLogger());
                                     }
                             });
                     server_boot_strap.childOption(ChannelOption.AUTO_READ, true);
                     ChannelFuture ch_ret = server_boot_strap.bind(port);
                     logger.trace("run SmtpServer");
                     //ChannelFuture f = b.register().sync();
                      ch_ret.sync().channel().closeFuture().sync();
                } 
                catch (Exception e) {
                        logger.error("run SmtpServer", e);
                } 
                finally {
                       stop();
                }
        }
        public static void main(String []args) {
                int smtpPort = 2525;
                System.setProperty("java.net.preferIPv4Stack","true");
                logger.trace("Set java property:java.net.preferIPv4Stack=true");

                SmtpServer s = new SmtpServer(smtpPort);
                logger.trace("start SmtpServer port:"+smtpPort);
                s.run();
        }
}
