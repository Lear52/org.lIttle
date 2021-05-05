package org.little.zmq.zmq;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
                if (request == null)
                    break; //  Interrupted, exit

                String uuid = request.popString();
                String reqFilename = server02.requestFilename(uuid);
                String repFilename = server02.replyFilename(uuid);

                if (new File(repFilename).exists()) {
                    DataInputStream file = null;
                    try {
                        file = new DataInputStream(
                            new FileInputStream(repFilename)
                        );
                        reply = ZMsg.load(file);
                        reply.push("200");
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
                }
                else {
                    reply = new ZMsg();
                    if (new File(reqFilename).exists())
                        reply.push("300"); //Pending
                    else reply.push("400"); //Unknown
                }
                request.destroy();
            }
            worker.destroy();
        }
}
