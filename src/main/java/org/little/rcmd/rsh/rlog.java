package org.little.rcmd.rsh;

import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class rlog{

       private static Logger       logger = LoggerFactory.getLogger(rlog.class);
       private static StringBuffer buf    = new StringBuffer();

       public static void print(String txt){
              buf.append(txt);
              if(txt.indexOf('\n')>=0){
                 logger.trace(buf.toString());
                 buf=new StringBuffer();
              }

       }
       public static void print(char txt){
              buf.append(txt);
              if(txt=='\n') {
                 logger.trace(buf.toString());
                 buf=new StringBuffer();
              }

       }

}
