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

public class ImapClientGetMsg {

        private static final Logger logger = LoggerFactory.getLogger(ImapClientGetMsg.class);

            Properties props;
            Store      store;
            IMAPFolder folder;
            boolean    debug   = true;
            String     userName = "av";
            String     password = "123";
            String     host     = "127.0.0.1";
            int        port    = 1143;
            
            
        public ImapClientGetMsg() {

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
        
                System.out.println("pre Session.getInstance");
                Session session = Session.getInstance(props, auth);//null
                System.out.println("post Session.getInstance");

                session.setDebug(debug);


                try {
                        // Try to initialise session with given credentials
                        store = session.getStore("imap");
                        System.out.println("client:getStore(imap)");
                        // store = session.getStore("imaps");

                        store.connect(host, port, userName, password);// 1143
                        logger.trace("client:connect "+host+","+port+","+userName+","+password);
                        System.out.println("client:connect Ok"+host+","+port+","+userName+","+password);


                        folder = (IMAPFolder) store.getFolder("inbox"); // Get the inbox
                        logger.trace("client:getFolder inbox");
                        System.out.println("client:getFolder inbox");

                        System.out.println("client:begin openFolder inbox");
                        if (!folder.isOpen())folder.open(Folder.READ_WRITE);
                        System.out.println("client:end   openFolder inbox:"+folder.toString());

                        
                        
                        System.out.println("client:count of Messages        : " + folder.getMessageCount());

                        System.out.println("client:count of Unread Messages : " + folder.getUnreadMessageCount());

                        if(folder.getMessageCount()>0) {
                        int count = 0;
                        Message messages[] = folder.getMessages();

                        // Get all messages
                        for (Message message : messages) {
                                count++;
                                try {
                                    InputStream i = message.getInputStream();
                                	int c;
                                    while ((c = i.read()) > -1) {
                                    	System.out.write(c);
                                    }
                                    i.close();
                                } catch (Exception eee) {
                                    eee.printStackTrace();
                                }

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
                                        System.out.println("console:multipart.getCount():" + multipart.getCount()+"\n");

                                        for (int x = 0; x < multipart.getCount(); x++) {
                                                System.out.println("console:multipart:"+x+"/"+multipart.getCount()+"------------------------\n");
                                                
                                                BodyPart bodyPart = multipart.getBodyPart(x);
                                                // If the part is a plan text message, then print it out.
                                                System.out.println("console:"+bodyPart.getContentType());
                                                if(x>=0){
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
                } finally {
                        try {
                                if (folder != null && folder.isOpen()) {
                                	System.out.println("client:close folder");
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
                logger.trace("client:Set java property:java.net.preferIPv4Stack=true");
                ImapClientGetMsg cln = new ImapClientGetMsg();

                cln.run();

        }
}
