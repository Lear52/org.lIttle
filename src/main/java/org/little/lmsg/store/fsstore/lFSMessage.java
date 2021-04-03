package org.little.lmsg.store.fsstore;
        
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.little.lmsg.lMessage;
import org.little.lmsg.store.lFolder;
import org.little.util.Except;
import org.little.util._Base64;
import org.little.util.string.stringDate;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class lFSMessage  extends lFSElement {
       private static final Logger logger = LoggerFactory.getLogger(lFSMessage.class);


       private lMessage           msg;
       public final static String PREFIX="m_";
      
      
       public    lFSMessage(lFolder folder,String _name, int msgnum) {
                 super((lFSFolder)folder,_name);
                 clear();
                 msg.setNum(msgnum);
       }

       protected void   clear(){msg=new lMessage();}
       protected String getPrefix  (){return PREFIX;};
      
       public lMessage  get(){return msg;};
       public void      set(lMessage m){msg=m;};

       public boolean create(){
              File f=new File(getFullName());
              boolean ret=false;
              try{
                  ret=f.createNewFile();
              }catch(Exception e){
                     if(logger.isTrace()) {
                        Except ex=new Except("error create:"+getFullName(),e);
                        logger.error("ex:"+ex);
                     }
              }
              return ret;
       };
       public boolean del(){
              File f=new File(getFullName());
              boolean ret=false;
              try {
                  f.delete();
              } 
              catch (Exception e) {
                     if(logger.isTrace()) {
                        Except ex=new Except(e);
                        logger.error("ex:"+ex);
                     }
              }
              return ret;
       }

       public boolean save(){
              try {
                  FileOutputStream out = new FileOutputStream(getFullName());
                  String s=msg2str();
                  if(s==null)return false;
                  byte [] buffer=s.getBytes();
                  out.write(buffer);
                  out.flush();  
                  out.close();
              } 
              catch (Exception e) {
                     if(logger.isTrace()) {
                        Except ex=new Except(e);
                        logger.error("ex:"+ex);
                     }
              }
              return true;
       };
      
       public boolean load(){
              try {
                   BufferedInputStream is = new BufferedInputStream(new FileInputStream(getFullName()));

                   boolean ret=load(get(),is);

                   is.close();
                   return ret;
              } 
              catch (Exception e) {
                     if(logger.isTrace()) {
                        Except ex=new Except(e);
                        logger.error("ex:"+ex);
                     }
                  return false;
              }
             
       }
       public boolean copy(lFolder new_folder){
              lFSMessage new_msg=((lFSFolder)new_folder).createMessageXML();
              if(new_msg==null)return false;
              new_msg.set(get());
              return new_msg.save();
       }
       public boolean remove(lFolder new_folder){
              boolean ret=copy(new_folder);
              if(ret==false) return false;
              return del();
       }
       private  boolean load(lMessage msg,BufferedInputStream is) {
              Document    doc=null;
              try {
                   DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                   doc= documentBuilder.parse(is);
              } 
              catch (Exception e) {
                     if(logger.isTrace()) {
                        Except ex=new Except(e);
                        logger.error("ex:"+ex);
                     }
                     return false;
              }
             
              try {
                   doc.getFirstChild().getNodeValue();
                   Node node_cfg = doc.getFirstChild();                
             
                   if("msgbox".equals(node_cfg.getNodeName())==false){ return false;}
             
                   NodeList list=node_cfg.getChildNodes();     
                   for(int i=0;i<list.getLength();i++){
                       Node n=list.item(i);
                       if("header".equals(n.getNodeName())){loadHeader(n);                                                   continue;}
                       else
                       if("body_t".equals(n.getNodeName())){get().setBodyTxt(n.getTextContent());                            continue;}
                       else
                       if("body_b".equals(n.getNodeName())){get().setBodyBin(_Base64.base64ToByteArray(n.getTextContent())); continue;}
                   }
                   return true;
              } 
              catch (Exception e) {
                     if(logger.isTrace()) {
                        Except ex=new Except(e);
                        logger.error("ex:"+ex);
                     }
              }
              return false;
       }
       private void loadHeader(Node node_header) {
               NodeList list=node_header.getChildNodes();     
               for(int i=0;i<list.getLength();i++){
                   Node n=list.item(i);
                   if("msg_from"        .equals(n.getNodeName())){ get().setFrom       (n.getTextContent());                                                                              continue;}
                   else
                   if("msg_id"          .equals(n.getNodeName())){ get().setId         (new String(_Base64.base64ToByteArray(n.getTextContent())));                                       continue;}
                   else                                                                                                                                                                  
                   if("msg_subject"     .equals(n.getNodeName())){ get().setSubject    (new String(_Base64.base64ToByteArray(n.getTextContent())));                                       continue;}
                   else                                                                                                                                                                  
                   if("msg_to"          .equals(n.getNodeName())){ loadTo(n);                                                                                                             continue;}
                   else                                                                                                                                                                  
                   if("msg_filename"    .equals(n.getNodeName())){ get().setFilename   (n.getTextContent());                                                                              continue;}
                   else                                                                                                                                                                  
                   if("msg_mimetype"    .equals(n.getNodeName())){ get().setMime       (n.getTextContent());                                                                              continue;}
                   else                                                                                                                                                                  
                   if("msg_create_date" .equals(n.getNodeName())){ get().setCreateDate (stringDate.str2date(n.getTextContent()));                                                            continue;}
                   else                                                                                                                                                                  
                   if("msg_sent_date"   .equals(n.getNodeName())){ get().setSentDate   (stringDate.str2date(n.getTextContent()));                                                            continue;}
                   else                                                                                                                                                                  
                   if("msg_receive_date".equals(n.getNodeName())){ get().setReceiveDate(stringDate.str2date(n.getTextContent()));                                                            continue;}
                   else                                                                                                                                                                  
                   if("msg_del_date"   .equals(n.getNodeName())){ get().setDelDate     (stringDate.str2date(n.getTextContent()));                                                            continue;}
                   else                                                                                                                                                                  
                   if("msg_answer_date".equals(n.getNodeName())){ get().setAnswerDate  (stringDate.str2date(n.getTextContent()));                                                            continue;}
                   else
                   if("msg_uid"        .equals(n.getNodeName())){ String s=n.getTextContent(); int _id=0;  try{_id=Integer.parseInt(s);   }catch(Exception e){_id=0;} get().setUID(_id);    continue;}
                   else                                                                                                                                                                  
                   if("msg_size"       .equals(n.getNodeName())){ String s=n.getTextContent(); int size=0; try{size=Integer.parseInt(s);}catch(Exception e){size=0;} get().setSize(size); continue;}
                   else
                   if("x509"           .equals(n.getNodeName())){ loadX509(n);                                                                                                            continue;}
               }
           
       }
       private void loadTo(Node node_to) {
               NodeList list=node_to.getChildNodes();     
               for(int i=0;i<list.getLength();i++){
                   Node n=list.item(i);
                   if("to".equals(n.getNodeName())){ get().addTO(n.getTextContent());continue;}
               }
       }
       private void loadX509(Node node_x509) {
               NodeList list=node_x509.getChildNodes();     
               for(int i=0;i<list.getLength();i++){
                   Node n=list.item(i);
                   if("x509_type"      .equals(n.getNodeName())){ get().setX509Type     (n.getTextContent());          continue;}
                   else
                   if("x509_type_file" .equals(n.getNodeName())){ get().setX509TypeFile (n.getTextContent());          continue;}
                   else
                   if("x509_begin_date".equals(n.getNodeName())){ get().setX509BeginDate(stringDate.str2date(n.getTextContent()));continue;}
                   else
                   if("x509_end_date"  .equals(n.getNodeName())){ get().setX509EndDate  (stringDate.str2date(n.getTextContent()));continue;}
                   else
                   if("x509_serial"    .equals(n.getNodeName())){ get().setX509Serial   (n.getTextContent());          continue;}
                   else
                   if("x509_subject"   .equals(n.getNodeName())){ get().setX509Subject  (n.getTextContent());          continue;}
                   else
                   if("x509_issuer"    .equals(n.getNodeName())){ get().setX509Issuer   (n.getTextContent());          continue;}
               }
       }

       private String msg2str() {
           StringBuffer buf=new StringBuffer(10240);
           buf.append("<?xml version=\"1.0\" encoding=\"WINDOWS-1251\"?>\n");
           buf.append("<msgbox>\n");
           buf.append("<header>\n");
           buf.append("<msg_uid>" ).append(get().getUID() ).append("</msg_uid>\n" );
           buf.append("<msg_from>").append(get().getFrom()).append("</msg_from>\n");
           buf.append("<msg_to>\n");
           {
            String[] to=get().getTO();
            for(int i=0;i<to.length;i++)buf.append("<to>").append(to[i]).append("</to>\n");
            buf.append("</msg_to>\n");
           }

           if(get().getCreateDate() !=null){buf.append("<msg_create_date>").append(stringDate.date2str(get().getCreateDate())).append("</msg_create_date>\n");             }
           if(get().getSentDate()   !=null){buf.append("<msg_sent_date>").append(stringDate.date2str(get().getSentDate())).append("</msg_sent_date>\n");                   }
           if(get().getReceiveDate()!=null){buf.append("<msg_receive_date>");buf.append(stringDate.date2str(get().getReceiveDate()));  buf.append("</msg_receive_date>\n");}
           if(get().getDelDate()    !=null){buf.append("<msg_del_date>").append(stringDate.date2str(get().getReceiveDate())).append("</msg_del_date>\n");                  }
           if(get().getAnswerDate() !=null){buf.append("<msg_answer_date>").append(stringDate.date2str(get().getReceiveDate())).append("</msg_answer_date>\n");            }

           buf.append("<msg_id>")      .append(_Base64.byteArrayToBase64(get().getId().getBytes()))     .append("</msg_id>\n");
           buf.append("<msg_subject>") .append(_Base64.byteArrayToBase64(get().getSubject().getBytes())).append("</msg_subject>\n");

           buf.append("<msg_filename>").append(get().getFilename()).append("</msg_filename>\n");
           buf.append("<msg_mimetype>").append(get().getMime())    .append("</msg_mimetype>\n");
           buf.append("<msg_size>")    .append(get().getSize())    .append("</msg_size>\n");
                                                                                                 
           buf.append("<x509>\n");                                                               
           buf.append("<x509_type>");       buf.append(get().getX509Type     ());                  buf.append("</x509_type>\n");
           buf.append("<x509_type_file>");  buf.append(get().getX509TypeFile ());                  buf.append("</x509_type_file>\n");
           buf.append("<x509_begin_date>"); buf.append(stringDate.date2str(get().getX509BeginDate()));buf.append("</x509_begin_date>\n");
           buf.append("<x509_end_date>");   buf.append(stringDate.date2str(get().getX509EndDate  ()));buf.append("</x509_end_date>\n");
           buf.append("<x509_serial>");     buf.append(get().getX509Serial   ());                  buf.append("</x509_serial>\n");
           buf.append("<x509_subject>");    buf.append(get().getX509Subject  ());                  buf.append("</x509_subject>\n");
           buf.append("<x509_issuer>");     buf.append(get().getX509Issuer   ());                  buf.append("</x509_issuer>\n");
           buf.append("</x509>\n");

           buf.append("</header>\n");

           if(get().getBodyTxt()!=null){ buf.append("<body_t>").append(get().getBodyTxt()).append("</body_t>\n"); }
           if(get().getBodyBin()!=null){ buf.append("<body_b>").append(_Base64.byteArrayToBase64(get().getBodyBin())).append("</body_b>\n");}

           buf.append("</msgbox>");          
           return buf.toString();
       }

       

}
                              