package org.little.proxy.NullR;

//import java.util.Arrays;

//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.DefaultParser;
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.ParseException;
//import org.apache.commons.lang3.StringUtils;
import org.little.proxy.commonProxy;
import org.little.proxy.NullR.handler.NullProxyFFInitializer;
import org.little.proxy.NullR.handler.NullProxyFBInitializer;
import org.little.proxy.NullR.handler.NullProxyLogHandler;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

public class NullProxyFServer extends  NullProxy_Server{

    private static final Logger logger = LoggerFactory.getLogger(NullProxyFServer.class);

    private   EventLoopGroup                     parent_event_group1;
    private   EventLoopGroup                     child_event_group1;
    private   ServerBootstrap                    server_boot_strap1;
    private   EventLoopGroup                     parent_event_group2;
    private   EventLoopGroup                     child_event_group2;
    private   ServerBootstrap                    server_boot_strap2;
    private   NullProxyPair                      list_pair;

    /**
     *
     */
    public NullProxyFServer(){
           parent_event_group1 = null;
           child_event_group1  = null;
           server_boot_strap1  = null;
           parent_event_group2 = null;
           child_event_group2  = null;
           server_boot_strap2  = null;
           list_pair           = null;           
    }

    @Override
    public void run(){

        logger.trace("begin run()");

        try {
            NullProxyFFInitializer  init_class1;
            NullProxyFBInitializer  init_class2;
            LoggingHandler          log;
            log                    = new NullProxyLogHandler();
            
            list_pair              = new NullProxyPair();
            
            server_boot_strap1     = new ServerBootstrap();
            parent_event_group1    = new NioEventLoopGroup(1);
            child_event_group1     = new NioEventLoopGroup();
            init_class1            = new NullProxyFFInitializer(list_pair);

            server_boot_strap2     = new ServerBootstrap();
            parent_event_group2    = new NioEventLoopGroup(1);
            child_event_group2     = new NioEventLoopGroup();
            init_class2            = new NullProxyFBInitializer(list_pair);
            
            logger.trace("create ServerBootstrap");
            server_boot_strap1.group(parent_event_group1, child_event_group1);
            server_boot_strap2.group(parent_event_group2, child_event_group2);
            logger.trace("register parent_event_group, child_event_group");
            // 
            server_boot_strap1.channel(NioServerSocketChannel.class);
            server_boot_strap1.handler(log);
            server_boot_strap1.childHandler(init_class1);
            server_boot_strap1.childOption(ChannelOption.AUTO_READ, false);
            server_boot_strap1.childOption(ChannelOption.SO_SNDBUF, 1024 * 32768);
            server_boot_strap1.childOption(ChannelOption.SO_RCVBUF, 1024 * 32768);
            server_boot_strap1.childOption(ChannelOption.SO_KEEPALIVE, true);

            server_boot_strap2.channel(NioServerSocketChannel.class);
            server_boot_strap2.handler(log);
            server_boot_strap2.childHandler(init_class2);
            server_boot_strap2.childOption(ChannelOption.AUTO_READ, false);
            server_boot_strap2.childOption(ChannelOption.SO_SNDBUF, 1024 * 32768);
            server_boot_strap2.childOption(ChannelOption.SO_RCVBUF, 1024 * 32768);
            server_boot_strap2.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture ch_ret1;
            ChannelFuture ch_ret2;

            int localPort=commonProxy.get().getCfgServer().getPort();
            int reversePort=commonProxy.get().getReversePort();

            if("*".equals(commonProxy.get().getCfgServer().getLocalServerBind())){
               ch_ret1 = server_boot_strap1.bind(localPort);
            }
            else{
               ch_ret1 = server_boot_strap1.bind(commonProxy.get().getCfgServer().getLocalServerBind(),localPort);
            }

            if("*".equals(commonProxy.get().getReverseHost())){
               ch_ret2 = server_boot_strap2.bind(reversePort);
            }
            else{
               ch_ret2 = server_boot_strap2.bind(commonProxy.get().getReverseHost(),reversePort);
            }
            logger.trace("bind local ports");
            
            ch_ret1.sync();
            ch_ret2.sync();
            logger.trace("server bind ok");
             //
            Channel channel1;
            Channel channel2;
            channel1 = ch_ret1.channel();
            channel2 = ch_ret2.channel();
            logger.trace("get server channel ("+channel1.id().asShortText()+" <> "+channel2.id().asShortText()+") and wait close");

            
            ch_ret1 = channel1.closeFuture().sync();
            ch_ret2 = channel2.closeFuture().sync();
            logger.trace("server channel close");
            
        } 
        catch (Exception ex) {
               Except e=new Except(" ",ex);
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
           if(parent_event_group1 !=null)parent_event_group1.shutdownGracefully();
           if(child_event_group1  !=null)child_event_group1.shutdownGracefully();
           if(parent_event_group2 !=null)parent_event_group2.shutdownGracefully();
           if(child_event_group2  !=null)child_event_group2.shutdownGracefully();
           parent_event_group1 = null;
           child_event_group1  = null;
           parent_event_group2 = null;
           child_event_group2  = null;
           server_boot_strap1  = null;
           server_boot_strap2  = null;
           super.stop();
    }
    public static void main(String[] args) {
        NullProxyFServer server=new NullProxyFServer();
    	
    	run_main(server,args);
    }
}
