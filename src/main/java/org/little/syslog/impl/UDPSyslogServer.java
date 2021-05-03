package org.little.syslog.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServer;

public class UDPSyslogServer extends UDPNetSyslogServer {
       private static Logger logger = LoggerFactory.getLogger(UDPSyslogServer.class);

       @Override
       public void shutdown() {
              super.shutdown();
              thread = null;
       }

       @Override
       public void run() {
              this.shutdown = false;
              printEvent log=null;
              SyslogServerConfigIF _log = this.getConfig();
              if(_log instanceof UDPSyslogServerConfig) {
            	 log=((UDPSyslogServerConfig)_log).getLog(); 
              }
              
              try {
                     this.ds = createDatagramSocket();
              } catch (Exception e) {
                     logger.error("ex:"+new Except("Creating DatagramSocket failed",e));
                     //e.printStackTrace();
                     //throw new SyslogRuntimeException(e);
              }

              byte[] receiveData = new byte[SyslogConstants.SYSLOG_BUFFER_SIZE];

              while (!this.shutdown) {
                     try {
                            final DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);
                            this.ds.receive(dp);
                            final SyslogServerEventIF event = new Rfc5424Event(receiveData, dp.getOffset(), dp.getLength());

                            if(log!=null)log.print(event);
                            else logger.trace("Syslog:"+event);

                     } catch (SocketException se) {
                              logger.error("ex:"+new Except("read DatagramSocket failed",se));
                     } catch (IOException ioe) {
                              logger.error("ex:"+new Except("read DatagramSocket failed",ioe));
                     }
              }
       }
}
