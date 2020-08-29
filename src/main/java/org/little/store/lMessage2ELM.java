package org.little.store;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

 

public class lMessage2ELM{
   
    public static  void parse(lMessage msg,String filename) {
           try{
               OutputStream out=new FileOutputStream(filename);
               parse(msg,out);
           }
           catch(Exception e){ 
               e.printStackTrace(System.out);
           }

    }
    public  static void  parse(lMessage msg,OutputStream out){
           MimeMessage eml;

           Properties props = System.getProperties();
           Session session = Session.getInstance(props, null);
           try{
               eml = new MimeMessage(session);
               eml.setFrom(msg.getFrom());
               eml.setSubject(msg.getSubject());
               eml.setSentDate(msg.getSentDate());
               String[] to=msg.getTO();
               for(int i=0;i<to.length;i++){
	         eml.addRecipients(Message.RecipientType.TO, to[i]);
	       }
	       byte [] buffer=msg.getBodyBin();
               MimeBodyPart mbp2;
               //if(false){
              //    ByteArrayInputStream is=new ByteArrayInputStream(buffer);
               //   mbp2 = new MimeBodyPart(is);
               //}
               //else{
               mbp2 = new MimeBodyPart();
               DataHandler dh=new DataHandler(buffer,"application/octet-stream");
               mbp2.setDataHandler(dh);
               //}
               mbp2.setFileName(msg.getFilename());
               mbp2.setDisposition("attachment");
               Multipart mp = new MimeMultipart();
	       mp.addBodyPart(mbp2);
	       eml.setContent(mp);
	       eml.writeTo(out);

           }
           catch(Exception e){ 
               e.printStackTrace(System.out);
           }

           try {
               out.flush();     
               out.close();
           } catch (Exception e) {
             e.printStackTrace(System.out);
           }

    }


}

