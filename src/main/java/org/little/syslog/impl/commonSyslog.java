package org.little.syslog.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

/**
 * 
 */
public class commonSyslog{

       private static final Logger logger = LoggerFactory.getLogger(commonSyslog.class);

       private Node          node_cfg;
       private String        def_page;
       private String        error_page;
       private int           port;
       private Node          mq_node_cfg;
       private boolean       forward_mq;



       private static commonSyslog cfg = new commonSyslog();
       public  static commonSyslog get(){ if(cfg==null)cfg=new commonSyslog();return cfg;};
       
       private commonSyslog(){clear();}

       private void clear(){
               node_cfg   =null;            
               def_page   ="index.jsp";   
               error_page ="error.jsp"; 
               port       =9898;       
               mq_node_cfg=null;
               forward_mq =false;   
       }
       
       public boolean init(Node _node_cfg){
              if(_node_cfg==null)return false;
              node_cfg=_node_cfg;
              logger.info("The configuration node:"+node_cfg.getNodeName()+" for commonSyslog");

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return false;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("global_option".equalsIgnoreCase(n.getNodeName())){initGlobal(n);}
                  if("mq_option".equalsIgnoreCase(n.getNodeName())){mq_node_cfg=n;}
              }

              logger.info("The configuration node:"+node_cfg.getNodeName()+" for commonSyslog ");
              return true;
       }
       private void initGlobal(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("port"       .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent(); try{port=Integer.parseInt(s, 10);}catch(Exception e){ port=9898;logger.error("port:"+s);} logger.info("port:"+port);}
                     else
                     if("forward_mq" .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent(); try{forward_mq=Boolean.parseBoolean(s);}catch(Exception e){ forward_mq=false;logger.error("forward_mq:"+s);} logger.info("forward_mq:"+forward_mq);}
                     else
                     if("def_page"   .equalsIgnoreCase(n.getNodeName())){def_page          =n.getTextContent(); logger.info("def_page:"+def_page);}
                     else
                     if("error_page" .equalsIgnoreCase(n.getNodeName())){error_page        =n.getTextContent(); logger.info("error_page:"+error_page);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

       }
       public String       getDefPage        () {return def_page;          }
       public String       getErrorPage      () {return error_page;        }
       public int          getPort           () {return port;              }
       public Node         getMQNode         () { return mq_node_cfg;      }
       public boolean      isForwardMQ       () { return forward_mq;       }

       //--------------------------------------------------------------------------------------------------------

       private static String getDefNodeName(){ return "little";};
       private static String getNodeName()   { return "littlesyslog";}


       public static boolean loadCFG(String cfg_filename) {
              Node _node_cfg = findCFG(cfg_filename);
              if(_node_cfg==null)return false;
              return commonSyslog.get().init(_node_cfg);
       }

       private static Node findCFG(String cfg_filename) {
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               try {
                     DocumentBuilder builder;
                     builder  = factory.newDocumentBuilder();
                     Document doc = builder.parse(cfg_filename);
                     logger.trace("open doc:"+cfg_filename);
                     //-----------------------------------------------------------------------
                     Node node_cfg = doc.getFirstChild();
                     //-----------------------------------------------------------------------
                     if(getNodeName().equals(node_cfg.getNodeName())){
                        logger.trace("config structure name:"+getNodeName());
                        return node_cfg;
                     }
                     //-----------------------------------------------------------------------
                     if(getDefNodeName().equals(node_cfg.getNodeName())){
                         logger.trace("default config structure name:"+getDefNodeName());
                         logger.trace("seach topic name:"+getNodeName());
                         NodeList glist=node_cfg.getChildNodes();     
                         for(int i=0;i<glist.getLength();i++){
                             Node n=glist.item(i);
                             //logger.trace("topic["+i+"] name:"+n.getNodeName());
                             if(getNodeName().equals(n.getNodeName())) {
                                      //logger.trace("topic["+i+"] is name:"+getNodeName());
                                      return n;
                             }
                         }
                     } 
                     //-----------------------------------------------------------------------
               }
               catch(Exception e) {
                     logger.error("Could not load xml config file:"+cfg_filename, e);
                     return null;
               }
               return null;
       }


       //--------------------------------------------------------------------------------------------------------




}

