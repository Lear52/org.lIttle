package org.little.mailx;

import java.util.Date;
//import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;

public class ImapClientPutMsg {

        private static final Logger logger = LoggerFactory.getLogger(ImapClientGetMsg0.class);

        Properties props;
        Store store;
        IMAPFolder  folder;
        boolean     debug    = true;
        String      userName = "av";
        String      password = "123";
        String      host     = "127.0.0.1";
        int         port     = 1143;//143;
            
            
        public ImapClientPutMsg() {
                store = null;
                folder = null;
        }

        /**
         * Returns the folder containing the mail in the inbox
         *
         * @return IMAPFolder
         */
        public IMAPFolder getMailFolder() {
                return folder;
        }

        /**
         * 
         * Initialises the IMAP session, creates the store for storing and retrieving
         * messages. Populates an IMAPFolder with the inbox content.
         * 
         */
        public void run(String filename) {

                props = System.getProperties();
                /*
                 * if(false){ Enumeration en = prop.propertyNames();
                 * while(en.hasMoreElements()){ String s=(String)en.nextElement();
                 * logger.trace(s+":" +prop.getProperty(s)); } }
                 */
                props.setProperty("mail.debug"               , "true");
                props.setProperty("mail.imap.ssl.enable"     , "false");
                props.setProperty("mail.imap.starttls.enable", "false");
                props.setProperty("mail.imap.sasl.enable"    , "false");
                props.setProperty("mail.debug.auth.username" , "true");
                props.setProperty("mail.debug.auth.password" , "true");
                props.setProperty("mail.socket.debug"        , "true");
                Authenticator auth=new Authenticator() {
                          @Override
                          protected PasswordAuthentication getPasswordAuthentication() {
                                  System.out.println("l:p    "+userName+":"+password);
                          return new PasswordAuthentication(userName,password);
                          }
                };
        
                System.out.println("console:pre Session.getInstance");
                Session session = Session.getInstance(props, auth);//null
                System.out.println("console:post Session.getInstance");

                session.setDebug(debug);


                try {
                        // Try to initialise session with given credentials
                        System.out.println("console:BEGIN");
                        store = session.getStore("imap");
                        System.out.println("console:getStore(imap)");
                        // store = session.getStore("imaps");

                        logger.trace("connect to:"+host+"("+port+") "+userName+":"+password);
                        store.connect(host, port, userName, password);// 1143
                        logger.trace("connect "+host+","+port+","+userName+","+password);
                        System.out.println("console:connect Ok "+host+","+port+","+userName+","+password);


                        folder = (IMAPFolder) store.getFolder("inbox"); // Get the inbox
                        logger.trace("getFolder inbox");
                        System.out.println("console:getFolder inbox");

                        if (!folder.isOpen())folder.open(Folder.READ_WRITE);
                        System.out.println("console:openFolder inbox");
                        System.out.println("console:No of Messages : "        + folder.getMessageCount());
                        System.out.println("console:No of Unread Messages : " + folder.getUnreadMessageCount());

	                // create a message
	                String to1      = "av@vip.cbr.ru";
	                String to2      = "iap@vip.cbr.ru";
	                String from     = "av@vip.cbr.ru";
	                String msgText1 = ""
                                        +"CERTIFICATE\n"
                                        +"DER\n"
                                        +"Thu Feb 07 12:56:45 MSK 2019\n"
                                        +"Sat May 05 02:59:00 MSK 2035\n"
                                        +"85486455983605724770238770388276084954\n"
                                        +"CN=92svc-CA-test, OU=PKI, OU=Tatarstan, DC=region, DC=cbr, DC=ru\n"
                                        +"CN=ROOTsvc-CA-test, OU=GUBZI, OU=PKI, DC=region, DC=cbr, DC=ru\n"
                                        ;

	                MimeMessage msg = new MimeMessage(session);
	                msg.setFrom(new InternetAddress(from));
	                InternetAddress[] address = {new InternetAddress(to1),new InternetAddress(to2)};
	                msg.setRecipients(Message.RecipientType.TO, address);
	                msg.setSubject("append message");
	                MimeBodyPart mbp1 = new MimeBodyPart();
	                mbp1.setText(msgText1);
	                MimeBodyPart mbp2 = new MimeBodyPart();
	                mbp2.attachFile(filename);
	                Multipart mp = new MimeMultipart();
	                mp.addBodyPart(mbp1);
	                mp.addBodyPart(mbp2);
	                msg.setContent(mp);
	                msg.setSentDate(new Date());

	                MimeMessage [] msgs=new MimeMessage[1];
	                msgs[0]=msg;
	                folder.appendMessages(msgs);

                } catch (Exception e) {
                        Except ex=new Except(e);
                        logger.error("ex:" + ex);
                        e.printStackTrace();
                        // System.exit(0);
                } finally {
                        try {
                                if (folder != null && folder.isOpen()) {
                                        folder.close(true);
                                }
                                if (store != null) {
                                        store.close();
                                }
                        } catch (Exception e) {
                        }
                }
        }

        public static void main(String[] args) {

                System.setProperty("java.net.preferIPv4Stack", "true");
                logger.trace("Set java property:java.net.preferIPv4Stack=true");
                ImapClientPutMsg cln = new ImapClientPutMsg();

                cln.run(args[0]);

        }
}
