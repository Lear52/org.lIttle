package org.little.rcmd.rsh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
        

public class rCP_L2R extends rCP{

       private static Logger logger = LoggerFactory.getLogger(rCP_L2R.class);

       private boolean ptimestamp = false;

       public rCP_L2R() {}

       public rCP_L2R(String rfile,String lfile) {
              super(rfile,lfile);
       }
       public rCP_L2R(rShell sh,String name,int index,String _rfile,String _lfile) {
              super(sh,name,index,_rfile,_lfile);
       }

       @Override
       protected String r_command(){
                 if(ptimestamp)return "scp -p -t "+rfile;
                 else          return "scp -t "+rfile;
     }


       @Override
       protected boolean _run() {
                 boolean ret = true;
                 logger.debug("begin command:"+name+" index:"+index +" L2R");
                 try {
                     
                      // exec 'scp -t rfile' remotely
                      //rfile=rfile.replace("'", "'\"'\"'");
                      //rfile="'"+rfile+"'";
                      String command;
                     
                      if(checkAck(in)!=0){
                         logger.debug("1 command:"+name+" index:"+index +" L2R:checkAck(in)!=0 return:false");
                         return false;
                      }
                     
                      File _lfile = new File(lfile);
                     
                      if(ptimestamp){
                        command="T "+(_lfile.lastModified()/1000)+" 0";
                        // The access time should be sent here,
                        // but it is not accessible with JavaAPI ;-<
                        command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
                        out.write(command.getBytes()); 
                        out.flush();
                        if(checkAck(in)!=0){
                           logger.debug("2 command:"+name+" index:"+index +" L2R:checkAck(in)!=0 return:false");
                           return false;
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
                         logger.debug("3 command:"+name+" index:"+index +" L2R:checkAck(in)!=0 return:false");
                         return false;
                      }
                     
                      // send a content of lfile
                      InputStream fis=new FileInputStream(lfile);
                
                      byte[] buf=new byte[1024];
                      while(true){
                        int len=fis.read(buf, 0, buf.length);
                        if(len<=0) break;
                        out.write(buf, 0, len); 
                        out.flush();
                      }
                      fis.close();
                      fis=null;
                      // send '\0'
                      buf[0]=0; out.write(buf, 0, 1); out.flush();
                      if(checkAck(in)!=0){
                         ret=false;
                      }
                      else ret=true;
                
                      out.close(); 
                 } 
                 catch (IOException e) {
                       logger.error("error ex:"+e);
                       return false;
                 } 
                 logger.debug("end command:"+name+" index:"+index +" L2R return:"+ret);
                 return ret;
        }
       @Override
       public String toString(){
               return "rCP(L2R):"+name+" index:"+index+" local:"+lfile+" "+"remote:"+rfile;
       }  

          
        public static void main(String[] arg){
               boolean ret;

               rCP_L2R  cp0=new rCP_L2R("remoute_out.bin","local_out.bin");
                  
               if(arg.length>0)cp0.setHost(arg[0]);
               else            cp0.setHost("127.0.0.1");
               
               if(arg.length>1)cp0.setUser(arg[1]);
               else            cp0.setUser("adm");
               
               if(arg.length>2)cp0.setPasswd(arg[2]);
               else            cp0.setPasswd("2wsxXSW@");
               
               ret=cp0.open();System.out.println("open connection:"+ret); 
              
                             
               System.out.println("----------------"); 
               ret=cp0.run();            
               System.out.println("----------------"+ret); 
              
               cp0.close();
               System.out.println("close connection"); 
        }

}
