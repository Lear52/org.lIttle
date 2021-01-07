package org.little.proxy.tcpRelay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * A <code>Relay</code> object is used by <code>TcpTunnel</code>
 * and <code>TcpTunnelGui</code> to relay bytes from an
 * <code>InputStream</code> to a <code>OutputStream</code>.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 * @author Scott Nichol (snichol@computer.org)
 */
public class TcpRelay extends Thread {
       final static int BUFSIZ = 1000;
       InputStream  in;
       OutputStream out;
       byte buf[] = new byte[BUFSIZ];
       OutputStream os;
       String enc = "8859_1";
       String tag;

       TcpRelay(InputStream in, OutputStream out, OutputStream os, String enc,String tag) {
           this.in = in;
           this.out = out;
           this.os = os;
           this.enc = enc;
           this.tag = tag;
       }
       public String getEncoding() {
           return enc;
       }
       public void run() {
           int n;
           try {
               while ((n = in.read(buf)) > 0) {
                   out.write(buf, 0, n);
                   out.flush();
                   if (os != null) {
                       os.write(tag.getBytes());/**/
                       os.write(buf, 0, n);
                       os.flush();
                   }
               }
           } catch (IOException e) {
               String s;
               s=e.getMessage()+" "+"\r\n";
               try {
                   os.write(s.getBytes());
                   os.flush();
               } catch (IOException e1) {}
              
           } finally {
               try {
                   in.close();
                   out.close();
                   String s;
                   s=new Date()+"\r\n";
                   os.write(s.getBytes());
                   s="----------------------------------------------------------\r\n";
                   os.write(s.getBytes());
                   os.flush();
                   os.close();
                   
               } catch (IOException e) {}
           }
       }
       public void setEncoding(String enc) {
           this.enc = enc;
       }
}
