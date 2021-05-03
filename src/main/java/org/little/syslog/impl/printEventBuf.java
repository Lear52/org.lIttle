package org.little.syslog.impl;
        
import org.productivity.java.syslog4j.server.SyslogServerEventIF;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class printEventBuf  implements printEvent {
       
       private ArrayDeque<SyslogServerEventIF> list_event;


       public printEventBuf(){
              list_event=new ArrayDeque<SyslogServerEventIF>();
       }

       @Override
       public void print(SyslogServerEventIF event){
              list_event.addFirst(event);
              if(list_event.size()>25)list_event.removeLast();
       }

       public  ArrayList<String> getString(){
               ArrayList<String> list_str=new ArrayList<String>();
               Iterator<SyslogServerEventIF> list=list_event.iterator();               
               while(list.hasNext()) {
            	   list_str.add(list.next().toString());
               }
               return list_str;
       }

       public  Iterator<SyslogServerEventIF> getEvent(){
           Iterator<SyslogServerEventIF> list=list_event.iterator();               
           return list;
   }

          
}
