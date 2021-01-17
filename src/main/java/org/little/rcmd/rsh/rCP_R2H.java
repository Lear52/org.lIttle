package org.little.rcmd.rsh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
        

public class rCP_R2H extends rCP_R2L{

       private static Logger logger = LoggerFactory.getLogger(rCP_R2H.class);
       //private String http;
       public rCP_R2H(rShell sh,String name,int index,String _rfile,String _lfile) {
              super(sh,name,index,_rfile,"");
              //http=_lfile;
       }

       @Override
       protected String r_command(){return "scp -f "+rfile; }

       @Override
       protected boolean _run() {
                 boolean ret=false;
                 logger.debug("begin command:"+name+" index:"+index +" R2L");

              try {

                   byte[] buf=new byte[1024];
              
                   //send '\0'
                   buf[0]=0; 
                   out.write(buf, 0, 1);
                   out.flush();
      
                   while(true){
                         int c=checkAck(getIN());
                         if(c!='C'){
                            break;
                         }
                   
                         // read '0644 '
                         in.read(buf, 0, 5);
                      
                         long filesize=0L;
                         while(true){
                              if(in.read(buf, 0, 1)<0){
                                 // error
                                 break; 
                              }
                              if(buf[0]==' ')break;
                              filesize=filesize*10L+(long)(buf[0]-'0');
                         }
                      
                         String file=null;
                   
                         for(int i=0;;i++){
                             in.read(buf, i, 1);
                             if(buf[i]==(byte)0x0a){
                                file=new String(buf, 0, i);
                                break;
                             }
                         }
                         logger.trace("read file:"+file);
                         
                         // send '\0'
                         buf[0]=0; 
                         out.write(buf, 0, 1); 
                         out.flush();
                      
                         //  read a content of lfile
                         ByteArrayOutputStream fos = new ByteArrayOutputStream();
                         int foo;
                         while(true){
                               if(buf.length<filesize) foo=buf.length;
                               else foo=(int)filesize;
                               foo=getIN().read(buf, 0, foo);
                               if(foo<0){
                                  // error 
                                  break;
                               }
                               fos.write(buf, 0, foo);
                               filesize-=foo;
                               if(filesize==0L) break;
                         }
                         fos.close();

                         sent(fos);

                         fos=null;
                      
                         if(checkAck(getIN())!=0){
                            ret=true;
                            break;
                         }
                         else {
                             // send '\0'
                             buf[0]=0; 
                             out.write(buf, 0, 1); 
                             out.flush();
                             ret=true;
                         }
                   }
              } 
              catch (IOException e) {
                    logger.error("error ex:"+e);
                    ret=false;
              } 

              logger.debug("end command:"+name+" index:"+index +" R2S return:"+ret);

              return ret;
        }
       public boolean sent(ByteArrayOutputStream os){
              return false;

       }
       @Override
       public String toString(){
               return "rCP(R2H):"+name+" index:"+index+" local:"+lfile+" "+"remote:"+rfile;
       }  

          
        public static void main(String[] arg){
        }

}
