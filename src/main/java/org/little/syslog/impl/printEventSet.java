package org.little.syslog.impl;
        
import java.util.ArrayList;

import org.productivity.java.syslog4j.server.SyslogServerEventIF;

public class printEventSet  implements printEvent {
       private ArrayList<printEvent> list_log;

       public printEventSet(){
              list_log=new ArrayList<printEvent>();
       }

       public void add(printEvent log){list_log.add(log);}

       @Override
       public void print(SyslogServerEventIF event){
              for(int i=0;i<list_log.size();i++)list_log.get(i).print(event);
       }


          
}
