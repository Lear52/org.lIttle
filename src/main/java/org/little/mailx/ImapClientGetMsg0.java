package org.little.mailx;

import java.io.FileOutputStream;
import java.io.InputStream;
//import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.Except;


import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import com.sun.mail.imap.IMAPFolder;

public class ImapClientGetMsg0 {

        private static final Logger logger = LoggerFactory.getLogger(ImapClientGetMsg0.class);

        Properties props;
        Store store;
        IMAPFolder  folder;
        boolean     debug    = true;
        String      userName = "av@vip.cbr.ru";
        String      password = "123";
        String      host     = "127.0.0.1";
        int         port     = 143;
            
            
        public ImapClientGetMsg0() {
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
         * @param  
         * 
         */
        public void run() {

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

                        if(folder.getMessageCount()>0) {
                        int count = 0;
                        Message messages[] = folder.getMessages();

                        // Get all messages
                        for (Message message : messages) {
                                count++;
                                Flags mes_flag = message.getFlags();
                                // Get subject of each message
                                System.out.println("console:The " + count + "th message is: " + message.getSubject());
                                // System.out.println(message.getContentType());
                                if (message.getContentType().contains("TEXT/PLAIN")) {
                                        System.out.println("console:"+message.getContent());
                                } else {
                                        // How to get parts from multiple body parts of MIME message
                                        Multipart multipart = (Multipart) message.getContent();
                                        System.out.println("console:Multipart message-----------" + multipart.getCount() + "----------------\n");

                                        for (int x = 0; x < multipart.getCount(); x++) {
                                                System.out.println("console:multipart:"+x+"/"+multipart.getCount()+"\n");
                                                
                                                BodyPart bodyPart = multipart.getBodyPart(x);
                                                // If the part is a plan text message, then print it out.
                                                System.out.println("console:"+bodyPart.getContentType());
                                                System.out.println("console:filename:"+bodyPart.getFileName());
                                                System.out.println("console:Content:"+bodyPart.getContent().toString());
                                                InputStream is = bodyPart.getInputStream();
                                                try {
                                                    FileOutputStream out = new FileOutputStream("out.file.imap");
                                                	int c;
                                                    while ((c = is.read()) > -1) {
                                                    	out.write(c);
                                                    }
                                                    is.close();
                                                    out.flush();
                                                    out.close();
                                                } catch (Exception eee) {
                                                    eee.printStackTrace();
                                                }
                                                
                                                System.out.println("\n");
                                                
                                                //if (bodyPart.getContentType().contains("TEXT/PLAIN")) {
                                                //}

                                        }
                                }

                                System.out.println("console:"+" Has this message been read?  flag:" + mes_flag.contains(Flag.SEEN));
                        }
                        }
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
                ImapClientGetMsg0 cln = new ImapClientGetMsg0();

                cln.run();

        }
}
