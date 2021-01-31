package org.little.util.wrapper;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.lang3.StringUtils;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

public interface iWrapper extends Runnable{
       public static final Logger     _LOG = LoggerFactory.getLogger(iWrapper.class);
      
       public static final String      OPTION_CFG   = "cfg";
       public static final String      OPTION_HELP  = "help";
      
       public void    clear() ;
       public void    run() ;
       public int     start(String args[]) ;
       public int     stop(int exitCode) ;
       public int     restart() ;
      
       
       public static void printHelp(final Options options,final String errorMessage) {
              if (!StringUtils.isBlank(errorMessage)) {
                  _LOG.error(errorMessage);
              }
             
              final HelpFormatter formatter = new HelpFormatter();
              formatter.printHelp("littleproxy", options);
       }
    
       public static String getFileanme(String args[]) {
              Options           options;
              CommandLineParser parser;
              CommandLine       cmd;
              String            xpath;
             
             
              options = new Options();
              parser  = new DefaultParser();
              xpath   ="./littleproxy.xml";
             
              options.addOption(null, OPTION_CFG   , true, "Run with cfg file.");
              options.addOption(null, OPTION_HELP  , false,"Display command line help.");
             
              try {
                  cmd = parser.parse(options, args);
              } catch (final ParseException e) {
                  _LOG.error("Could not parse command line: " + Arrays.asList(args), e);
                  printHelp(options,"Could not parse command line: " + Arrays.asList(args));
                  return null;
              }
             
              if (cmd.hasOption(OPTION_HELP)) {
                  printHelp(options, null);
                  return null;
              }
              
              if (cmd.hasOption(OPTION_CFG)) {
                  xpath = cmd.getOptionValue(OPTION_CFG);
              } 
              return xpath;
    }

}

