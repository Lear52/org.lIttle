package org.little.stream.srv;

import java.io.File;

import org.zeromq.ZMsg;
import org.zeromq.ZThread.IDetachedRunnable;


public class serverClose implements IDetachedRunnable{
        @Override
        public void run(Object[] args){
        	String  broker ="tcp://localhost:5555";
        	String  service="titanic.close";
        	boolean verbose=true;

                serverAPI worker = new serverAPI(broker, service, verbose);

                ZMsg reply = null;
                while (true) {
                      ZMsg request = worker.receive(reply);
                      if (request == null) break; 
                      String uuid         = request.popString();
                      request.destroy();
                      reply = new ZMsg();
                      reply.add("200");
                }
                worker.destroy();
        }
    }

