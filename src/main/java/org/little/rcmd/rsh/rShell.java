package org.little.rcmd.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;

public class rShell  extends r_ssh{
       private static Logger logger = LoggerFactory.getLogger(rShell.class);
       
       public rShell() {}

       protected boolean openChannel() throws JSchException {
               channel=session.openChannel("shell");
               return true;
       }
       public  boolean open() {
               try{
                   session=jsch.getSession(getUser(), getHost(), 22);
                   session.setPassword(getPasswd());
                  
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
                  
                   openChannel();
                  
                   out=channel.getOutputStream();
                   in =channel.getInputStream();
                  
                   channel.connect(3*1000);
               }
               catch(Exception e){
          	  logger.error("error ex:"+e);
                     return false;
               }
                
               return true;
       }
       public void close() {
              channel.disconnect();
              session.disconnect();
       }

       public boolean run()  {
              BufferedInputStream bufin = new BufferedInputStream(in);
              int c;
              
              try {
                   StringBuilder buf=new StringBuilder(1024);
                   while ((c=bufin.read()) >= 0){
                          if((char)c=='\r' || (char)c=='\n'|| (char)c=='#') {
                             String s=buf.toString();
                             System.out.println(s);
                             out.write('\r');
                             out.write('\n');
                             buf.setLength(0);
                             buf.trimToSize();
                          }
                          else {
                             buf.append((char)c);
                          }
                   }
                              
              } catch (IOException e) {
            	  logger.error("error ex:"+e);
              }
           
              return true;
       }
         
       public static void main(String[] arg){
              boolean ret;
              
              rShell sh=new rShell();
                 
              if(arg.length>0)sh.setHost(arg[0]);
              else            sh.setHost("127.0.0.1");
              
              if(arg.length>1)sh.setUser(arg[1]);
              else            sh.setUser("adm");
              
              if(arg.length>2)sh.setPasswd(arg[2]);
              else sh.setPasswd("2wsxXSW@");
              
              ret=sh.open();
              System.out.println("open connection:"+ret); 
             
              ret=sh.run();            
             
              sh.close();
              System.out.println("close connection"); 
           
           
       }

}
