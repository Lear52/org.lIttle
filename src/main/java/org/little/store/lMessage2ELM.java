package org.little.store;

//import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
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

//import org.little.imap.commonIMAP;

 

public class lMessage2ELM{
   
    public static  void parse(lMessage msg,String filename,String _domain) {
           try{
               OutputStream out=new FileOutputStream(filename);
               parse(msg,out,_domain);
           }
           catch(Exception e){ 
               e.printStackTrace(System.out);
           }

    }
    public  static String parse(lMessage msg,String _domain){
    	ByteArrayOutputStream out=new ByteArrayOutputStream();
    	parse(msg,out,_domain);
    	
    	return out.toString();
    }
    public  static void  parse(lMessage msg,OutputStream out,String _domain){
           MimeMessage eml;

           Properties props = System.getProperties();
           Session session = Session.getInstance(props, null);
           //String default_domen=commonIMAP.get().getDefaultDomain();
           String default_domen=_domain;
           default_domen="@"+default_domen;
           try{
               eml = new MimeMessage(session);
               eml.setFrom(msg.getFrom()+default_domen);
               eml.setSubject(msg.getSubject());
               eml.setSentDate(msg.getSentDate());
               //eml.
               
               String[] to=msg.getTO();
               for(int i=0;i<to.length;i++){
	           eml.addRecipients(Message.RecipientType.TO, to[i]+default_domen);
               }

               String buffer1=msg.getBodyTxt();
               MimeBodyPart mbp1;
               mbp1 = new MimeBodyPart();
               DataHandler dh1=new DataHandler(buffer1,"text/plain");
               mbp1.setDataHandler(dh1);
               
               byte [] buffer2=msg.getBodyBin();
               MimeBodyPart mbp2;
               mbp2 = new MimeBodyPart();
               DataHandler dh2=new DataHandler(buffer2,"application/octet-stream");
               mbp2.setDataHandler(dh2);
               mbp2.setFileName(msg.getFilename());
               mbp2.setDisposition("attachment");

               Multipart mp = new MimeMultipart();

               mp.addBodyPart(mbp1);
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

