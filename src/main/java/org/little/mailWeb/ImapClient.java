package org.little.mailWeb;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.little.lmsg.lMessage;
import org.little.lmsg.lMessageX509;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._ByteBuilder;
import org.little.util.run.scheduler;
import org.little.util.run.task;

import com.sun.mail.imap.IMAPFolder;

public class ImapClient extends task{

        private static final Logger logger = LoggerFactory.getLogger(ImapClient.class);

        private Properties       props;
        private Store            store;
        private IMAPFolder       folder;
        private Session          session;
        private commonIMAPClient cfg;

            
        public ImapClient() {
               store    = null;
               folder   = null;
               session  = null;
               cfg=new commonIMAPClient();
        }


        public boolean loadCFG(String xpath){
               return cfg.loadCFG(xpath);
        }
        public int     getTimeout        () {return cfg.getTimeout();}

        protected void   open(){
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
                                  logger.trace("l:p    "+cfg.getUser()+":"+cfg.getPasswrd());
                                  return new PasswordAuthentication(cfg.getUser(),cfg.getPasswrd());
                          }
                };
        
                logger.trace("console:pre Session.getInstance");
                session = Session.getInstance(props, auth);//null
                logger.trace("console:post Session.getInstance");

                session.setDebug(cfg.isDebug());
                try {
                        // Try to initialise session with given credentials
                        store = session.getStore("imap");

                        logger.trace("connect to:"+cfg.getHost()+"("+cfg.getPort()+") "+cfg.getUser()+":"+cfg.getPasswrd());

                        store.connect(cfg.getHost(), cfg.getPort(), cfg.getUser(), cfg.getPasswrd());// 143

                        logger.trace("connect "+cfg.getHost()+","+cfg.getPort()+","+cfg.getUser()+","+cfg.getPasswrd());

                } catch (Exception e) {
                        Except ex=new Except(e);
                        logger.error("error open connection ex:" + ex);
                        store = null;
                        return;
                }
                try {

                        folder = (IMAPFolder) store.getFolder(cfg.getFolder()); // Get the inbox
                        logger.trace("getFolder "+cfg.getFolder());

                        if (!folder.isOpen())folder.open(Folder.READ_WRITE);
                        logger.trace("openFolder "+cfg.getFolder()+" No of Messages:"        + folder.getMessageCount()+" No of Unread Messages : " + folder.getUnreadMessageCount());

                } catch (Exception e) {
                        Except ex=new Except(e);
                        logger.error("error open folder ex:" + ex);
                        store  = null;
                        folder = null;
                        return;
                }


        }
        protected void close(){
                  try {
                       if(folder != null && folder.isOpen()) {
                          folder.close(true);
                       }
                       if(store != null) {
                          store.close();
                       }
                  } catch (Exception e) {}

        }
        private void ParsePart(BodyPart bodyPart,lMessage msg) {
            try {
                    lMessage _msg=new lMessage(msg);
               
                    _msg.setFilename(bodyPart.getFileName());
               
                    byte [] buf=null;
                    InputStream is=null;
                    _ByteBuilder b=new _ByteBuilder();
                    try {
                       is = bodyPart.getInputStream();
                       //buf=_ByteBuilder.toByte(is);
                       int c;
                       while ((c = is.read()) > -1) {
                               b.append(c);
                       }
               
                    }
                    finally { try {if(is!=null)is.close();} catch(Exception e){}}
               
                    buf=b.getBytes();
                    if(buf==null) {
                       logger.trace("buffer is null");
                       return;
                    }
                    logger.trace("buffer length:" + buf.length);
               
                    _msg.setBodyBin(buf);
                    logger.trace("letter:" + _msg);
               
                    _msg=lMessageX509.parse(_msg);
                    if(_msg!=null)logger.trace("letter x509:" + _msg);
            }
            catch(Exception e){
                  logger.error("ex:"+e);
                  return;
            }
        	
        }
        private void parseMessage(Message message,int count) {
               lMessage msg=new lMessage();
               try {
                  Flags mes_flag = message.getFlags();
                  // Get subject of each message
                  logger.trace("letter:" + count + " subject: " + message.getSubject());
              
                  Address[] addr;
                  if((addr = message.getFrom()) != null) msg.setFrom(addr[0].toString());
                  if ((addr = message.getAllRecipients()) != null) for (int j = 0; j < addr.length; j++)msg.addTO(addr[j].toString());
                  msg.setSubject   (message.getSubject());
                  msg.setCreateDate(message.getSentDate());
                  msg.setSentDate  (message.getSentDate());
                  String _id=null;
                  if( message instanceof MimeMessage)_id = ((MimeMessage)message).getMessageID();
                  if(_id==null) {_id=lMessage.getNewID(msg.getFrom());}
                  msg.setId(_id);
                  msg.setReceiveDate(new Date());
                  if(message.getContentType().contains("TEXT/PLAIN")){
                      logger.trace("letter:" + count + " subject: " + message.getSubject()+" text only");
                      return;
                   } 
               }
               catch(Exception e){
                     logger.error("ex:"+e);
                     return;
               }
               //
               try {
                   Multipart multipart = (Multipart) message.getContent();
                   for(int x = 0; x < multipart.getCount(); x++){
                       BodyPart bodyPart = multipart.getBodyPart(x);
                  
                       if(bodyPart.getContentType().contains("TEXT/PLAIN")){
                          logger.trace("letter:" + count + " subject: " + message.getSubject()+" part:"+(x+1)+"/"+multipart.getCount()+" type:"+bodyPart.getContentType());
                          continue;
                       }
                       logger.trace("letter:" + count + " subject: " + message.getSubject()+" part:"+(x+1)+"/"+multipart.getCount()+" type:"+bodyPart.getContentType());
                       
                       ParsePart(bodyPart,msg);
                   }
               }
               catch(Exception e){
                     logger.error("ex:"+e);
                     return;
               }
        }
        public void work() {

               open();
               if(folder==null)return;

               try {
                    if(folder.getMessageCount()>0) {
                       int count = 0;
                       Message messages[] = folder.getMessages();
                       for(Message message : messages) {
                           count++;
                           parseMessage(message,count);
                           //logger.trace("Has this message been read?  flag:" + mes_flag.contains(Flag.SEEN));
                       }
                    }
               } 
               catch (Exception e) {
                 Except ex=new Except(e);
                 logger.error("ex:" + ex);
                       // System.exit(0);
               } 
               finally {
                  close();
               }
        }

        public static void main(String[] args) {

                System.setProperty("java.net.preferIPv4Stack", "true");
                logger.trace("Set java property:java.net.preferIPv4Stack=true");
                ImapClient cln = new ImapClient();
                String xpath=args[0];
                System.out.println(xpath);

                cln.loadCFG(args[0]);

                cln.work();

        }


}
