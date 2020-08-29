package org.little.proxy.Null;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import org.little.proxy.util.commonProxy;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.logging.LoggingHandler;
//import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;

public class NullProxyMain implements  Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(NullProxyMain.class);

    private   EventLoopGroup                     parent_event_group;
    private   EventLoopGroup                     child_event_group;
    private   EventLoopGroup                     sharp_event_group;
    private   ServerBootstrap                    server_boot_strap;
    //private   GlobalChannelTrafficShapingHandler countHandler;
    protected Thread                             process_tread;


    /**
     *
     */
    public NullProxyMain(){
           parent_event_group = null;
           child_event_group  = null;
           server_boot_strap  = null;
           process_tread      = null;
    }

    public void init(){}

    public void run(){

        LOG.trace("begin run()");

        try {
            server_boot_strap      = new ServerBootstrap();
            parent_event_group     = new NioEventLoopGroup(commonProxy.get().getNumberThreads()); //specified_number_of_threads = 4;//1
            child_event_group      = new NioEventLoopGroup();
            /*
            sharp_event_group      = new NioEventLoopGroup();

            long TRAFFIC_SHAPING_CHECK_INTERVAL_MS = 250L;
            long writeThrottleBytesPerSecond       = 1000000000;
            long readThrottleBytesPerSecond        = 1000000000; 

            countHandler           = new GlobalChannelTrafficShapingHandler(sharp_event_group,
                                         writeThrottleBytesPerSecond,readThrottleBytesPerSecond,
                                         TRAFFIC_SHAPING_CHECK_INTERVAL_MS,
                                         Long.MAX_VALUE
                                         );

            NullProxyInitializer child_channel               = new NullProxyInitializer(countHandler);
            */
            NullProxyInitializer child_channel               = new NullProxyInitializer();
            LoggingHandler       log                         = new NullProxyLog();

            
            LOG.trace("create ServerBootstrap");
            server_boot_strap=server_boot_strap.group(parent_event_group, child_event_group);
            LOG.trace("register parent_event_group, child_event_group");
            // 
            server_boot_strap=server_boot_strap.channel(NullProxyServerChannel.class);
            // 
            server_boot_strap=server_boot_strap.handler(log);
            // 
            server_boot_strap=server_boot_strap.childHandler(child_channel);
            server_boot_strap=server_boot_strap.childOption(ChannelOption.AUTO_READ, false);

            ChannelFuture ch_ret;

            int localPort=commonProxy.get().getPort();

            if("*".equals(commonProxy.get().getLocalServerBind())) ch_ret = server_boot_strap.bind(localPort);
            else ch_ret = server_boot_strap.bind(commonProxy.get().getLocalServerBind(),localPort);

            LOG.trace("bind local port");
            // ch_ret - The result of an asynchronous Channel I/O operation. 
            //Waits for this future until it is done, and rethrows the cause of the failure if this future failed.
            //
            ChannelFuture ch_ret1 = ch_ret.sync();
            LOG.trace("server future sync 01");
      
             //
            Channel channel;
            channel = ch_ret1.channel();
            LOG.trace("ch_ret1.channel()");

            ChannelFuture ch_ret2;
            ch_ret2 = channel.closeFuture();
            LOG.trace("channel.closeFuture()");
            ch_ret2.sync();
            LOG.trace("channel future sync 02");
            
        } 
        catch (Exception ex) {
               Except e=new Except(" ",ex);
               LOG.error("error ex:"+e);
        }
        finally {
            LOG.trace("finally");
            stop();
            LOG.trace("end run()");
        }

    }

    public boolean isRun(){
           if(process_tread==null)return false;
           return true;
    }

    public void start(){
           LOG.trace("null server start");
           if(process_tread==null){
              LOG.trace("create new tread");
              process_tread=new Thread(this,"wrapperMainProxy");
              process_tread.start();
              LOG.trace("start new tread");
           }
    }

    public void stop(){
           LOG.trace("stop group");
           if(parent_event_group !=null)parent_event_group.shutdownGracefully();
           if(child_event_group  !=null)child_event_group.shutdownGracefully();
           if(sharp_event_group  !=null)sharp_event_group.shutdownGracefully();
           parent_event_group = null;
           sharp_event_group  = null;
           child_event_group  = null;
           server_boot_strap  = null;
           process_tread      = null;
    }
    /*
    public long queuesSize() {
        if(countHandler!=null)return countHandler.queuesSize();
        else return 0;
    }
    public String toString() {
        if(countHandler!=null)return 
               "queuesSize:"+countHandler.queuesSize()+" "
              +countHandler.trafficCounter().toString()
              ;
        else return "";
    }
    */
    public static void main(String[] args) {

           System.setProperty("java.net.preferIPv4Stack","true");
           LOG.trace("Set java property:java.net.preferIPv4Stack=true");

           NullProxyMain server=new NullProxyMain();

           if(!NullProxyMain.opt(server,args)){
              LOG.error("check args");
              return;
           }
          
           LOG.trace("START wrapper");
          
           
           server.start();

           while(server.isRun()){
               synchronized(server){
                   //if(!m_mainComplete)break;
                   server.notifyAll();
               }
               try{Thread.sleep(1000L); } catch (InterruptedException e){LOG.trace("ex:" + e);}
           }
           
           server.stop();
           LOG.trace("EXIT wrapper");
    }
    public static boolean opt(NullProxyMain server,String[] args) {
           
           //String            OPTION_PORT  = "port";
           //String            OPTION_RPORT = "rport";
           String            OPTION_HELP  = "help";
           //String            OPTION_RHOST = "rhost";
           String            OPTION_CFG   = "cfg";
           //int               LOCAL_PORT  ;
           //String            REMOTE_HOST ;
           //int               REMOTE_PORT ;
           String            xpath ;
           Options           options;
           CommandLineParser parser;
           CommandLine       cmd;
          
           options = new Options();
           parser  = new DefaultParser();
           
           //options.addOption(null, OPTION_PORT  , true, "Run on the specified port.");
           //options.addOption(null, OPTION_RPORT , true, "Run as reverse proxy.");
           //options.addOption(null, OPTION_RHOST , true, "Run as reverse proxy.");
           options.addOption(null, OPTION_CFG   , true, "Run with cfg");
           options.addOption(null, OPTION_HELP  , false,"Display command line help.");
           try {
               cmd = parser.parse(options, args);
           } catch (final ParseException e) {
               LOG.error("Could not parse command line: " + Arrays.asList(args), e);
               printHelp(options,"Could not parse command line: " + Arrays.asList(args));
               return false;
           }
          
           if (cmd.hasOption(OPTION_HELP)) {
               printHelp(options, null);
               return false;
           }
           /*
           if (cmd.hasOption(OPTION_RHOST)) {
               REMOTE_HOST = cmd.getOptionValue(OPTION_RHOST);
           }
           else REMOTE_HOST = "127.0.0.1";
          
           if (cmd.hasOption(OPTION_RPORT)) {
               final String val = cmd.getOptionValue(OPTION_RPORT);
               try{
                   REMOTE_PORT = Integer.parseInt(val);
               } 
               catch (final NumberFormatException e) {
                     LOG.error("Unexpected remote port " + val, e);
                     printHelp(options, "Unexpected remote port " + val);
                     return false;
               }
           } else {
               REMOTE_PORT = 19;
           }
           if (cmd.hasOption(OPTION_PORT)) {
               final String val = cmd.getOptionValue(OPTION_PORT);
               try{
                   LOCAL_PORT = Integer.parseInt(val);
               } 
               catch (final NumberFormatException e) {
                     LOG.error("Unexpected bind port " + val, e);
                     printHelp(options, "Unexpected bind port " + val);
                     return false;
               }
           } else {
               LOCAL_PORT = 8080;
           }
           */
           if (cmd.hasOption(OPTION_CFG)) {
               xpath = cmd.getOptionValue(OPTION_CFG);
           } else {
               xpath = "littleproxy_0.xml";
           }
           
           if(commonProxy.get().loadCFG(xpath)==false){
              LOG.error("error read config file:"+xpath);
              return false;
           }
           commonProxy.get().init();
           commonProxy.get().initMBean();

           //System.out.println("Proxying *:" + LOCAL_PORT + " to " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");
           //server.setPort(LOCAL_PORT);
           //server.setRPort(REMOTE_PORT);
           //server.setRHost(REMOTE_HOST);
           
           server.init();

           System.out.println("Proxying bind:" + commonProxy.get().getPort() + " to " + commonProxy.get().getHosts().getDefaultHost() + ':' + commonProxy.get().getHosts().getDefaultPort() + " ...");

           return true;
    }


    private static void printHelp(final Options options,final String errorMessage) {
        if (!StringUtils.isBlank(errorMessage)) {
            LOG.error(errorMessage);
            System.err.println(errorMessage);
        }

        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("littleproxy", options);
    }
}
