package org.little.rcmd.test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
        
public class ScpTo{
       public static void main(String[] arg){
              if(arg.length!=2){
                System.err.println("usage: java ScpTo file1 user@remotehost:file2");
                System.exit(-1);
              }      
     
              FileInputStream fis=null;
              try{
             
                   String lfile=arg[0];
                   String user=arg[1].substring(0, arg[1].indexOf('@'));
                   arg[1]=arg[1].substring(arg[1].indexOf('@')+1);
                   String host=arg[1].substring(0, arg[1].indexOf(':'));
                   String rfile=arg[1].substring(arg[1].indexOf(':')+1);
                  
                   JSch jsch=new JSch();
                   Session session=jsch.getSession(user, host, 22);
                  
                   // username and password will be given via UserInfo interface.
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
                   session.connect();
                  
                   boolean ptimestamp = true;
                  
                   // exec 'scp -t rfile' remotely
                   rfile=rfile.replace("'", "'\"'\"'");
                   rfile="'"+rfile+"'";
                   String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
                   Channel channel=session.openChannel("exec");
                   ((ChannelExec)channel).setCommand(command);
                  
                   // get I/O streams for remote scp
                   OutputStream out=channel.getOutputStream();
                   InputStream in=channel.getInputStream();
                  
                   channel.connect();
                  
                   if(checkAck(in)!=0){
                      System.exit(0);
                   }
                  
                   File _lfile = new File(lfile);
                  
                   if(ptimestamp){
                     command="T "+(_lfile.lastModified()/1000)+" 0";
                     // The access time should be sent here,
                     // but it is not accessible with JavaAPI ;-<
                     command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
                     out.write(command.getBytes()); out.flush();
                     if(checkAck(in)!=0){
                         System.exit(0);
                     }
                   }
                  
                   // send "C0644 filesize filename", where filename should not include '/'
                   long filesize=_lfile.length();
                   command="C0644 "+filesize+" ";
                   if(lfile.lastIndexOf('/')>0){
                     command+=lfile.substring(lfile.lastIndexOf('/')+1);
                   }
                   else{
                     command+=lfile;
                   }
                   command+="\n";
                   out.write(command.getBytes()); out.flush();
                   if(checkAck(in)!=0){
                      System.exit(0);
                   }
                  
                   // send a content of lfile
                   fis=new FileInputStream(lfile);
                   byte[] buf=new byte[1024];
                   while(true){
                     int len=fis.read(buf, 0, buf.length);
                     if(len<=0) break;
                     out.write(buf, 0, len); //out.flush();
                   }
                   fis.close();
                   fis=null;
                   // send '\0'
                   buf[0]=0; out.write(buf, 0, 1); out.flush();
                   if(checkAck(in)!=0){
                      System.exit(0);
                   }
                   out.close();
                  
                   channel.disconnect();
                   session.disconnect();
                  
                   System.exit(0);
              }
              catch(Exception e){
                System.out.println(e);
                try{if(fis!=null)fis.close();}catch(Exception ee){}
              }
       }
     
       static int checkAck(InputStream in) throws IOException{
              int b=in.read();
              if(b==0) return b;
              if(b==-1) return b;
     
              if(b==1 || b==2){
                 StringBuffer sb=new StringBuffer();
                 int c;
                 do {
                     c=in.read();
                     sb.append((char)c);
                 } while(c!='\n');
                 if(b==1){ // error
                    System.out.print(sb.toString());
                 }
                 if(b==2){ // fatal error
                    System.out.print(sb.toString());
                 }
              }
              return b;
       }
     
}
