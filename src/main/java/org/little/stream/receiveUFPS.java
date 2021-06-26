package org.little.stream;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZThread;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.stream.srv.*;

public class receiveUFPS {
       private static final Logger logger = LoggerFactory.getLogger(receiveUFPS.class);
       private String               destinationName;
       
       public receiveUFPS() {
              clear();
       }

       public void clear() {

       }
       
       public void open() {
              try {
                  ZContext ctx = new ZContext();
                  Socket requestPipe = ZThread.fork(ctx, new serverRequest());
                  //ZThread.start(new serverReply());
                  //ZThread.start(new serverClose());

                  Poller poller = ctx.createPoller(1);
                  poller.register(requestPipe, ZMQ.Poller.POLLIN);
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
