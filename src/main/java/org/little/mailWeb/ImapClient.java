package org.little.mailWeb;
       
import java.io.*;
import java.util.zip.*;
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
import org.little.util.run.task;

import com.sun.mail.imap.IMAPFolder;

public class ImapClient extends task{

        private static final Logger logger   = LoggerFactory.getLogger(ImapClient.class);

        private static folderARH    log_arh = new folderLogFile();

        private Properties          props;
        private Store               store;
        private IMAPFolder          folder_inbox;
        private IMAPFolder          folder_outbox;
        private Session             session;
        private commonArh           cfg;

            
        public ImapClient() {
               store           = null;
               folder_inbox    = null;
               folder_outbox   = null;
               session         = null;
               cfg             = new commonArh();
        }


        public boolean loadCFG(String xpath){
               boolean ret=cfg.loadCFG(xpath);
               cfg.init();
               log_arh=cfg.getCfgDB().getFolder();
               return ret;
        }
        public int     getTimeout        () {return cfg.getTimeout();}

        public folderARH getLog          () {return cfg.getFolder(); }
        public String    getDefPage      () {return cfg.getDefPage(); }
        public String    getErrorPage      () {return cfg.getErrorPage(); }

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
                                  logger.trace("l:p    "+cfg.getUser()+":"+cfg.getPasswd());
                                  return new PasswordAuthentication(cfg.getUser(),cfg.getPasswd());
                          }
                };
        
                logger.trace("console:pre Session.getInstance");
                session = Session.getInstance(props, auth);//null
                logger.trace("console:post Session.getInstance");

                session.setDebug(cfg.isDebug());
                try {
                     // Try to initialise session with given credentials
                     store = session.getStore("imap");

                     logger.trace("connect to:"+cfg.getHost()+"("+cfg.getPort()+") "+cfg.getUser()+":"+cfg.getPasswd());

                     store.connect(cfg.getHost(), cfg.getPort(), cfg.getUser(), cfg.getPasswd());// 143

                     logger.trace("connect "+cfg.getHost()+","+cfg.getPort()+","+cfg.getUser()+","+cfg.getPasswd());

                } catch (Exception e) {
                     Except ex=new Except(e);
                     logger.error("error open connection ex:" + ex);
                     store = null;
                     return;
                }
                try {
                     folder_inbox = (IMAPFolder) store.getFolder(cfg.getInboxFolder()); // Get the inbox
                     logger.trace("getInboxFolder "+cfg.getInboxFolder());

                     if (!folder_inbox.isOpen())folder_inbox.open(Folder.READ_WRITE);
                     logger.trace("openInboxFolder "+cfg.getInboxFolder()+" No of Messages:"  + folder_inbox.getMessageCount()+" No of Unread Messages : " + folder_inbox.getUnreadMessageCount());

                } catch (Exception e) {
                     Except ex=new Except(e);
                     logger.error("error open input folder ex:" + ex);
                     store  = null;
                     folder_inbox = null;
                     return;
                }
                logger.trace("getInboxFolder open!");
                try {
                     folder_outbox = (IMAPFolder) store.getFolder(cfg.getOutboxFolder()); // Get the inbox
                     logger.trace("getOutboxFolder "+cfg.getOutboxFolder());

                     if (!folder_outbox.isOpen())folder_outbox.open(Folder.READ_WRITE);
                     logger.trace("openOutboxFolder "+cfg.getInboxFolder()+" No of Messages:" + folder_inbox.getMessageCount()+" No of Unread Messages : " + folder_inbox.getUnreadMessageCount());

                } catch (Exception e) {
                     Except ex=new Except(e);
                     logger.error("error open output folder ex:" + ex);
                     folder_outbox = null;
                     return;
                }
                logger.trace("getOutboxFolder open!");


        }
        protected void close(){
                  try {
                       if(folder_inbox != null && folder_inbox.isOpen()) {
                          folder_inbox.close(true);
                          folder_inbox=null;
                       }
                  } catch (Exception e) {}
                  try {
                       if(folder_outbox != null && folder_outbox.isOpen()) {
                           folder_outbox.close(true);
                           folder_outbox=null;
                       }
                  } catch (Exception e) {}
                  try {
                       if(store != null) {
                          store.close();
                       }
                       store=null;
                  } catch (Exception e) {}

        }
        private void ParsePart(byte [] buf,lMessage msg,String filename,boolean is_check_zip) {
                if(is_check_zip){
                   try{
                        logger.trace("check zip:" + filename);
                        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(buf));
                        ZipEntry       entry;
                        String         name;
                        int            size;
                        while((entry=zin.getNextEntry())!=null){
                              
                            name = entry.getName(); // получим название файла
                            size = (int)entry.getSize();  // получим его размер в байтах
                            byte [] _buf=new byte[size]; 
                            zin.read(_buf,0,size);
                            zin.closeEntry();
                            logger.trace("zip entry:" + filename+"|"+name);
                            ParsePart(_buf,msg,filename+"|"+name,false) ;

                        }
                        zin.close();
                        return;
                   }
                   catch(Exception e){
                         logger.trace("no zip:" + filename);
                   }
                   ParsePart(buf,msg,filename,false) ;
                   return;
                }
                else{
                   logger.trace("no zip parse:" + filename);
                   lMessage _msg=new lMessage(msg);
                   _msg.setFilename(filename);
                   try {
                        _msg.setBodyBin(buf);
                        //logger.trace("letter:" + _msg);
                      
                        _msg=lMessageX509.parse(_msg);
                        if(_msg!=null){
                           logger.trace("letter:" + _msg);

                           log_arh.save( _msg);
                        }
                   }
                   catch(Exception e){
                         logger.error("ex:"+e);
                         return;
                   }
                   logger.trace("parse:" + filename+" ok!");
                }
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

                        ParsePart(buf,msg,bodyPart.getFileName(),true);

                }
                catch(Exception e){
                      logger.error("ex:"+e);
                      return;
                }
               
        }
        private void parseMessage(Message message,int count) {
               lMessage msg=new lMessage();
               try {
                  //Flags mes_flag = message.getFlags();
                  // Get subject of each message
                  logger.trace("letter:" + count + " subject: " + message.getSubject());
              
                  Address[] addr;

                  if((addr = message.getFrom()) != null) msg.setFrom(addr[0].toString());
                  if((addr = message.getAllRecipients()) != null) for (int j = 0; j < addr.length; j++)msg.addTO(addr[j].toString());

                  msg.setUID(-1);
                  
                  msg.setNum       (message.getMessageNumber());
                  msg.setSubject   (message.getSubject());
                  msg.setCreateDate(message.getSentDate());
                  msg.setSentDate  (message.getSentDate());
                  msg.setReceiveDate(message.getReceivedDate());
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

               logger.trace("begin work");
               open();
               if(folder_inbox==null)return;

               try {
                    if(folder_inbox.getMessageCount()>0) {
                       int count = 0;
                       Message messages[] = folder_inbox.getMessages();
                       for(Message message : messages) {
                           count++;
                           parseMessage(message,count);

                           if(folder_outbox!=null) {
                              Message[] tempMessageArray=new Message[1];
                              tempMessageArray[0]=message;
                              folder_inbox.copyMessages(tempMessageArray, folder_outbox); 
                              message.setFlag(Flags.Flag.DELETED, true);
                           }
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
               logger.trace("end work");
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
