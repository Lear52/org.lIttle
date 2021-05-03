package org.little.syslog.impl;
        
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;

public class printEventLog  implements printEvent {
       private static Logger logger = LoggerFactory.getLogger(printEventLog.class);

       @Override
       public void print(SyslogServerEventIF event){
           logger.trace(new String(event.getRaw()));
           logger.info(event);
       }


          
}
