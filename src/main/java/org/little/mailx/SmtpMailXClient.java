package org.little.mailx;
        
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class SmtpMailXClient {
    private static final Logger  logger = LoggerFactory.getLogger(SmtpMailXClient.class);

       private String  to       ;
       private String  from     ;
       private String  host     ;
       private int     port     ;
       private String  userName ;
       private String  password ;
       private String  subject  ;
       private String  msgText  ;
       private boolean debug   ;
       private boolean is_auth ;

       public SmtpMailXClient(){
              debug   = true;
              to       ="";
              from     ="";
              host     ="127.0.0.1";
              port     =25;
              userName ="";
              password ="";
              subject  ="";
              msgText  ="";
              is_auth  =true;
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
       public void setPort(int _port) {
           this.port = _port;
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
       public void setAuth(boolean auth) {
              this.is_auth = auth;
       }
       @SuppressWarnings("resource")
       public void sent(String filename){
              ByteArrayOutputStream out=new ByteArrayOutputStream();
              FileInputStream       in =null;
              try {
                   in = new FileInputStream(filename);
              } catch (Exception e) {
                try {in.close();}catch(Exception e1){;}
                try {out.close();}catch(Exception e2){}
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
              props.put("mail.smtp.host", host);
              props.put("mail.smtp.port", ""+port);

              Session session;
              if(is_auth){
                 System.out.println("mail.smtp.auth:"+is_auth);
                 props.put("mail.smtp.user", userName);
                 props.put("mail.smtp.auth", true);
                 props.put("mail.smtp.auth.mechanisms", "LOGIN"); //"LOGIN PLAIN DIGEST-MD5 NTLM"
                 Authenticator auth=new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                              return new PasswordAuthentication(userName,password);
                    }
                 };
                 session = Session.getInstance(props, auth);
              }
              else{
                 System.out.println("mail.smtp.auth:"+is_auth);
                 props.put("mail.smtp.auth", false);
                 session = Session.getInstance(props, null);
              }
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
              
              //System.out.println("ok!");
              
       }


}
