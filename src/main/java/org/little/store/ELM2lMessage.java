package org.little.store;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;

import org.little.util._ByteBuilder;

 

public class ELM2lMessage{
   

    public  static lMessage[] parse(String filename) {
           try{
               BufferedInputStream in=new BufferedInputStream(new FileInputStream(filename));
               return parse(in);
           }
           catch(Exception e){ 
               e.printStackTrace(System.out);
               return null;
           }
    }
    public  static lMessage[]  parse(BufferedInputStream in){
           if(in==null)return null;
           MimeMessage eml;
           Properties  props   = System.getProperties();
           Session     session = Session.getInstance(props, null);
           try{
               eml = new MimeMessage(session,in);
               if(eml instanceof Message){
                  return parse(eml);
               }
               else return null;
               }
           catch (Exception e) { 
                  return null; 
           }
    }
    public  static lMessage[]  parse(MimeMessage eml){

           lMessage            msg =new lMessage ();
           ArrayList<lMessage> list=new ArrayList<lMessage>(10);
           lMessage[]          ret =null;

           try{
               if(eml instanceof Message){
                   Address[] addr;
                   // FROM
                   if ((addr = eml.getFrom())          != null) msg.setFrom(addr[0].toString());
                   // TO
                   //if ((a = eml.getReplyTo())       != null) for (int j = 0; j < a.length; j++)msg.addTO(a[j].toString());
                   if ((addr = eml.getAllRecipients()) != null) for (int j = 0; j < addr.length; j++)msg.addTO(addr[j].toString());
                   // SUBJECT
                   msg.setSubject (eml.getSubject());
                   // DATE
                   msg.setCreateDate(eml.getSentDate());
                   msg.setSentDate(eml.getSentDate());
                   // ID
                   String _id = eml.getMessageID();
                   if(_id==null) {_id=lMessage.getNewID(msg.getFrom());}
                   msg.setId(_id);

                   //String[] hdrs = eml.getHeader("X509");if(hdrs != null) {}
                   msg.setReceiveDate(new Date());
               }
               else return null;
               }
           catch (Exception e) { 
                  return null; 
           }
           
           _eml2msg(list,msg,eml);

           //msg.setBodyTxt(body);
           if(list.size()==0)return null;

           ret=list.toArray(new lMessage[list.size()]);
           return ret;
    }

    private static  int _eml2msg(ArrayList<lMessage> list,lMessage msg,Part p){

           String  filename;
           String  context_type;
           try {
                context_type = p.getContentType();
                context_type=(new ContentType(context_type)).toString();
                filename = p.getFileName();
           } 
           catch (Exception pex) {
               return -1;
           }

           try{
               if(p.isMimeType("text/plain")){
                  String body=((String)p.getContent());
                  if(filename==null)return 0;
                  if("".equals(filename))return 0;
                  byte []  buf=body.getBytes();
                  lMessage _msg=new lMessage(msg);
                  _msg.setFilename(filename);
                  _msg.setBodyBin(buf);
                  list.add(_msg);
                  return 1;
               } 
               else 
               if (p.isMimeType("multipart/*")) {
	          Multipart mp = (Multipart)p.getContent();
	          int count = mp.getCount();
	          for (int i = 0; i < count; i++)_eml2msg(list,msg,mp.getBodyPart(i));
               }
               else 
               if (p.isMimeType("message/rfc822")) {
                  _eml2msg(list,msg,(Part)p.getContent());
               }
               else {
                    Object o = p.getContent();
                    if (o instanceof String) {
                        String body=((String)o);
                        if(filename==null)return 0;
                        if("".equals(filename))return 0;
                        byte []  _buf=body.getBytes();
                        lMessage _msg=new lMessage(msg);
                        _msg.setFilename(filename);
                        _msg.setBodyBin(_buf);
                        list.add(_msg);
                        return 1;
                    } 
                    else 
                    if (o instanceof InputStream) {
                        InputStream is = (InputStream)o;
                        //int c;
                        _ByteBuilder buffer=new _ByteBuilder(10240);
                        int ret=0;
                        do{
                           ret=buffer.read(is);
                        }
                        while(ret>0);
                        if(filename==null)return 0;
                        if("".equals(filename))return 0;
                        byte []  byte_buf=buffer.getBytes();
                        System.out.println("file len:"+byte_buf.length);
                        lMessage _msg=new lMessage(msg);
                        _msg.setFilename(filename);
                        _msg.setBodyBin(byte_buf);
                        list.add(_msg);
                        return 1;
                    } 
                    else
                    return -1;
               }
           }
           catch (Exception e) { 
                  return -1; 
           }
           return 0;
    }

}

