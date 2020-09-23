package org.little.rcmd.rsh;

import java.io.InputStream;
import java.io.OutputStream;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class r_ssh{

       private static Logger logger = LoggerFactory.getLogger(r_ssh.class);
       protected JSch         jsch;
       protected Session      session;
       protected Channel      channel;
                          
       private String         host;
       private String         user;
       private String         passwd;

       protected InputStream  in;
       protected OutputStream out;
       
       public r_ssh() {
              clear();
       }
       private void clear() {
               jsch=new JSch();
               session=null;
               host=null;
               user=null;
               passwd=null;
       }
       public String       getHost  () {return host;}
       public String       getUser  () {return user;}
       public String       getPasswd() {        return passwd;        }
       public void         setHost  (String host) {this.host = host;}
       public void         setUser  (String user) {this.user = user;        }
       public void         setPasswd(String passwd) {this.passwd = passwd;        }

       public InputStream  getIN    (){return in;  }
       public OutputStream getOUT   (){return out;}

       protected boolean openChannel() throws JSchException { return false; }

       public  boolean open() {
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
                   in =channel.getInputStream();
                  
                   channel.connect(3*1000);
               }
               catch(Exception e){
          	  logger.error("error ex:"+e);
                     return false;
               }
                
               return true;
       }
       public void close(){
              channel.disconnect();
              session.disconnect();
       }

       public boolean run(){ 
              logger.error("run abstract class");
    	      return false;
       }
         

}
