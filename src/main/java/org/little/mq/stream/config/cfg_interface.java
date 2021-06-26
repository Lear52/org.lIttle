package org.little.mq.stream.config;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.mq.mqapi.cfg_conn;
import org.little.util.string.stringCase;
import org.little.util.string.stringWildCard;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class cfg_interface {
       final private static String CLASS_NAME = "org.little.mq.stream.config.cfg_intstance";
       final private static int CLASS_ID = 1801;
       public static String getClassName(){return CLASS_NAME;}
       public static int getClassId() {return CLASS_ID;}
       private static Logger log=new Logger(CLASS_NAME);


       private String               name        ;
       private String               description ;
       private String               source      ;
       private String               error       ;
       private String               logPath     ;
       private int                  logLevel    ;
       private String               logFormat   ;
       private ArrayList<cfg_point> maskRoute   ;
       private cfg_point            defaultRoute;
        private cfg_conn             default_conn;

        public cfg_interface(){clear();}

        public void clear(){
               name         = null;                               
               description  = null;                               
               source       = null;                               
               error        = null;                               
               logPath      = null;                               
               logLevel     = def.CNFG_TAG_LOG_LEVEL_DEFAULT_INT; 
               logFormat    = null;                               
               maskRoute    = new ArrayList<cfg_point>(16);                               
               defaultRoute = null; 
               default_conn = null;
                              
        }


       public String getName       () {return name;       }
       public String getDescription() {return description;}
       public String getSource     () {return source;     }
       public String getError      () {return error;      }
       public String getLogPath    () {return logPath;    }
       public int    getLogLevel   () {return logLevel;   }
       public String getLogFormat  () {return logFormat;  }

       public void   setConnection(cfg_conn x){default_conn=x;}

       public ArrayList<cfg_point> getQ() {
              ArrayList<cfg_point> outqueue=new ArrayList<cfg_point>(16);
              for(int i = 0; i < maskRoute.size(); i++) {
                   cfg_point p=maskRoute.get(i);
                   //log.trace(p.toString());
                   if(p.is(outqueue)==false)outqueue.add(p);
              }
              return outqueue;
       }
       public void getRoute(String uic_to,ArrayList<cfg_addr> outqueue) {
              for(int i = 0; i < maskRoute.size(); i++) {
                  //получить маску по маске                      
                  cfg_point p=maskRoute.get(i);
                  //поиск по маске                      
                  if(stringWildCard.wildcardMatch(uic_to, p.getMask(),stringCase.SENSITIVE)==true){
                     // маска совпала
                     //if(p.is(outqueue)==false) {
                           //новый адрес
                            boolean is_new=true;
                         for(int j=0;j<outqueue.size();j++) {
                                cfg_addr pp=outqueue.get(i);
                                if(p.equals(pp)) {is_new=false;break;}
                         }                             
                            if(is_new)outqueue.add(p);
                     //}
                     if(p.isLast())return ;//
                  }
              }
        }

       public int  parse(Node node){
              name         =node.getAttributes().getNamedItem(def.CNFG_TAG_INTERFACE_NAME).getTextContent();
              description  =node.getAttributes().getNamedItem(def.CNFG_TAG_INTERFACE_DESCRIPTION).getTextContent();
              return parse(node.getChildNodes());
        }
       private int parse(NodeList nodeList) {
               ArrayList<cfg_point> _maskRoute = new ArrayList<cfg_point>(16);                               
               int n  = nodeList.getLength();
               int i;
               for (i = 0; i < n; i++) {
                    Node nd=nodeList.item(i);
                    String tag =nd.getNodeName();

                    if((source    == null)&& (tag.equalsIgnoreCase(def.CNFG_TAG_SOURCE_QUEUE_NAME))) source    = nd.getTextContent();
                    else 
                    if((error     == null)&& (tag.equalsIgnoreCase(def.CNFG_TAG_ERROR_QUEUE_NAME)))  error     = nd.getTextContent();
                    else 
                    if((logPath   == null)&& (tag.equalsIgnoreCase(def.CNFG_TAG_LOG_PATH)))          logPath   = nd.getTextContent();
                    else 
                    if((logLevel  == 0l)&& (tag.equalsIgnoreCase(def.CNFG_TAG_LOG_LEVEL))){
                         String cntx=nd.getTextContent();
                         if (def.CNFG_TAG_LOG_LEVEL_ALL.equalsIgnoreCase(cntx))       logLevel = def.CNFG_TAG_LOG_LEVEL_ALL_INT;
                         else 
                         if (def.CNFG_TAG_LOG_LEVEL_MQ_HEADER.equalsIgnoreCase(cntx)) logLevel = def.CNFG_TAG_LOG_LEVEL_MQ_HEADER_INT;
                         else 
                         if (def.CNFG_TAG_LOG_LEVEL_XML_HEADER.equalsIgnoreCase(cntx))logLevel = def.CNFG_TAG_LOG_LEVEL_XML_HEADER_INT;
                         else 
                         if (def.CNFG_TAG_LOG_LEVEL_DISABLED.equalsIgnoreCase(cntx))  logLevel = def.CNFG_TAG_LOG_LEVEL_DISABLED_INT;
                    }
                    else 
                    if((logFormat == null)&&(tag.equalsIgnoreCase(def.CNFG_TAG_LOG_FORMAT)))logFormat = nd.getTextContent();
                    else 
                    if((defaultRoute == null)&&(tag.equalsIgnoreCase(def.CNFG_TAG_DEFAULT_ROUTE))){

                        cfg_point p = new cfg_point();
                        p.setMask("*");
                        p.isDefault(true);
               
                        int ret=p.parse(nd);

                        if(ret<0)continue;
               
                        defaultRoute=p;
                    //log.trace("define point:default"+"  "+defaultRoute.toString());
                    }
                    else 
                    if(tag.equalsIgnoreCase(def.CNFG_TAG_ROUTE)){
                        cfg_point p = new cfg_point();

                        int ret=p.parse(nd);

                        if(ret<0)continue;
                        _maskRoute.add(p);
                    }

               }
               for (i = 0; i < _maskRoute.size(); i++) {
                   cfg_point p =_maskRoute.get(i);
                   if(p.isDefault())continue;
                   if(p.getQMName            ()==null)p.setQMName         (default_conn.getQMName           ());   
                   if(defaultRoute!=null){
                      if(p.getReportQMName   ()==null)p.setReportQMName   (defaultRoute.getReportQMName   ());   
                      if(p.getReportQueueName()==null)p.setReportQueueName(defaultRoute.getReportQueueName());
                      //if(p.getReport         ()==null)p.setReport         (defaultRoute.getReport         ());         
                      //if(p.getMask           ()==null)p.setMask           (defaultRoute.getMask           ());           
                      if(p.getExpiry         ()==null)p.setExpiry         (defaultRoute.getExpiry         ());         
                   }
                   log.trace("point:"+i+"  "+p.toString());
                   maskRoute.add(p);
               }
               if(defaultRoute!=null){
                   if(defaultRoute.getQMName ()==null)defaultRoute.setQMName(default_conn.getQMName           ());   
                   maskRoute.add(defaultRoute);
                   log.trace("point:default"+"  "+defaultRoute.toString());
               }

               return 0;   
        }


}