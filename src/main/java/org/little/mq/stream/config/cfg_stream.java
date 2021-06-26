package org.little.mq.stream.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.little.mq.mqapi.cfg_conn;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.little.util.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class cfg_stream{
       final private static String CLASS_NAME="org.little.mq.stream.config.cfg_stream";
       final private static int    CLASS_ID  =1803;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private ArrayList<cfg_interface> interfaces;
       private cfg_conn                 default_conn;


       public cfg_stream() {
              interfaces = new ArrayList<cfg_interface>(8);
              default_conn=null;
       }
       public int size() {
              return interfaces.size();
       }

       public cfg_interface getInterface(int x) {
              if (x < interfaces.size()) return interfaces.get(x);
              return null;
       }

       public void setConnection(cfg_conn x) {
              default_conn=x;
       }

       public int parse(String xml)   {

              Document        cnfgDoc ;
              try{
                  DocumentBuilder builder ;
                  java.io.Reader in=new java.io.StringReader(xml);
                  builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                  cnfgDoc = builder.parse(new org.xml.sax.InputSource(in));
              }
              catch(ParserConfigurationException ex1){
                    log.error("parse(1) config file ex:"+ex1);
                    return -1;
              }
              catch(SAXException ex2){
                    log.error("parse(2) config file ex:"+ex2);
                    return -1;

              }
              catch(IOException ex3){
                    log.error("parse(3) config file ex:"+ex3);
                    return -1;
              }

              int  inter_size=cnfgDoc.getElementsByTagName(def.CNFG_TAG_INTERFACE).getLength();

              if (inter_size == 0) { 
                     String err=new String(new Date().toString() + " <> " + "Wrong configuration - at least one interface required");
                     log.error(err);
                     return -1;
              }
              log.trace("parse xml config file");

              for(int i = 0; i < inter_size; i++) {
                  cfg_interface  inter=new cfg_interface();

                  inter.setConnection(default_conn);

                  Node node=cnfgDoc.getElementsByTagName(def.CNFG_TAG_INTERFACE).item(i);
                  int ret=inter.parse(node);
                  if(ret<0)continue;
                  interfaces.add(inter);
                  log.trace("add interface:"+i);
              }
              if(interfaces.size()==0)return -1;
                 return 0;
       }
       public static void main(String[] args) throws Exception {
              String xml="<STREAM_CONFIGURATION>"+"\n"+
                         "<INTERFACES>"+"\n"+
                         "  <INT name=\"INT1\" description=\"#1\">"+"\n"+
                         "       <SOURCE_QUEUE_NAME>IN.STREAM</SOURCE_QUEUE_NAME>"+"\n"+
                         "       <ERROR_QUEUE_NAME>ERROR.STREAM</ERROR_QUEUE_NAME>"+"\n"+
                         "       <LOG_PATH>c:\\stream\\log</LOG_PATH>"+"\n"+
                         "       <LOG_LEVEL>ALL</LOG_LEVEL>"+"\n"+
                         "       <LOG_FORMAT>ALL</LOG_FORMAT>"+"\n"+
                         "       <DEFAULT_ROUTE>"+"\n"+
                         "           <TARGET_QUEUE_NAME>TO.SVK</TARGET_QUEUE_NAME>"+"\n"+
                         "       </DEFAULT_ROUTE>"+"\n"+
                         "       <ROUTE>"+"\n"+
                         "            <TARGET_QUEUE_NAME>TO.RKC02_22</TARGET_QUEUE_NAME>"+"\n"+
                         "            <POINT>uic:2202000000**</POINT>"+"\n"+
                         "       </ROUTE>"+"\n"+
                         "       <ROUTE>"+"\n"+
                         "            <TARGET_QUEUE_NAME>TO.RKC82_22</TARGET_QUEUE_NAME>"+"\n"+
                         "            <POINT>uic:2208001000**</POINT>"+"\n"+
                         "       </ROUTE>"+"\n"+
                         "       <ROUTE>"+"\n"+
                         "            <TARGET_QUEUE_NAME>TO.RKC99_22</TARGET_QUEUE_NAME>"+"\n"+
                         "            <POINT>uic:2299999000**</POINT>"+"\n"+
                         "       </ROUTE>"+"\n"+
                         "       <ROUTE>"+"\n"+
                       "            <TARGET_QUEUE_NAME>TO.PU_01</TARGET_QUEUE_NAME>"+"\n"+
                         "            <POINT>uic:2201002000**</POINT>"+"\n"+
                         "       <ROUTE>"+"\n"+
                         "   </INT>"+"\n"+
                         "</INTERFACES>"+"\n"+
                         "</STREAM_CONFIGURATION>"+"\n"
                         ;
              cfg_stream cfg=new cfg_stream();
              cfg.parse(xml);
       }
            
                                
}