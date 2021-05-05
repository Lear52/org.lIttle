package org.little.zmq.zmq;

import java.io.File;

import org.zeromq.ZMsg;
import org.zeromq.ZThread.IDetachedRunnable;


    //  .split Titanic close task
    //  The {{titanic.close}} task removes any waiting replies for the request
    //  (specified by UUID). It's idempotent, so it is safe to call more than
    //  once in a row:
public class serverClose implements IDetachedRunnable{
        @Override
        public void run(Object[] args){
        	String broker="tcp://localhost:5555";
        	String service="titanic.close";
        	boolean verbose=true;
            serverAPI worker = new serverAPI(broker, service, verbose);
            ZMsg reply = null;

            while (true) {
                ZMsg request = worker.receive(reply);
                if (request == null)
                    break; //  Interrupted, exit

                String uuid = request.popString();
                String req_filename = server02.requestFilename(uuid);
                String rep_filename = server02.replyFilename(uuid);
                new File(rep_filename).delete();
                new File(req_filename).delete();

                request.destroy();
                reply = new ZMsg();
                reply.add("200");
            }
            worker.destroy();
        }
    }

