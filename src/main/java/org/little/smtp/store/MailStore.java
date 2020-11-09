package org.little.smtp.store;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.smtp.tool.arrbyte;
import org.little.store.ELM2lMessage;
import org.little.store.lMessageX509;
import org.little.store.lRoot;
import org.little.store.lStore;
import org.little.store.lUID;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class MailStore {

        private static final Logger logger = LoggerFactory.getLogger(MailStore.class);

        private ArrayList<arrbyte> buf;
        private StringBuilder      buffer;
        private int                count;
        private boolean            is_str=true;

       public MailStore(){
              
              if(is_str){buffer=new StringBuilder();buf=null;          }
              else{      buffer=null;buf=new ArrayList<arrbyte>(10240);}
              count=0;
        }

	public void addDataLine(ByteBuf line_without_crlf){
	       //int  len=line_without_crlf.writableBytes();
	       int  len=line_without_crlf.readableBytes();
	       byte [] b=new byte [len+2];
               if(len>0)line_without_crlf.readBytes(b,0,len);
               b[len]='\r';
               b[len+1]='\n';

               

               if(is_str)buffer.append(b);
               else{
                  buf.add(new arrbyte(b)); 
                  count+=(len+2);
               }

        }
	private byte [] get(){
                byte [] dest=new byte [count];

                count=0;

                for(int i=0;i<buf.size();i++){
                    byte[] src=buf.get(i).get();
                    System.arraycopy(src,0,dest,count,src.length);
                    count+=src.length;
                }
                //---------------------------------------------------------
                buf=new ArrayList<arrbyte>(10240);
                count=0;
                return dest;
        }

	public boolean finish(){
               //---------------------------------------------------------
               byte [] dest;
               if(is_str){dest=buffer.toString().getBytes();buffer=new StringBuilder();}
               else       dest=get();

               logger.trace("SMTP msg:\r\n"+new String(dest));
               //---------------------------------------------------------
               ByteArrayInputStream in_byte=new ByteArrayInputStream(dest);
               BufferedInputStream  in=new BufferedInputStream(in_byte);

               lMessage[] buf_message=ELM2lMessage.parse(in);

               if(buf_message==null){
                  logger.info("no SMTP msg");
                  return true;
               }
               for(int i=0;i<buf_message.length;i++){
                   if(buf_message[i]==null)continue;
                   buf_message[i]=lMessageX509.parse(buf_message[i]);
                   if(buf_message[i]==null)continue;

                   lMessage  msg  =buf_message[i];
                   String [] to   =msg.getTO();
                   String    from =msg.getFrom();
                   for(int j=0;j<to.length;j++){
                       String  store_name=to[j];
                       lStore  store     =lRoot.getStore(store_name);  if(store ==null)continue;
                       lFolder folder    =store.getInboxFolder();      if(folder==null)continue;
                       msg.setUID(lUID.get());
                       folder.save(msg);
                       folder.close();
                       store.close();
                   }
                   {
                     String  store_name=from;
                     lStore  store     =lRoot.getStore(store_name);  if(store ==null)continue;
                     lFolder folder    =store.getOutboxFolder();      if(folder==null)continue;
                     msg.setUID(lUID.get());
                     folder.save(msg);
                     folder.close();
                     store.close();

                   }

               }
               logger.trace("save EML msg to XML msg");
               return true;
        }

}
