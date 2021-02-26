package org.little.rcmd.rsh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
        

/**
*  class rcopy remove file to buffer
*/
public class rCP_Remote2Buffer extends rCP_Remote2Local{

       private static Logger logger = LoggerFactory.getLogger(rCP_Remote2Buffer.class);
       private byte [] buffer;

       public rCP_Remote2Buffer(String name,int index,String _rfile,String _lfile) {
           super(name,index,_rfile,"");
           buffer=null;
           //http=_lfile;
       }

       @Override
       protected String r_command(){return "scp -f "+rfile; }

       //@Override
       //public String type() {return getClass().getName();}

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
                      
                         //  read a content of rfile
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
                         //buffer=fos.toByteArray();
                         
                         sent(fos);
                         fos.close();
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
              buffer= os.toByteArray();
              return true;
        }
	@Override
	public byte[] getBuffer() {
	      return buffer;
	}

        @Override
        public String toString(){
               return "rCP(R2H):"+name+" index:"+index+" local:"+lfile+" "+"remote:"+rfile;
        }  

          
        public static void main(String[] arg){
        }

}
