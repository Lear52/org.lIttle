package org.little.proxy.NullR;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.little.proxy.commonProxy;
import org.little.proxy.NullR.handler.NullProxyInitializerFront;
import org.little.proxy.NullR.handler.NullProxyInitializerBack;
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

public class NullProxyServer implements  Runnable{

    private static final Logger logger = LoggerFactory.getLogger(NullProxyServer.class);

    private   EventLoopGroup                     parent_event_group1;
    private   EventLoopGroup                     child_event_group1;
    private   ServerBootstrap                    server_boot_strap1;
    private   EventLoopGroup                     parent_event_group2;
    private   EventLoopGroup                     child_event_group2;
    private   ServerBootstrap                    server_boot_strap2;
    protected Thread                             process_tread;
    private   NullProxyPair                      list_pair;


    /**
     *
     */
    public NullProxyServer(){
           parent_event_group1 = null;
           child_event_group1  = null;
           server_boot_strap1  = null;
           parent_event_group2 = null;
           child_event_group2  = null;
           server_boot_strap2  = null;
           process_tread       = null;
           list_pair           = null;           
    }

    public void init(){}

    public void run(){

        logger.trace("begin run()");

        try {
            NullProxyInitializerFront init_class1;
            NullProxyInitializerBack  init_class2;
            LoggingHandler       log;
            log                    = new NullProxyLogHandler();
            
            list_pair              = new NullProxyPair();
            
            server_boot_strap1     = new ServerBootstrap();
            parent_event_group1    = new NioEventLoopGroup(1);
            child_event_group1     = new NioEventLoopGroup();
            init_class1            = new NullProxyInitializerFront(list_pair);

            server_boot_strap2     = new ServerBootstrap();
            parent_event_group2    = new NioEventLoopGroup(1);
            child_event_group2     = new NioEventLoopGroup();
            init_class2            = new NullProxyInitializerBack(list_pair);
            
            
            logger.trace("create ServerBootstrap");
            server_boot_strap1.group(parent_event_group1, child_event_group1);
            server_boot_strap2.group(parent_event_group2, child_event_group2);
            logger.trace("register parent_event_group, child_event_group");
            // 
            server_boot_strap1.channel(NioServerSocketChannel.class);
            server_boot_strap1.handler(log);
            server_boot_strap1.childHandler(init_class1);
            server_boot_strap1.childOption(ChannelOption.AUTO_READ, true);

            server_boot_strap2.channel(NioServerSocketChannel.class);
            server_boot_strap2.handler(log);
            server_boot_strap2.childHandler(init_class2);
            server_boot_strap2.childOption(ChannelOption.AUTO_READ, true);

            ChannelFuture ch_ret1;
            ChannelFuture ch_ret2;

            int localPort=commonProxy.get().getCfgServer().getPort();

            ch_ret1 = server_boot_strap1.bind(localPort);
            ch_ret2 = server_boot_strap2.bind(localPort+1);

            logger.trace("bind local ports");
            
            /*
            ch_ret1.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future1) {
                    if (!future1.isSuccess()) {
                        LOG.trace("1 future1.NotSuccess() channel:"+future1.channel().id().asShortText());
                    }
                    else {
                        LOG.trace("1 future1.isSuccess() channel:"+future1.channel().id().asShortText());
                        server_channel1=future1.channel();
                    }
                }
            });
            ch_ret2.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future1) {
                    if (!future1.isSuccess()) {
                        LOG.trace("2 future1.NotSuccess() channel:"+future1.channel().id().asShortText());
                    }
                    else {
                        LOG.trace("2 future1.isSuccess() channel:"+future1.channel().id().asShortText());
                        server_channel2=future1.channel();
                    }
                }
            });
            */

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

    public boolean isRun(){
           if(process_tread==null)return false;
           return true;
    }

    public void start(){
           logger.trace("null server start");
           if(process_tread==null){
              logger.trace("create new tread");
              process_tread=new Thread(this,"wrapperMainProxy");
              process_tread.start();
              logger.trace("start new tread");
           }
    }

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
           process_tread      = null;
    }

    public static void main(String[] args) {

           System.setProperty("java.net.preferIPv4Stack","true");
           logger.trace("Set java property:java.net.preferIPv4Stack=true");

           NullProxyServer server=new NullProxyServer();

           if(!NullProxyServer.opt(server,args)){
              logger.error("unknow args");
              return;
           }
          
           logger.trace("START wrapper");
          
           
           server.start();

           while(server.isRun()){
               synchronized(server){
                   //if(!m_mainComplete)break;
                   server.notifyAll();
               }
               try{Thread.sleep(1000L); } catch (InterruptedException e){logger.trace("ex:" + e);}
           }
           
           server.stop();
           logger.trace("EXIT wrapper");
    }
    public static boolean opt(NullProxyServer server,String[] args) {
           String            OPTION_HELP  = "help";
           String            OPTION_CFG   = "cfg";
           String            xpath ;
           Options           options;
           CommandLineParser parser;
           CommandLine       cmd;
          
           options = new Options();
           parser  = new DefaultParser();
           
           options.addOption(null, OPTION_CFG   , true, "Run with cfg");
           options.addOption(null, OPTION_HELP  , false,"Display command line help.");
           {  StringBuilder buf=new StringBuilder();
              buf.append(NullProxyServer.class).append(" ");
              for(int i=0;i<args.length;i++)buf.append("\"").append(args[i]).append("\" ");
              logger.info("run: " + buf.toString());
           }

           try {
               cmd = parser.parse(options, args);
           } catch (final ParseException e) {
               logger.error("Could not parse command line: " + Arrays.asList(args), e);
               printHelp(options,"Could not parse command line: " + Arrays.asList(args));
               return false;
           }
          
           if (cmd.hasOption(OPTION_HELP)) {
               printHelp(options, null);
               return false;
           }
           if (cmd.hasOption(OPTION_CFG)) {
               xpath = cmd.getOptionValue(OPTION_CFG);
           } else {
               xpath = "littleproxy.xml";
           }

           logger.info("config file:"+xpath);
           
           if(commonProxy.get().loadCFG(xpath)==false){
              logger.error("error read config file:"+xpath);
              return false;
           }

           commonProxy.get().init();
           commonProxy.get().initMBean();

           server.init();

           System.out.println("reverse tunnelig bind:" + commonProxy.get().getCfgServer().getPort() + " <> " + (commonProxy.get().getCfgServer().getPort()+1));

           return true;
    }


    private static void printHelp(final Options options,final String errorMessage) {
        if (!StringUtils.isBlank(errorMessage)) {
            logger.error(errorMessage);
            System.err.println(errorMessage);
        }

        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("littleproxy", options);
    }
}
