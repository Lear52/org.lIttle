package org.little.stream.srv;

import org.zeromq.ZMsg;
import org.zeromq.ZThread.IDetachedRunnable;


public class serverReply implements IDetachedRunnable {
        @Override
        public void run(Object[] args){
        	    String broker="tcp://localhost:5555";
        	    String service="titanic.reply";
        	    boolean verbose=true;
                serverAPI worker = new serverAPI(broker, service, verbose);
        	
                ZMsg reply = null;

                 while (true) {
                    ZMsg request = worker.receive(reply);
                    if (request == null)break; //  Interrupted, exit
            
                     String uuid = request.popString() ;
            
                     reply.push("200");
                     reply = new ZMsg();
                     reply.push("300"); //Pending
                     request.destroy();
                }
                worker.destroy();
        }
}
