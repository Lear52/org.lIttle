package org.little.rsh;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class rShell{
        private JSch     jsch;
        private Session  session;

        private String   host;
        private String   user;
        private String   passwd;
        private Channel  channel;
        private InputStream in;
        private OutputStream out;
        
        public rShell() {
                clear();
                
        }
        public rShell(InputStream _in,OutputStream _out) {
            clear();
            in=_in;
            out=_out;
            
    }
        private void clear() {
                jsch=new JSch();
                    session=null;
                    host=null;
                    user=null;
                    passwd=null;
                
                
        }
        public String getHost() {return host;}
        public void   setHost(String host) {this.host = host;}
        public String getUser() {return user;}
        public void   setUser(String user) {this.user = user;        }
        public String getPasswd() {        return passwd;        }
        public void   setPasswd(String passwd) {this.passwd = passwd;        }

        public boolean open() {
            try{
                   session=jsch.getSession(user, host, 22);
                   session.setPassword(passwd);
                   UserInfo ui = new UserInfo(){
                                 public void showMessage(String message){
                                 }
                                 public boolean promptYesNo(String message){
                                 return true;
                                 }
								@Override
								public String getPassphrase() {
									
									return null;
								}
								@Override
								public String getPassword() {
									
									return null;
								}
								@Override
								public boolean promptPassphrase(String arg0) {
									
									return false;
								}
								@Override
								public boolean promptPassword(String arg0) {
									
									return false;
								}
                   };
                   session.setUserInfo(ui);
                   session.connect(30000);  

                   channel=session.openChannel("shell");
                   out=channel.getOutputStream();
                   in=channel.getInputStream();

                   channel.connect(3*1000);

            }
            catch(Exception e){
              System.out.println(e);
              return false;
            }
                
            return true;
        }
        public void close() {
                channel.disconnect();
        }
        public boolean run()  {
            BufferedInputStream bufin = new BufferedInputStream(in);
            int c;
            
            try {
            	StringBuilder buf=new StringBuilder(1024);
				while ((c=bufin.read()) >= 0){
					if((char)c=='\r' || (char)c=='\n') {
						String s=buf.toString();
						System.out.println(s);
						out.write('\r');
						out.write('\n');
						buf.setLength(0);
						buf.trimToSize();
					}
					else {
						 buf.append((char)c);
						 //System.out.print((char)c);
						 //System.out.println(" cod:"+c);
					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	
        	return true;
        }
          
        public static void main(String[] arg){
            boolean ret;
            
        	rShell sh=new rShell(System.in,System.out);
        	
            if(arg.length>0)sh.setHost(arg[0]);
            else            sh.setHost("127.0.0.1");
            
            if(arg.length>1)sh.setUser(arg[1]);
            else            sh.setUser("root");
            
            if(arg.length>2)sh.setPasswd(arg[2]);
            else sh.setPasswd("biglear14");
            
            ret=sh.open();
            System.out.println("open connection:"+ret); 
            ret=sh.run();            
            sh.close();
            System.out.println("close connection"); 
            
            
        }

}
