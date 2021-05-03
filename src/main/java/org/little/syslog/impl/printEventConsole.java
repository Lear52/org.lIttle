package org.little.syslog.impl;
        
import org.productivity.java.syslog4j.server.SyslogServerEventIF;

public class printEventConsole  implements printEvent {

   	@Override
       public void print(SyslogServerEventIF event){
               System.out.println(">>>" + event.getMessage());
       }


          
}
