package org.little.syslog.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;

import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServer;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Except;

public class TCPSyslogServer extends TCPNetSyslogServer {
       private static Logger logger = LoggerFactory.getLogger(TCPSyslogServer.class);
       protected Set<Socket> sockets;
       
       public TCPSyslogServer() {
              sockets = new HashSet<Socket>();
              sockets = Collections.synchronizedSet(sockets);
       }
       
       @Override
       public void run() {
              logger.info("starting  Syslog server socket tcp");
              printEvent           log=null;
              {
                SyslogServerConfigIF _log = this.getConfig();
               
                if(_log instanceof TCPSyslogServerConfig) {
                   log=((TCPSyslogServerConfig)_log).getLog(); 
                }
              }

              try {
                   logger.info("Creating Syslog server socket");
                   this.serverSocket = createServerSocket();
              } catch (IOException e) {
                   logger.error(" ex:"+new Except("Creating Syslog server socket",e));
              }
              if(this.serverSocket==null){
                 logger.error("cannot creating Syslog server socket");
              }
              else
              while (!this.shutdown) {
                     try {
                          Socket socket = this.serverSocket.accept();
                          logger.info("Handling Syslog client " + socket.getInetAddress());
                          new Thread(new TCPSyslogSocketHandler(this.sockets, this, socket,log)).start();

                     } catch (IOException e) {
                             logger.error(" ex:"+new Except("Handling Syslog client",e));
                     }
              }
       }

}
