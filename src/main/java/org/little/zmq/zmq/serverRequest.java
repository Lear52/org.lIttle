package org.little.zmq.zmq;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZThread.IAttachedRunnable;

public class serverRequest implements IAttachedRunnable {
        @Override
        public void run(Object[] args, ZContext ctx, Socket pipe){
        	String broker="tcp://localhost:5555";
        	String service="titanic.request";
        	boolean verbose=true;
            serverAPI worker = new serverAPI(broker, service, verbose);
            ZMsg reply = null;

            while (true) {
                //  Send reply if it's not null
                //  And then get next request from broker
                ZMsg request = worker.receive(reply);
                if (request == null)
                    break; //  Interrupted, exit

                //  Ensure message directory exists
                new File(server02.TITANIC_DIR).mkdirs();

                //  Generate UUID and save message to disk
                String uuid = server02.generateUUID();
                String filename = server02.requestFilename(uuid);
                DataOutputStream file = null;
                try {
                    file = new DataOutputStream(new FileOutputStream(filename));
                    ZMsg.save(request, file);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if (file != null)
                            file.close();
                    }
                    catch (IOException e) {
                    }
                }
                request.destroy();

                //  Send UUID through to message queue
                reply = new ZMsg();
                reply.add(uuid);
                reply.send(pipe);

                //  Now send UUID back to client
                //  Done by the mdwrk_recv() at the top of the loop
                reply = new ZMsg();
                reply.add("200");
                reply.add(uuid);
            }
            worker.destroy();
        }
}

