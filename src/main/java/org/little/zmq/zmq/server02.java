package org.little.zmq.zmq;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZThread;

public class server02{
    //  Return a new UUID as a printable character string
    //  Caller must free returned string when finished with it
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

    public static final String TITANIC_DIR = ".titanic";

    //  Returns freshly allocated request filename for given UUID
    public static String requestFilename(String uuid){
        String filename = String.format("%s/%s.req", TITANIC_DIR, uuid);
        return filename;
    }

    //  Returns freshly allocated reply filename for given UUID
    public static String replyFilename(String uuid){
        String filename = String.format("%s/%s.rep", TITANIC_DIR, uuid);
        return filename;
    }

    //  .split worker task
    //  This is the main thread for the Titanic worker. It starts three child
    //  threads; for the request, reply, and close services. It then dispatches
    //  requests to workers using a simple brute force disk queue. It receives
    //  request UUIDs from the {{titanic.request}} service, saves these to a
    //  disk file, and then throws each request at MDP workers until it gets a
    //  response.
    public static void main(String[] args){
        boolean verbose = (args.length > 0 && "-v".equals(args[0]));

        try (ZContext ctx = new ZContext()) {
            Socket requestPipe = ZThread.fork(ctx, new serverRequest());
            ZThread.start(new serverReply());
            ZThread.start(new serverClose());

            Poller poller = ctx.createPoller(1);
            poller.register(requestPipe, ZMQ.Poller.POLLIN);

            //  Main dispatcher loop
            while (true) {
                //  We'll dispatch once per second, if there's no activity
                int rc = poller.poll(1000);
                if (rc == -1)
                    break; //  Interrupted
                if (poller.pollin(0)) {
                    //  Ensure message directory exists
                    new File(TITANIC_DIR).mkdirs();

                    //  Append UUID to queue, prefixed with '-' for pending
                    ZMsg msg = ZMsg.recvMsg(requestPipe);
                    if (msg == null)
                        break; //  Interrupted
                    String uuid = msg.popString();
                    BufferedWriter wfile = null;
                    try {
                        wfile = new BufferedWriter(
                            new FileWriter(TITANIC_DIR + "/queue", true)
                        );
                        wfile.write("-" + uuid + "\n");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    finally {
                        try {
                            if (wfile != null)
                                wfile.close();
                        }
                        catch (IOException e) {
                        }
                    }
                    msg.destroy();
                }

                // Brute force dispatcher
                // "?........:....:....:....:............:";
                byte[] entry = new byte[37];

                RandomAccessFile file = null;

                try {
                    file = new RandomAccessFile(TITANIC_DIR + "/queue", "rw");
                    while (file.read(entry) > 0) {
                        //  UUID is prefixed with '-' if still waiting
                        if (entry[0] == '-') {
                            if (verbose)
                                System.out.printf(
                                    "I: processing request %s\n",
                                    new String(
                                        entry, 1, entry.length - 1, ZMQ.CHARSET
                                    )
                                );
                            if (serviceSuccess(
                                    new String(
                                        entry, 1, entry.length - 1, ZMQ.CHARSET
                                    )
                                )) {
                                //  Mark queue entry as processed
                                file.seek(file.getFilePointer() - 37);
                                file.writeBytes("+");
                                file.seek(file.getFilePointer() + 36);
                            }
                        }

                        //  Skip end of line, LF or CRLF
                        if (file.readByte() == '\r')
                            file.readByte();

                        if (Thread.currentThread().isInterrupted())
                            break;
                    }
                }
                catch (FileNotFoundException e) {
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (file != null) {
                        try {
                            file.close();
                        }
                        catch (IOException e) {
                        }
                    }
                }
            }
        }
    }

    //  .split try to call a service
    //  Here, we first check if the requested MDP service is defined or not,
    //  using a MMI lookup to the Majordomo broker. If the service exists, we
    //  send a request and wait for a reply using the conventional MDP client
    //  API. This is not meant to be fast, just very simple:
    static boolean serviceSuccess(String uuid){
        //  Load request message, service will be first frame
        String filename = requestFilename(uuid);

        //  If the client already closed request, treat as successful
        if (!new File(filename).exists())
            return true;

        DataInputStream file = null;
        ZMsg request;
        try {
            file = new DataInputStream(new FileInputStream(filename));
            request = ZMsg.load(file);
        }
        catch (IOException e) {
            e.printStackTrace();
            return true;
        }
        finally {
            try {
                if (file != null)
                    file.close();
            }
            catch (IOException e) {
            }
        }
        ZFrame service = request.pop();
        String serviceName = service.toString();

        //  Create MDP client session with short timeout
        clientAPI client = new clientAPI("tcp://localhost:5555", false);
        client.setTimeout(1000); //  1 sec
        client.setRetries(1); //  only 1 retry

        //  Use MMI protocol to check if service is available
        ZMsg mmiRequest = new ZMsg();
        mmiRequest.add(service);
        ZMsg mmiReply = client.send("mmi.service", mmiRequest);
        boolean serviceOK = (mmiReply != null &&
                             mmiReply.getFirst().toString().equals("200"));
        mmiReply.destroy();

        boolean result = false;
        if (serviceOK) {
            ZMsg reply = client.send(serviceName, request);
            if (reply != null) {
                filename = replyFilename(uuid);
                DataOutputStream ofile = null;
                try {
                    ofile = new DataOutputStream(new FileOutputStream(filename));
                    ZMsg.save(reply, ofile);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
                finally {
                    try {
                        if (file != null)
                            file.close();
                    }
                    catch (IOException e) {
                    }
                }
                result = true;
            }
            reply.destroy();
        }
        else request.destroy();

        client.destroy();
        return result;
    }
}
