package org.little.mailx;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.little.http.HttpX509Server;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SmtpCLN {
    private static final Logger  logger = LoggerFactory.getLogger(SmtpCLN.class);


       private String to       ;
       private String from     ;
       private String host     ;
       private String userName ;
       private String password ;
       private String subject  ;
       private String msgText  ;
       private boolean debug   ;

    	public SmtpCLN(){
              debug   = true;
              to       ="";
              from     ="";
              host     ="";
              userName ="";
              password ="";
              subject  ="";
              msgText  ="";
       }
       public void setTo(String to) {
              this.to = to;
       }
       public void setFrom(String from) {
              this.from = from;
       }
       public void setHost(String host) {
              this.host = host;
       }
       public void setUserName(String userName) {
              this.userName = userName;
       }
       public void setPassword(String password) {
              this.password = password;
       }
       public void setSubject(String subject) {
              this.subject = subject;
       }
       public void setMsgText(String msgText) {
              this.msgText = msgText;
       }
       public void setDebug(boolean debug) {
		     this.debug = debug;
       }
       public void sent(String filename){
    	   ByteArrayOutputStream out=new ByteArrayOutputStream();
           FileInputStream in;
           try {
			   in = new FileInputStream(filename);
		   } catch (FileNotFoundException e) {
			   logger.error("ex:"+e);
			   return;
		   }
           

           byte[] buf = new byte[512];
           int count;
           try {
			while ((count = in.read(buf)) > 0) {
			       out.write(buf, 0, count);
			   }
           } catch (IOException e) {
 			   logger.error("ex:"+e);
			   return;
		   }
    	   sent(out,filename);
       }
       public void sent(ByteArrayOutputStream os,String filename){
    	      byte [] buffer2= os.toByteArray();
    	      sent(buffer2,filename);
       }
       public void sent(byte [] buffer2,String filename){
    	   
              System.setProperty("java.net.preferIPv4Stack","true");
              Properties props = System.getProperties();
              props.put("mail.smtp.user", from);
              props.put("mail.smtp.auth", true);
              props.put("mail.smtp.auth.mechanisms", "LOGIN"); //"LOGIN PLAIN DIGEST-MD5 NTLM"
              props.put("mail.smtp.host", host);
              props.put("mail.smtp.port", "2525");
              Authenticator auth=new Authenticator() {
                 @Override
                 protected PasswordAuthentication getPasswordAuthentication() {
                           return new PasswordAuthentication(userName,password);
                 }
              };
             
              Session session = Session.getInstance(props, auth);//null
             
              session.setDebug(debug);
             
              try {
                  MimeMessage msg = new MimeMessage(session);
                  msg.setFrom(new InternetAddress(from));
                  InternetAddress[] address = {new InternetAddress(to)};
                  msg.setRecipients(Message.RecipientType.TO, address);
                  msg.setSubject(subject);
             
                  MimeBodyPart mbp1;
                  mbp1 = new MimeBodyPart();
                  DataHandler dh1=new DataHandler(msgText,"text/plain");
                  mbp1.setDataHandler(dh1);
                     
                  MimeBodyPart mbp2;
                  mbp2 = new MimeBodyPart();
                  DataHandler dh2=new DataHandler(buffer2,"application/octet-stream");
                  mbp2.setDataHandler(dh2);
                  mbp2.setFileName(filename);
                  mbp2.setDisposition("attachment");
             
                  Multipart mp = new MimeMultipart();
                  mp.addBodyPart(mbp1);
                  mp.addBodyPart(mbp2);
                  msg.setContent(mp);
                  msg.setSentDate(new Date());
                  //System.out.println("pre send msg");
                  Transport.send(msg,userName,password);
                  //System.out.println("post send msg");
                  
              } catch (MessagingException mex) {
                       mex.printStackTrace();
                       Exception ex = null;
                       if((ex = mex.getNextException()) != null) {
                           ex.printStackTrace();
                       }
              }
              
              System.out.println("ok!");
              
       }
       public static void main(String[] args) {


       }
}
