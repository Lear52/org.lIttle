package org.little.syslog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.little.util.Except;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class Client {
       public static String app=Client.class.getName();

       public static class Options {
              public String host     = null;
              public String port     = null;
              public String level    = "INFO";
              public String facility = "USER";
              public String protocol = null;
              public String message  = null;
              public String fileName = null;
              
              public boolean quiet = false;       

              public String usage = null;
       }
       
       public static void usage(String problem) {
              if(problem != null) {
                 System.out.println("Error: " + problem);
                 System.out.println();
              }
              
              System.out.println("Usage:");
              System.out.println();
              System.out.println(app+" [-h <host>] [-p <port>] [-l <level>] [-f <facility>]");
              System.out.println("       <protocol>");
              System.out.println();
              System.out.println(app+" [-h <host>] [-p <port>] [-l <level>] [-f <facility>]");
              System.out.println("       <protocol> [message...]");
              System.out.println();
              System.out.println(app+" [-h <host>] [-p <port>] [-l <level>] [-f <facility>]");
              System.out.println("       -i <file> <protocol>");
              System.out.println();
              System.out.println("-h <host>      host or IP to send message (default: localhost)");
              System.out.println("-p <port>      port to send message (default: 514)");
              System.out.println("-l <level>     syslog level to use (default: INFO)");
              System.out.println("-f <facility>  syslog facility to use (default: USER)");
              System.out.println("-i <file>      input taken from the specified file");
              System.out.println();
              System.out.println("-q             do not write anything to standard out");
              System.out.println();
              System.out.println("protocol       syslog protocol implementation");
              System.out.println("message        syslog message text");
              System.out.println();
              System.out.println("Notes:");
              System.out.println();
              System.out.println("Additional message arguments will be concatenated into the same");
              System.out.println("syslog message; calling SyslogMain will only send one message per call.");
              System.out.println();
              System.out.println("If the message argument is ommited, lines will be taken from the");
              System.out.println("standard input.");
       }
       
       public static Options parseOptions(String[] args) {
              Options options = new Options();

              
              int i = 0;
              while(i < args.length) {
                    String arg = args[i++];
                    boolean match = false;

                    if ("-h".equals(arg)) { if (i == args.length) { options.usage = "Must specify host with -h"; return options; } match = true; options.host = args[i++]; System.out.println("host:"+options.host);continue;}
                    if ("-p".equals(arg)) { if (i == args.length) { options.usage = "Must specify port with -p"; return options; } match = true; options.port = args[i++]; System.out.println("port:"+options.port);continue;}
                    if ("-l".equals(arg)) { if (i == args.length) { options.usage = "Must specify level with -l"; return options; } match = true; options.level = args[i++]; System.out.println("level:"+options.level);continue;}
                    if ("-f".equals(arg)) { if (i == args.length) { options.usage = "Must specify facility with -f"; return options; } match = true; options.facility = args[i++]; System.out.println("facility:"+options.facility);continue;}
                    if ("-i".equals(arg)) { if (i == args.length) { options.usage = "Must specify file with -i"; return options; } match = true; options.fileName = args[i++]; System.out.println("fileName:"+options.fileName);continue;}
                    
                    if ("-q".equals(arg)) { match = true; options.quiet = true; continue;}

                    
                    if(options.protocol == null && !match) {
                       match = true;
                       options.protocol = arg;
                       System.out.println("protocol:"+options.protocol);
                    }

                    if(!match) {
                       if(options.message == null) {
                         options.message = arg;
                              
                       } else {
                         options.message += " " + arg;
                       }
                    }
              }
              
              if(options.protocol == null) {
                 options.usage = "Must specify protocol";
                 return options;
              }
              
              if(options.message != null && options.fileName != null) {
                 options.usage = "Must specify either -i <file> or <message>, not both";
                 return options;
              }

              return options;
       }
       
       public static void main(String[] args){

              Options options = parseOptions(args);

              if(options.usage != null) {
                 usage(options.usage);
                 return ;
              }
              
              if(!options.quiet) {
                 System.out.println("Syslog " + Syslog.getVersion());
              }
              
              if(!Syslog.exists(options.protocol)) {
                 usage("Protocol \"" + options.protocol + "\" not supported");
                 return;
              }
              

              try{
                 SyslogIF syslog = Syslog.getInstance(options.protocol);
                 System.out.println("getInstance("+options.protocol+")");
                 
                 SyslogConfigIF syslogConfig = syslog.getConfig();
                 
                 if(options.host != null) {
                    syslogConfig.setHost(options.host);
                    if(!options.quiet){System.out.println("Sending to host: " + options.host);}
                 }
                
                 if(options.port != null) {
                    syslogConfig.setPort(Integer.parseInt(options.port));
                    if(!options.quiet){System.out.println("Sending to port: " + options.port);}
                 }
                 
                 int level = SyslogUtility.getLevel(options.level);
                 syslogConfig.setFacility(options.facility);
                 
                 if(options.message != null) {
                    if(!options.quiet) { System.out.println("Sending " + options.facility + "." + options.level + " message \"" + options.message + "\"");}              
                    syslog.log(level,options.message);
                 } 
                 else {
                    BufferedReader br;
                    if(options.fileName != null)br = new BufferedReader(new InputStreamReader(new FileInputStream(options.fileName)));
                    else                        br = new BufferedReader(new InputStreamReader(System.in)); 
                    
                    String line = br.readLine();
                    while(line != null && line.length() > 0) {
                          if(!options.quiet) {
                            System.out.println("Sending " + options.facility + "." + options.level + " message \"" + line + "\"");
                          }
                          syslog.log(level,line);
                          line = br.readLine();
                    }
                 }


                 for(int i=0;i<10;i++)try {Thread.sleep(100);}catch(Exception e){}


              }
              catch(Exception ex){
                   System.out.println("ex:"+new Except("Error send",ex));
              }
              finally{
                 Syslog.shutdown();
                 System.out.println("shutdown");
              }

              return;
       }
}
