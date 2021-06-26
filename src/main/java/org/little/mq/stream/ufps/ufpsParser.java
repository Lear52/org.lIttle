package org.little.mq.stream.ufps;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.little.util.Except;
import org.little.util.Logger;

public class ufpsParser {
       final private static String CLASS_NAME="prj0.stream.ufpsParser";
       final private static int    CLASS_ID  =1605;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       public static boolean is_debug=true;

       public static void set(u_msg msg,String element,String data){
                 if(element==null)return;
                 if(data==null)return;

                 //if(element.equalsIgnoreCase(def.H_HEADER                    )){}else
                 if(element.equalsIgnoreCase(def.H_NAME_TO                   )){msg.addTO(data)       ;}else
                 if(element.equalsIgnoreCase(def.H_NAME_FROM                 )){msg.setFROM(data)     ;}else
                 //if(element.equalsIgnoreCase(def.H_MESSAGE_INFO_TAG          )){}else
                 if(element.equalsIgnoreCase(def.H_MESSAGE_TYPE              )){msg.setType(data)      ;}else
                 if(element.equalsIgnoreCase(def.H_PRIORITY                  )){msg.setPriority(data)  ;}else
                 if(element.equalsIgnoreCase(def.H_MESSAGE_ID                )){msg.setID(data)        ;}else
                 //if(element.equalsIgnoreCase(def.H_LEGACY_TRANSPORT_FILE_NAME)){}else
                 if(element.equalsIgnoreCase(def.H_CREATE_TIME               )){msg.setCreateTime(data) ;}else
                 if(element.equalsIgnoreCase(def.H_APPLICATION_MESSAGE_ID    )){msg.setAppID(data)     ;}else
                 if(element.equalsIgnoreCase(def.H_CORRELATION_MESSAGE_ID    )){msg.setCorID(data)     ;}else
                 if(element.equalsIgnoreCase(def.H_SEND_TIME                 )){msg.setSendTime(data)  ;}else
                 if(element.equalsIgnoreCase(def.H_RECEIVE_TIME              )){msg.setReceveTime(data);}else
                 if(element.equalsIgnoreCase(def.H_ACCEPT_TIME               )){msg.setAcceptTime(data);}else
                 if(element.equalsIgnoreCase(def.H_ACKNOLEDGE_REQUEST        )){msg.setAckRequest(data);}else
                 if(element.equalsIgnoreCase(def.H_DOC_FORMAT                )){msg.setDocFormat (data);}else
                 if(element.equalsIgnoreCase(def.H_DOC_TYPE                  )){msg.setDocType   (data);}else
                 if(element.equalsIgnoreCase(def.H_DOC_ID                    )){msg.setDocID     (data);}else
                 {}
               
       }

       public static int parse(u_msg msg,InputStream input) throws Except{
              XMLStreamReader r       = null;
              XMLInputFactory factory = XMLInputFactory.newInstance();
              try {
                r = factory.createXMLStreamReader(input);
              } 
              catch (XMLStreamException e) {

                throw new Except ("Error createXMLStreamReader",e);
              }

              try {
                   int event = r.getEventType();
                   String current=null;
                   boolean is_run=true;
                   while (is_run) {
                          switch (event) {
                          case XMLStreamConstants.START_DOCUMENT:
                                if(is_debug)log.trace("XML start");
                                break;
                          case XMLStreamConstants.START_ELEMENT:
                                if(is_debug)log.trace("XML elenment start");
                                QName obj=r.getName();
                                current=obj.getLocalPart();
                                if(is_debug)log.trace("XML current:"+current);
                                if(current!=null)if(current.equalsIgnoreCase(def.H_BODY))is_run=false;
                                break;
                          case XMLStreamConstants.CHARACTERS:
                                if(is_debug)log.trace("XML char:"+r.getText());
                                if (r.isWhiteSpace()) break;
                                set(msg,current,r.getText());                                  
                                break;
                          case XMLStreamConstants.END_ELEMENT:
                                if(is_debug)log.trace("XML elenment end");
                                break;
                          case XMLStreamConstants.END_DOCUMENT:
                                if(is_debug)log.trace("XML end");
                                break;
                          }
                          if (!r.hasNext())is_run=false;
                          else event = r.next();
                   }
           } 
           catch (XMLStreamException e) {
                  Except ex=new Except("XML parsing",e);
                  log.error(""+ex);
                  return def.RET_ERROR;
           }
           finally {
                try{r.close();} catch (XMLStreamException e) {}
           }
           return def.RET_OK;


       }
     
     
       public static void main(String[] args) {
	      try {
                  int count=1;
                  FileInputStream fis;
                  if(args.length==0){
                     System.out.println("java "+getClassName()+" filename.xml count");
                     return;
                  }
                  if(args.length>1){
                     try {count=Integer.parseInt(args[1], 10);} catch (Exception e) {count=1;}
                  }
                  fis = new FileInputStream(args[0]);
                  byte[] data = new byte[fis.available()];
                  fis.read(data);
                  fis.close();
                  System.out.println("start:"+new java.util.Date());
                  System.out.println("count:"+count);
                  u_msg msg=null;
                  for(int j=0;j<1;j++){
                  for(int i=0;i<count;i++){
                      ByteArrayInputStream input = new ByteArrayInputStream(data);
                      msg=new u_msg();
                      ufpsParser.parse(msg, input);
                  }
                  }
                  System.out.println("end:"+new java.util.Date());
                  System.out.println(msg.toString());
	      } catch (Exception e) {
		e.printStackTrace();
	      }
                  
       }


}
