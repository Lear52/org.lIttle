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
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class NullProxy_Server implements  Runnable{

    private static final Logger logger = LoggerFactory.getLogger(NullProxy_Server.class);

    protected Thread  process_tread;

    /**
     *
     */
    public NullProxy_Server(){
           process_tread       = null;
    }

    public void init(){}

    @Override
    public void run(){}

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
           process_tread      = null;
    }

    public static void main(String[] args) {

           System.setProperty("java.net.preferIPv4Stack","true");
           logger.trace("Set java property:java.net.preferIPv4Stack=true");

           NullProxy_Server server=new NullProxy_Server();
           run_main(server,args);
    }

    public static void run_main(NullProxy_Server server, String[] args) {

           System.setProperty("java.net.preferIPv4Stack","true");
           logger.trace("Set java property:java.net.preferIPv4Stack=true");

           if(!NullProxy_Server.opt(server,args)){
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
    public static boolean opt(NullProxy_Server server,String[] args) {
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
              buf.append(NullProxyFServer.class).append(" ");
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

           System.out.println("reverse tunnelig FRONT bind:" + commonProxy.get().getCfgServer().getPort() + " <> " + (commonProxy.get().getCfgServer().getPort()+1));

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

