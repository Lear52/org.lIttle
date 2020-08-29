package org.little.smtp.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.little.store.lFolder;
import org.little.store.lMessage;
import org.little.store.ELM2lMessage;
import org.little.store.lMessageX509;
import org.little.store.lRoot;
import org.little.store.lStore;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class MailDB {

        private static final Logger logger = LoggerFactory.getLogger(MailDB.class);

        private ArrayList<arrbyte> buf;
        private int                count;

       public MailDB(){
              buf=new ArrayList<arrbyte>(10240);
              count=0;
        }

	public void addDataLine(ByteBuf lineWithoutCRLF){
	       //int  len=lineWithoutCRLF.writableBytes();
	       int  len=lineWithoutCRLF.readableBytes();
	       byte [] b=new byte [len+2];
               if(len>0)lineWithoutCRLF.readBytes(b,0,len);
               b[len]='\r';
               b[len+1]='\n';

               buf.add(new arrbyte(b)); 
               count+=(len+2);
        }

	public boolean finish(){
               byte [] dest=new byte [count];
               count=0;
               for(int i=0;i<buf.size();i++){
                   byte[] src=buf.get(i).get();
                   System.arraycopy(src,0,dest,count,src.length);
                   count+=src.length;
               }
               buf=new ArrayList<arrbyte>(10240);
               count=0;
               ByteArrayInputStream in_byte=new ByteArrayInputStream(dest);
               BufferedInputStream  in=new BufferedInputStream(in_byte);

               lMessage[] buf_message=ELM2lMessage.parse(in);
               for(int i=0;i<buf_message.length;i++){
                   buf_message[i]=lMessageX509.parse(buf_message[i]);
                   if(buf_message[i]==null)continue;

                   lMessage  msg  =buf_message[i];
                   String [] to   =msg.getTO();
                   String    from =msg.getFrom();
                   for(int j=0;j<to.length;j++){
                       String  store_name=to[j];
                       lStore  store     =lRoot.getStore(store_name);  if(store ==null)continue;
                       lFolder folder    =store.getInboxFolder();      if(folder==null)continue;
                       folder.save(msg);
                   }
                   {
                     String  store_name=from;
                     lStore  store     =lRoot.getStore(store_name);  if(store ==null)continue;
                     lFolder folder    =store.getOutboxFolder();      if(folder==null)continue;
                     folder.save(msg);

                   }

               }
               logger.trace("save EML msg to XML msg");
               return true;
        }

}
