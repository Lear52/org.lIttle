package org.little.mailWeb.imap;
       
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.little.mailWeb.folder.folderARH;
import org.little.mailWeb.folder.folderLogFile;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._ByteBuilder;
import org.little.util.run.task;
import org.little.util.string.stringTransform;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.mail.imap.IMAPFolder;

public class ImapLoadBox extends task{

        private static final Logger logger   = LoggerFactory.getLogger(ImapLoadBox.class);

        private folderARH           log_arh;

        private Properties          props;
        private Store               store;
        private IMAPFolder          folder_inbox;
        private IMAPFolder          folder_outbox;
        private Session             session;
        private Authenticator       auth;
        
        private boolean             debug      ;
        private String              userName   ;
        private String              password   ;
        private String              local_bind_client;
        private String              client_host;
        private int                 client_port;
        private int                 task_timeout;
        private String              imap_inbox_folder;
        private String              imap_outbox_folder;

            
        public ImapLoadBox() {
               log_arh         = new folderLogFile();
               store           = null;
               folder_inbox    = null;
               folder_outbox   = null;
               props           = null;
               session         = null;
               auth            = null;
               //cfg             = new commonArh();
               debug            =false;        
               userName         ="av";        
               password         ="";        
               local_bind_client="*";  
               client_host      ="";        
               client_port      =143;        
               task_timeout     =10000;       
               imap_inbox_folder="inbox";  
               imap_outbox_folder=""; 
        }
        
        public boolean      isDebug           () {return debug;             }
        public String       getUser           () {return userName;          }
        public String       getPasswd         () {return password;          }
        public String       getLocalBindClient() {return local_bind_client; }
        public String       getHost           () {return client_host;       }
        public int          getPort           () {return client_port;       }
        public int          getTimeout        () {return task_timeout;      }
        public String       getInboxFolder    () {return imap_inbox_folder; }
        public String       getOutboxFolder   () {return imap_outbox_folder;}

        public void         setFolder         (folderARH f) {log_arh=f;}

        public void work() {

               logger.trace("begin work");

               open();

               if(session     ==null)return;
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
                 logger.error("work() ex:" + ex);
                       // System.exit(0);
               } 
               finally {
                  close();
               }
               logger.trace("end work");
        }

        public void init(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("user"              .equalsIgnoreCase(n.getNodeName())){userName          =n.getTextContent(); logger.info("user:"+userName);}
                     else
                     if("password"          .equalsIgnoreCase(n.getNodeName())){password          =n.getTextContent(); logger.info("password:"+password);}
                     else
                     if("client_port"       .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent(); try{client_port=Integer.parseInt(s, 10);}catch(Exception e){ client_port=143;logger.error("client_port:"+s);} logger.info("client_port:"+client_port);}
                     else                   
                     if("client_host"       .equalsIgnoreCase(n.getNodeName())){client_host       =n.getTextContent(); logger.info("client_host:"+client_host);}
                     else
                     if("local_bind_client" .equalsIgnoreCase(n.getNodeName())){local_bind_client =n.getTextContent(); logger.info("local_bind_client:"+local_bind_client);}
                     else
                     if("is_debug"          .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent(); try{debug=Boolean.parseBoolean(s);}catch(Exception e){ debug=false;logger.error("is_debug:"+s);} logger.info("is_debug:"+debug);}
                     else
                     if("run_timeout"       .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent(); try{task_timeout=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set run_timeout:"+s);task_timeout=10;}logger.info("run_timeout:"+task_timeout);}
                     else
                     if("inbox_folder"      .equalsIgnoreCase(n.getNodeName())){imap_inbox_folder =n.getTextContent(); logger.info("inbox_folder:"+imap_inbox_folder);}
                     else
                     if("outbox_folder"     .equalsIgnoreCase(n.getNodeName())){imap_outbox_folder=n.getTextContent(); logger.info("outbox_folder:"+imap_outbox_folder);}
                 }
                 if(stringTransform.isEmpty(local_bind_client))local_bind_client="*";
              }
              else{
                  logger.error("The configuration node:null");
              }                 
              setDelay(getTimeout());

              props = System.getProperties();
              /*
               * if(false){ Enumeration en = prop.propertyNames();
               * while(en.hasMoreElements()){ String s=(String)en.nextElement();
               * logger.trace(s+":" +prop.getProperty(s)); } }
               */
              if(isDebug())props.setProperty("mail.debug"       , "true");
              else         props.setProperty("mail.debug"       , "false");
              props.setProperty("mail.imap.ssl.enable"          , "false");
              props.setProperty("mail.imap.starttls.enable"     , "false");
              props.setProperty("mail.imap.sasl.enable"         , "false");
              props.setProperty("mail.debug.auth.username"      , "true");
              props.setProperty("mail.debug.auth.password"      , "true");
              if(isDebug())props.setProperty("mail.socket.debug", "true");
              else         props.setProperty("mail.socket.debug", "false");
              auth=new Authenticator() {
                  @Override
                  protected PasswordAuthentication getPasswordAuthentication() {
                          logger.trace("l:p    "+getUser()+":"+getPasswd());
                          return new PasswordAuthentication(getUser(),getPasswd());
                  }
               };

        
        }

        protected boolean  open(){
                session = Session.getInstance(props, auth);
                session.setDebug(isDebug());
                try {
                     // Try to initialise session with given credentials
                     store = session.getStore("imap");
                     store.connect(getHost(), getPort(), getUser(), getPasswd());
                     logger.trace("connect "+getHost()+","+getPort()+","+getUser()+","+getPasswd());

                } catch (Exception e) {
                     Except ex=new Except(e);
                     logger.error("error open connection "+getHost()+","+getPort()+","+getUser()+","+getPasswd()+" ex:" + ex);
                     store   = null;
                     session = null;
                     return false;
                }
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                try {
                     folder_inbox = (IMAPFolder) store.getFolder(getInboxFolder()); // Get the inbox
                     logger.trace("getInboxFolder "+getInboxFolder());

                     if (!folder_inbox.isOpen())folder_inbox.open(Folder.READ_WRITE);
                     logger.trace("openInboxFolder "+getInboxFolder()+" No of Messages:"  + folder_inbox.getMessageCount()+" No of Unread Messages : " + folder_inbox.getUnreadMessageCount());

                } catch (Exception e) {
                     Except ex=new Except(e);
                     logger.error("error open  folder:"+getInboxFolder()+" ex:" + ex);
                     store  = null;
                     session = null;
                     //folder_inbox = null;
                     close();
                     return false;
                }
                logger.trace("Folder:"+getInboxFolder()+" open!");
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                try {
                     folder_outbox = (IMAPFolder) store.getFolder(getOutboxFolder()); // Get the inbox
                     logger.trace("getOutboxFolder "+getOutboxFolder());

                     if (!folder_outbox.isOpen()){
                         folder_outbox.open(Folder.READ_WRITE);
                     }
                     logger.trace("openOutboxFolder "+getOutboxFolder()+" No of Messages:" + folder_inbox.getMessageCount()+" No of Unread Messages : " + folder_inbox.getUnreadMessageCount());

                } catch (Exception e) {
                     Except ex=new Except(e);
                     logger.error("error open folder:"+getOutboxFolder()+" ex:" + ex);
                     //folder_outbox = null;
                     close();
                     return false;
                }
                logger.trace("Folder:"+getOutboxFolder()+" open!");
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                return true;

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

                  session = null;
                  
        }
        //----------------------------------------------------------------------------------------------------------------
        private void ParsePart(byte [] buf,lMessage msg,String filename,boolean is_check_zip) {
                if(is_check_zip){
                   try{
                        logger.trace("check zip:" + filename);
                        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(buf));
                        ZipEntry       entry;
                        String         name;
                        int            size;
                        int            COUNT=0;
                        while((entry=zin.getNextEntry())!=null){
                              
                            name = entry.getName(); // получим название файла
                            size = (int)entry.getSize();  // получим его размер в байтах
                            byte [] _buf=new byte[size]; 
                            zin.read(_buf,0,size);
                            zin.closeEntry();
                            logger.trace("zip entry:" + filename+"|"+name);
                            ParsePart(_buf,msg,filename+"|"+name,false) ;
                            COUNT++;
                        }
                        zin.close();
                        if(COUNT==0){
                           logger.trace("no zip:" + filename);
                           ParsePart(buf,msg,filename,false) ;
                        }
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
                        _msg=lMessageX509.parse(_msg);
                        //logger.trace("letter:" + _msg);
                   }
                   catch(Exception e){
                         logger.error("lMessageX509.parse() ex:"+new Except("parse no zip part",e));
                         return;
                   }
                   try {
                        if(_msg!=null){
                           log_arh.save(_msg);
                        }
                   }
                   catch(Exception e){
                         logger.error("log_arh.save() ex:"+new Except("save no zip part",e));
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
                      logger.error("ParsePart ex:"+e);
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
                     logger.error("parseMessage() ex:"+e);
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
                     //java.lang.ClassCastException: java.lang.String incompatible with javax.mail.Multipart
                     logger.error("Multipart parse ex:"+e);
                     return;
               }
        }


}
