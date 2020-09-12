package org.little.smtp;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
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

/**
 * sendfile will create a multipart message with the second
 * block of the message being the given file.<p>
 *
 * This demonstrates how to use the FileDataSource to send
 * a file via mail.<p>
 *
 * usage: <code>java sendfile <i>to from smtp file true|false</i></code>
 * where <i>to</i> and <i>from</i> are the destination and
 * origin email addresses, respectively, and <i>smtp</i>
 * is the hostname of the machine that has smtp server
 * running.  <i>file</i> is the file to send. The next parameter
 * either turns on or turns off debugging during sending.
 *
 * @author	Christopher Cotton
 */
public class sendfile {

    public static void main(String[] args) {

        System.setProperty("java.net.preferIPv4Stack","true");

	if (args.length < 1) {
	    System.out.println("usage: java sendfile ");
	    System.exit(1);
	}

	String to1      = "av";
	String to2      = "iap";
	String from     = "av";
	String host     = "127.0.0.1";
	String filename = args[0];
	boolean debug   = true;
        String userName = from;
        String password = "123";

	//String msgText1 = "text:Sending a file.\n";
	//String subject = "subject:Sending a file";
	String subject = "CN=92svc-CA-test, OU=PKI, OU=Tatarstan, DC=region, DC=cbr, DC=ru";
	String msgText1 = ""
+"CERTIFICATE\n"
+"DER\n"
+"Thu Feb 07 12:56:45 MSK 2019\n"
+"Sat May 05 02:59:00 MSK 2035\n"
+"85486455983605724770238770388276084954\n"
+"CN=92svc-CA-test, OU=PKI, OU=Tatarstan, DC=region, DC=cbr, DC=ru\n"
+"CN=ROOTsvc-CA-test, OU=GUBZI, OU=PKI, DC=region, DC=cbr, DC=ru\n"
;
	
	// create some properties and get the default Session
	Properties props = System.getProperties();
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.auth", true);
        //props.put("mail.smtp.auth.mechanisms", "LOGIN"); //"LOGIN PLAIN DIGEST-MD5 NTLM"
        props.put("mail.smtp.auth.mechanisms", "PLAIN"); //"LOGIN PLAIN DIGEST-MD5 NTLM"
	props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "2525");
        Authenticator auth=new Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
              //System.out.println(" requestingSite:"+requestingSite+" requestingPort:"+requestingPort+" requestingProtocol:"+requestingProtocol+" requestingPrompt:"+requestingPrompt+" requestingUserName:"+requestingUserName  );
              System.out.println("l:p    "+userName+":"+password);

                        return new PasswordAuthentication(userName,password);
              }
        };
	
        System.out.println("pre Session.getInstance");
	Session session = Session.getInstance(props, auth);//null
        System.out.println("post Session.getInstance");
	session.setDebug(debug);
        System.out.println("setDebug(debug)");
	
	try {
	    // create a message
	    MimeMessage msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(from));
	    InternetAddress[] address = {new InternetAddress(to1),new InternetAddress(to2)};
	    msg.setRecipients(Message.RecipientType.TO, address);
	    msg.setSubject(subject);

	    // create and fill the first message part
	    MimeBodyPart mbp1 = new MimeBodyPart();
	    mbp1.setText(msgText1);

	    // create the second message part
	    MimeBodyPart mbp2 = new MimeBodyPart();

	    // attach the file to the message
	    mbp2.attachFile(filename);

	    /*
	     * Use the following approach instead of the above line if
	     * you want to control the MIME type of the attached file.
	     * Normally you should never need to do this.
	     *
	    FileDataSource fds = new FileDataSource(filename) {
		public String getContentType() {
		    return "application/octet-stream";
		}
	    };
	    mbp2.setDataHandler(new DataHandler(fds));
	    mbp2.setFileName(fds.getName());
	     */

	    // create the Multipart and add its parts to it
	    Multipart mp = new MimeMultipart();
	    mp.addBodyPart(mbp1);
	    mp.addBodyPart(mbp2);

	    // add the Multipart to the message
	    msg.setContent(mp);

	    // set the Date: header
	    msg.setSentDate(new Date());

	    /*
	     * If you want to control the Content-Transfer-Encoding
	     * of the attached file, do the following.  Normally you
	     * should never need to do this.
	     *
	    msg.saveChanges();
	    mbp2.setHeader("Content-Transfer-Encoding", "base64");
	     */

            System.out.println("pre send msg");
	    // send the message
	    Transport.send(msg,userName,password);

            System.out.println("post send msg");
	    
	} catch (MessagingException mex) {
	    mex.printStackTrace();
	    Exception ex = null;
	    if ((ex = mex.getNextException()) != null) {
		ex.printStackTrace();
	    }
	} catch (IOException ioex) {
	    ioex.printStackTrace();
	}
        System.out.println("ok!");

    }
}
