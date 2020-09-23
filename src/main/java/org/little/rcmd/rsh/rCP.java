package org.little.rcmd.rsh;

import java.io.IOException;
import java.io.InputStream;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
        

public class rCP extends r_ssh{
       private static Logger logger = LoggerFactory.getLogger(rCP.class);
       protected String rfile;
       protected String lfile;

       protected rCP() {
           set(null,null);
       }
       protected rCP(String _rfile,String _lfile) {
              set(_rfile,_lfile);
       }
       protected String r_command(){ return null; }

       protected boolean openChannel() throws JSchException {
               if(r_command()==null)return false;
               Channel channel=session.openChannel("exec");
               ((ChannelExec)channel).setCommand(r_command());
               return true;
       }
       public void set(String rfile,String lfile) {
           this.rfile=rfile;
           this.lfile=lfile;
       }
       public boolean run() {
    	   logger.error("run abstract class");
    	   return false;
       }

       protected int checkAck(InputStream in) throws IOException{
                int b=in.read();
                // b may be 0 for success,
                //          1 for error,
                //          2 for fatal error,
                //          -1
                if(b== 0) return b;
                if(b==-1) return b;
               
                if(b==1 || b==2){
                   StringBuffer sb=new StringBuffer();
                   int c;
                   do {
                       c=in.read();
                       sb.append((char)c);
                   }while(c!='\n');
                  
                   if(b==1){ // error
                      rlog.print(sb.toString()+"\n");
                   }
                   if(b==2){ // fatal error
                      rlog.print(sb.toString()+"\n");
                   }
                }
                return b;
        }
          
}
