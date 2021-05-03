package org.little.syslog;

import java.net.UnknownHostException;

import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.little.syslog.impl.TCPSyslogServerConfig;
import org.little.syslog.impl.UDPSyslogServerConfig;
import org.little.syslog.impl.printEventBuf;
import org.little.syslog.impl.printEventConsole;
import org.little.syslog.impl.printEventLog;
import org.little.syslog.impl.printEventSet;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;

public class Server extends tfork{
       private static Logger logger = LoggerFactory.getLogger(Server.class);

       public  int           SYSLOG_PORT;
       private printEventSet log;
       private printEventBuf last;
       private String     syslogProtocol;

       public Server(){
              syslogProtocol = "udp";
              SYSLOG_PORT = 9898;
              log = new printEventSet();
              last= new printEventBuf();
              log.add(new printEventLog());
              log.add(last);
       }
       public void printConsole() {
              log.add(new printEventConsole());
       }
       public void set(String _syslogProtocol,int _SYSLOG_PORT) {
              syslogProtocol = _syslogProtocol;
              SYSLOG_PORT    = _SYSLOG_PORT;
       }
       public printEventBuf print(){return last;}

       @Override
       public void run() {
              SyslogServerConfigIF config1 =null;
              SyslogServerConfigIF config2 =null;
              //if ("udp".equals(syslogProtocol)) 
              config1 = new UDPSyslogServerConfig(log);
              //else
              //if ("tcp".equals(syslogProtocol)) 
              config2 = new TCPSyslogServerConfig(log);
              //else {
              //   logger.error("Unsupported Syslog protocol: " + syslogProtocol);
              //   return;
              //}
              config1.setUseStructuredData(true);
              config1.setHost("0.0.0.0");
              config1.setPort(SYSLOG_PORT);
              config2.setUseStructuredData(true);
              config2.setHost("0.0.0.0");
              config2.setPort(SYSLOG_PORT);

              logger.info("Starting Syslog Server");
              logger.info("Protocol:     " + syslogProtocol);
              logger.info("Bind address: " + config1.getHost());
              logger.info("Port:         " + config1.getPort());

              // clear created server instances (TCP/UDP)
              SyslogServer.shutdown();
              // start syslog server
              SyslogServerIF server1=SyslogServer.createThreadedInstance("udp", config1);
              SyslogServerIF server2=SyslogServer.createThreadedInstance("tcp", config2);
              if(server1.getThread() == null) {
                 Thread thread = new Thread(server1);
                 thread.setName("SyslogServer:udp");
                 thread.setDaemon(server1.getConfig().isUseDaemonThread());
                 //if (server.getConfig().getThreadPriority() > -1) {
                 //       thread.setPriority(server.getConfig().getThreadPriority());
                 //}
                 //
                 server1.setThread(thread);
                 thread.start();
              }
              if(server2.getThread() == null) {
                 Thread thread = new Thread(server2);
                 thread.setName("SyslogServer:tcp");
                 thread.setDaemon(server2.getConfig().isUseDaemonThread());
                 //if (server.getConfig().getThreadPriority() > -1) {
                 //       thread.setPriority(server.getConfig().getThreadPriority());
                 //}
                 //
                 server2.setThread(thread);
                 thread.start();
              }
              
              while(isRun()){
                  try {Thread.sleep(100);}catch(Exception e){}
              }
              server1.shutdown();
              server2.shutdown();
              SyslogServer.shutdown();
              logger.info("End Syslog Server");
       }

       public static void main(String[] args) throws SyslogRuntimeException, UnknownHostException {
              Server server=new Server();
              // Details for the properties -
              // http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html
              //System.setProperty("jsse.enableSNIExtension", "false");
              // just in case...
              //System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
              //System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true");

              if(args.length > 0) {
                 server.set(args[0],9898);
              } 
              else {
                     logger.error("No protocol provided. Defaulting to udp");
                     logger.error("Simple syslog server (RFC-5424)");
                     logger.error("Usage:");
                     logger.error("  java  org.little.syslog.Server [protocol]");
                     logger.error("Possible protocols: udp, tcp");
                     return;
              }
              server.printConsole();
              server.fork();


       }


}
