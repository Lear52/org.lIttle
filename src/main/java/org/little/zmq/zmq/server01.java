package org.little.zmq.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class server01{

    public static void main(String[] args){
        ZContext context;
        try  {
            context = new ZContext();
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {

                byte[] reply = socket.recv(0);
                System.out.println("Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]" );

                String response = "world";

                byte [] buf=response.getBytes(ZMQ.CHARSET);
                socket.send(buf, 0);
                Thread.sleep(1000); //  Do some 'work'
            }
        }
        catch(Exception ex){
              System.out.println("Error ex:" +ex);
        }
    }
}
