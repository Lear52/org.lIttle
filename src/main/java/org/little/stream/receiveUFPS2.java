package org.little.stream;

import org.zeromq.*;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.zeromq.jms.*;

public class receiveUFPS2 {
       private static final Logger logger = LoggerFactory.getLogger(receiveUFPS2.class);
       private String               destinationName;
       
       public receiveUFPS2() {
              clear();
       }

       public void clear() {

       }
       
       public void open() {
              try {
              }
              catch (Exception ex) {
                     logger.error("open sender ex:"+new Except("",ex));
              }
             
       }
       public void close(){
       }
       
       public void run() {
       }



}
