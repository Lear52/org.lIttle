package org.little.rcmd.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

//import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
        
        

public class rCP extends rShell  implements rCMD {
       private static Logger logger = LoggerFactory.getLogger(rCP.class);

       protected String rfile;
       protected String lfile;
       protected int    index;
       protected String name;

       protected rCP() {
           set(null,null);
       }
       protected rCP(String _rfile,String _lfile) {
              set(_rfile,_lfile);
       }
       protected rCP(String name,int index,String _rfile,String _lfile) {
           set(_rfile,_lfile);

           setHost  ("");
           setUser  ("");
           setPasswd("");

           this.name=name;
           this.index=index;
       }

       @Override
       public String type() {return getClass().getName();}

       @Override
       public String [] print() {return str_null;}
       
       protected String r_command(){ return null; }

       @Override
       protected  boolean _open_session() {
                  boolean ret=super._open_session();
                  logger.trace("command:"+name+" index:"+index+" _open_session:"+ret);
                  return ret;
       }
       @Override
       protected  boolean _open_channel() {
           try{
               if(r_command()==null)return false;

               channel=session.openChannel("exec");
               ((ChannelExec)channel).setCommand(r_command());

               out=channel.getOutputStream();
               in =channel.getInputStream();

               channel.connect(3*1000);

               if(out==null || in==null){
                 logger.error("command:"+name+" index:"+index+" error open channel Stream");
                 return false;
               }
           }
           catch(Exception e){
                 logger.error("command:"+name+" index:"+index+" error ex:"+e);
                 return false;
           }
           logger.trace("open_channel command:"+name+" index:"+index+" _open_channel:true");
           return true;
       }
       public void set(String rfile,String lfile) {
           this.rfile=rfile;
           this.lfile=lfile;
       }
       protected boolean _run() {
               logger.error("run abstract class");
               return false;
       }
       @Override
       public boolean run(rShell sh,BufferedInputStream bufin) {
           setHost  (sh.getHost());
           setUser  (sh.getUser());
           setPasswd(sh.getPasswd());
           
           if(!_open_session())return false; 
           if(!_open_channel())return false; 

           boolean ret=_run();

           _close();
           return ret;
       }

       //@Override
       //public boolean run(rShell sh) {
       //       setHost  (sh.getHost());
       //       setUser  (sh.getUser());
       //       setPasswd(sh.getPasswd());
       //       
       //       if(!_open_session())return false; 
       //       if(!_open_channel())return false; 
       //
       //       boolean ret=_run();
       //
       //       _close();
       //       return ret;
       //}
       @Override
       public  boolean open() {
               return true;
       }
       @Override
       public  void close() {
               return ;
       }
       @Override
       public String toString(){
               return "rCP:"+name+" index:"+index+" local:"+lfile+" "+"remote:"+rfile;
       }  

       protected int checkAck(InputStream in) throws IOException{

                int b=in.read();
                // b may be 0 for success,
                //          1 for error,
                //          2 for fatal error,
                //          -1
                if(b== 0) {
                   logger.error("checkAck command:"+name+" index:"+index+" return:"+b);
                   return b;
                }
                if(b==-1){
                   logger.error("checkAck command:"+name+" index:"+index+" return:"+b);
                    return b;
                }
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
	@Override
	public byte[] getBuffer() {
		      // TODO Auto-generated method stub
		      return null;
	}
	@Override
	public void setBuffer(byte[] buffer) {
		      // TODO Auto-generated method stub
        }

          
}
