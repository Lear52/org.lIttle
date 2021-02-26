package org.little.rcmd;

import java.util.ArrayList;

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
public class commonAPK{

       private static final Logger logger = LoggerFactory.getLogger(commonAPK.class);

       private Node             node_cfg;
       private NodeList         node_apk;
       private NodeList         node_cmd;

       private static commonAPK cfg = new commonAPK();
       public  static commonAPK get(){ if(cfg==null)cfg=new commonAPK();return cfg;};

       public  rAPK getAPK(String _id_apk){

              for(int i=0;i<node_apk.getLength();i++){
                  Node n=node_apk.item(i);
                  if("node".equals(n.getNodeName())==false)continue;
                  if(n.getAttributes().getNamedItem("id")==null) {
                     logger.error("The configuration apk element: noname");
                     continue;
                  }
                  if(n.getAttributes().getNamedItem("id").getNodeValue().equals(_id_apk)){
                     rAPK rapk=new rAPK(_id_apk);      
                     rapk.initAPK(n);
                     rapk.initCMD(node_cmd);
                     logger.info("The configuration apk element:"+_id_apk);
                     return rapk;
                  }
             }
             return null;
       }

       public rAPK[] getAPK(){
              logger.info("get configuration apk");

              ArrayList<rAPK> list_apk=new ArrayList<rAPK>();
              for(int i=0;i<node_apk.getLength();i++){
                  Node n=node_apk.item(i);
                  //logger.trace("get node:"+n.getNodeName());
                  if("node".equals(n.getNodeName())==false)continue;

                  if(n.getAttributes().getNamedItem("id")==null) {
                     logger.error("The configuration apk element id:noname");
                     continue;
                  }
                  //logger.trace("get node:"+n.getNodeName()+" id");
                  String _id_apk=n.getAttributes().getNamedItem("id").getNodeValue();
                  logger.trace("get node:"+n.getNodeName()+" id:"+_id_apk);
                  rAPK rapk=new rAPK(_id_apk);      

                  rapk.initAPK(n);
                  //logger.trace("init apk node:"+n.getNodeName()+" id:"+_id_apk);
                  rapk.initCMD(node_cmd);
                  list_apk.add(rapk);
                  logger.info("The configuration apk element:"+_id_apk);
              }
           
              rAPK[] list=new rAPK[list_apk.size()]; 
              logger.info("The configuration apk size:"+list_apk.size());
              return list_apk.toArray(list);
       };
       
       
       private commonAPK(){clear();}

       private void clear(){
               node_cfg=null;              
               node_apk=null;              
               node_cmd=null;              
       }
       
       public boolean init(Node _node_cfg){
              if(_node_cfg==null)return false;
              node_cfg=_node_cfg;
              logger.info("The configuration node:"+node_cfg.getNodeName()+" for commonAPK");

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return false;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("apk".equals(n.getNodeName())      ){node_apk=n.getChildNodes();}
                  else
                  if("commanddi".equals(n.getNodeName())){node_cmd=n.getChildNodes();}
              }
              if(node_apk==null || node_cmd==null){
                 logger.error("Error configuration node:"+node_cfg.getNodeName()+" apk==null || commanddi==null");
                 return false;
              }

              logger.info("The configuration node:"+node_cfg.getNodeName()+" for commonAPK apk:"+node_apk.getLength()+" commanddi:"+node_cmd.getLength());
              return true;
       }
       //--------------------------------------------------------------------------------------------------------

       private static String getDefNodeName(){ return "little";};
       private static String getNodeName()   { return "littlecmd";}


       public static boolean loadCFG(String cfg_filename) {
              Node _node_cfg = findCFG(cfg_filename);
              if(_node_cfg==null)return false;
              return commonAPK.get().init(_node_cfg);
              //return true;
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

       public static void main(String[] arg){

              commonAPK.loadCFG("littleproxy_h.xml");


              rAPK r1=commonAPK.get().getAPK("main");
              if(r1==null) {
            	  
              }
              rAPK[] r_list=commonAPK.get().getAPK();
              if(r_list==null) {
            	  
              }
            
       }



}

