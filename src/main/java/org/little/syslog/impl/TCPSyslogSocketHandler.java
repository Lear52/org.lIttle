package org.little.syslog.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Set;

import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Except;

public class TCPSyslogSocketHandler implements Runnable {
       private static Logger logger = LoggerFactory.getLogger(TCPSyslogSocketHandler.class);

       protected SyslogServerIF server = null;
       protected Socket         socket = null;
       protected Set<Socket>    sockets = null;
       private   printEvent     log;
       /**
        * Constructor.
        *
        * @param sockets
        *            Set of all registered handlers.
        * @param server
        *            Syslog server instance
        * @param socket
        *            socket returned from the serverSocket.accept()
        */
       public TCPSyslogSocketHandler(Set<Socket> sockets, SyslogServerIF server, Socket socket,printEvent log) {
              this.sockets = sockets;
              this.server  = server;
              this.socket  = socket;
              this.log     = log;

              synchronized (this.sockets) {
                 this.sockets.add(this.socket);
              }
       }

       public void run() {
              try {
                     final BufferedInputStream   bis  = new BufferedInputStream(socket.getInputStream());
                     final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     boolean       firstByte     = true;
                     boolean       octetCounting = false;
                     StringBuilder octetLenStr   = new StringBuilder();
                     int b = -1;

                     do {
                            b = bis.read();
                            if (firstByte && b >= '1' && b <= '9') {
                                   // handle Octet Counting messages (cf. rfc-6587)
                                   octetCounting = true;
                            }
                            firstByte = false;
                            if (octetCounting) {
                                   if (b != ' ') {
                                          octetLenStr.append((char) b);
                                   } else {
                                          int len = Integer.parseInt(octetLenStr.toString());
                                          handleSyslogMessage(toByteArray(bis, len));
                                          // reset the stuff
                                          octetLenStr = new StringBuilder();
                                          firstByte = true;
                                          octetCounting = false;
                                   }
                            } else {
                                   // handle Non-Transparent-Framing messages (cf. rfc-6587)
                                   switch (b) {
                                   case -1:
                                   case '\r':
                                   case '\n':
                                          if (baos.size() > 0) {
                                              handleSyslogMessage(baos.toByteArray());
                                              baos.reset();
                                              firstByte = true;
                                          }
                                          break;
                                   default:
                                          baos.write(b);
                                          break;
                                   }
                            }
                     } while (b != -1);
              } catch (IOException e) {
                      logger.error(" ex:"+new Except("read Syslog server socket",e));
              } finally {
                     try {if (socket != null){socket.close();}} catch (IOException ioe) {}
                     sockets.remove(socket);
              }
       }

       /**
        * Parses {@link Rfc5424Event} instance from given raw message bytes
        * and sends it to event handlers.
        *
        * @param rawMsg
        */
       private void handleSyslogMessage(final byte[] rawMsg) {
               SyslogServerEventIF event = new Rfc5424Event(rawMsg, 0, rawMsg.length);
               if(log!=null)log.print(event);
               else logger.trace("Syslog:"+event);
       }
       public static byte[] toByteArray(InputStream input, int size) throws IOException {
    	   final int EOF = -1;
           if (size < 0) {
               throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
           }

           if (size == 0) {
               return new byte[0];
           }

           byte[] data = new byte[size];
           int offset = 0;
           int readed;

           while (offset < size && (readed = input.read(data, offset, size - offset)) != EOF) {
               offset += readed;
           }

           if (offset != size) {
               throw new IOException("Unexpected readed size. current: " + offset + ", excepted: " + size);
           }

           return data;
       }

}