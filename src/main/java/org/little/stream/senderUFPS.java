package org.little.stream;

import org.zeromq.ZFrame;
import org.zeromq.ZMsg;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.stream.srv.*;

public class senderUFPS {
       private static final Logger logger = LoggerFactory.getLogger(senderUFPS.class);
       private String    destinationName;
       private clientAPI session;
       
       public senderUFPS() {
              clear();
       }

       public void clear() {
              destinationName=null;
              session=null;
       }
       
       public void open() {
              boolean verbose=false;
              session = new clientAPI("tcp://localhost:5555", verbose);
       }
       public void close(){
              if(session!=null)session.destroy();
              session=null;
       }
       
       public void run() {
              ZMsg request = new ZMsg();
              request.add("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

              ZMsg reply = session.send(destinationName, request);
              if(reply != null) {
                 ZFrame status = reply.pop();
                 if(status.streq("200")) {
                    status.destroy();
                    return ;
                 }
                 reply.destroy();
              }
                         
              try{Thread.sleep(5000);}catch(Exception e){}
       }



}
