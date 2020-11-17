package org.little.mailx;

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
	   //String to2      = "iap";
	   String from     = "av";
	   String host     = "127.0.0.1";
	   int    port     = 2525;
	   String filename = args[0];
	   boolean debug   = true;
           String userName = "av";
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
	

     SmtpCLN cln=new SmtpCLN();
     cln.setTo(to1);
     cln.setFrom(from);
     cln.setHost(host);
     cln.setPort(port);
     cln.setSubject(subject);
     cln.setMsgText(msgText1);
     cln.setUserName(userName);
     cln.setPassword(password);
     cln.setDebug(debug);
     cln.sent(filename);
     System.out.println("ok!");

    }
}
