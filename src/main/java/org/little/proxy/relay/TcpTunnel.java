package org.little.proxy.relay;


import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A <code>TcpTunnel</code> object listens on the given port,
 * and once <code>Start</code> is pressed, will forward all bytes
 * to the given host and port.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class TcpTunnel {
       @SuppressWarnings("resource")
	public static void main(String args[]) throws IOException {
           if (args.length != 3 && args.length != 4) {
               System.err.println("Usage: java TcpTunnel listenport tunnelhost tunnelport [encoding]");
               System.exit(1);
           }
           int listenport = Integer.parseInt(args[0]);
           String tunnelhost = args[1];
           int tunnelport = Integer.parseInt(args[2]);
           String enc;
           if (args.length == 4) {
               enc = args[3];
           } else {
               enc = "8859_1";
           }
           System.out.println("TcpTunnel: ready to rock and roll on port " + listenport);
      
           ServerSocket ss=null;
           try {
        	   ss = new ServerSocket(listenport);
           
			   while (true) {
				   Socket sc;
				   Socket st;
			       
			       sc = ss.accept();
				   st = new Socket(tunnelhost, tunnelport);
			       int local_port1=st.getLocalPort();
			       //int local_port2=sc.getLocalPort();
			       System.out.println("TcpTunnel: tunnelling port " + listenport + " to port " + tunnelport + " on host " + tunnelhost);
     
			       String date=date2str(new Date());

			       String filename="file_log_"+listenport+"_"+tunnelhost+"-"+tunnelport+"_"+local_port1+"_"+date+".txt";
			       FileOutputStream os = new FileOutputStream(filename);
			       String s;
			       s="TcpTunnel: ready to rock and roll on port " + listenport+" "+tunnelhost+"-"+tunnelport+"\r\n";
			       os.write(s.getBytes());
			       s=new Date()+"\r\n";
			       os.write(s.getBytes());
			       s="*---------------------------------------------------------------------------------------------------------\r\n";
			       os.write(s.getBytes());
			       // relay the stuff thru
			       new Relay(sc.getInputStream(), st.getOutputStream(), os, enc,"1>").start();
			       new Relay(st.getInputStream(), sc.getOutputStream(), os, enc,"2>").start();
			       // that's it .. they're off; now I go back to my stuff.
			   }
		  }
          finally{
        	  try {ss.close();} catch(Exception e) {}
        	  
          }
       }
       public static String date2str(Date d){
              if(d==null) return null;
              try{
                  SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                  return sfd.format(d);
              } catch (Exception e) { return null; }
       }

}
