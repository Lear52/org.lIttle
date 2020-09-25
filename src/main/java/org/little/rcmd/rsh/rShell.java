package org.little.rcmd.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class rShell{
       private static Logger logger = LoggerFactory.getLogger(rShell.class);
       protected JSch         jsch;
       protected Session      session;
       protected Channel      channel;
                          
       private String         host;
       private String         user;
       private String         passwd;

       protected InputStream  in;
       protected OutputStream out;
       
       public rShell() {
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

       protected  boolean _open_session() {
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
                  }
                  catch(Exception e){
                              logger.error("error ex:"+e);
                        return false;
                  }
                   
                  return true;
       }
       protected  boolean _open_channel() {
                  try{
                      channel=session.openChannel("shell");
                      out=channel.getOutputStream();
                      in =channel.getInputStream();
                      channel.connect(3*1000);
                  }
                  catch(Exception e){
                        logger.error("error open channel ex:"+e);
                        return false;
                  }
                  return true;
       }
       protected void _close(){
                 channel.disconnect();
                 session.disconnect();
       }

       public  boolean open() {
               if(!_open_session())return false; 
               if(!_open_channel())return false; 
               return true;
       }
       public void close(){ _close();}
       
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
