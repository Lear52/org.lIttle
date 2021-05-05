package org.little.syslog.impl;
        
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;

public class printEventMQ  implements printEvent {
       private static Logger logger = LoggerFactory.getLogger(printEventLog.class);
       private String mq_mngr;
       private String mq_queue;
       private String mq_host;
       private int    mq_port;
       private String mq_user;
       private String mq_passwd;
       private String mq_channel;

       public printEventMQ(){

       }
       public void open(){

       }

       public void close(){

       }

       @Override
       public void print(SyslogServerEventIF event){

           logger.trace(new String(event.getRaw()));
       }


          
}
