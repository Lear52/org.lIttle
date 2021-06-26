package org.little.stream.srv;

//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;

import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
//import org.zeromq.ZMsg;
import org.zeromq.ZThread.IAttachedRunnable;

public class serverRequest implements IAttachedRunnable {
        @Override
        public void run(Object[] args, ZContext ctx, Socket pipe){
                /*
        	String broker="tcp://localhost:5555";
        	String service="titanic.request";
        	boolean verbose=true;

                serverAPI worker = new serverAPI(broker, service, verbose);
                ZContext ctx = new ZContext();
                ZMQ.Socket worker1;
                worker1 = ctx.createSocket(SocketType.DEALER);
                worker1.connect("tcp://localhost:5555");

                ZMsg reply = null;

                while (true) {
                    ZMsg request = worker.receive(reply);
                    if (request == null) break; //  Interrupted, exit
                    request.destroy();
                }
                worker.destroy();
                */
        }
}

