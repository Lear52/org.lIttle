package org.little.proxy.Null;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.little.proxy.commonProxy;
import org.little.proxy.Null.handler.NullProxyInitializer;
import org.little.proxy.Null.handler.NullProxyLogHandler;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

public class NullProxyServer implements  Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(NullProxyServer.class);

    private   EventLoopGroup                     parent_event_group;
    private   EventLoopGroup                     child_event_group;
    private   EventLoopGroup                     sharp_event_group;
    private   ServerBootstrap                    server_boot_strap;
    protected Thread                             process_tread;


    /**
     *
     */
    public NullProxyServer(){
           parent_event_group = null;
           child_event_group  = null;
           server_boot_strap  = null;
           process_tread      = null;
    }

    public void init(){}

    public void run(){

        LOG.trace("begin run()");

        try {
            NullProxyInitializer child_channel;
            LoggingHandler       log;
            server_boot_strap      = new ServerBootstrap();
            parent_event_group     = new NioEventLoopGroup(commonProxy.get().getNumberThreads()); //specified_number_of_threads = 4;//1
            child_event_group      = new NioEventLoopGroup();
            child_channel          = new NullProxyInitializer();
            log                    = new NullProxyLogHandler();

            
            LOG.trace("create ServerBootstrap");
            server_boot_strap=server_boot_strap.group(parent_event_group, child_event_group);
            LOG.trace("register parent_event_group, child_event_group");
            // 
            server_boot_strap=server_boot_strap.channel(NioServerSocketChannel.class);
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

    public static void main(String[] args) {

           System.setProperty("java.net.preferIPv4Stack","true");
           LOG.trace("Set java property:java.net.preferIPv4Stack=true");

           NullProxyServer server=new NullProxyServer();

           if(!NullProxyServer.opt(server,args)){
              LOG.error("unknow args");
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
              LOG.info("run: " + buf.toString());
           }

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
           if (cmd.hasOption(OPTION_CFG)) {
               xpath = cmd.getOptionValue(OPTION_CFG);
           } else {
               xpath = "littleproxy.xml";
           }

           LOG.info("config file:"+xpath);
           
           if(commonProxy.get().loadCFG(xpath)==false){
              LOG.error("error read config file:"+xpath);
              return false;
           }
           commonProxy.get().init();
           commonProxy.get().initMBean();

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
