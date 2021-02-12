package org.little.rcmd;

import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

/**
 * 
 */
public class commonAPK{

       private static final Logger logger = LoggerFactory.getLogger(commonAPK.class);

       private ArrayList<rAPK> list_apk; 

       private static commonAPK       cfg = new commonAPK();
       public  static commonAPK       get(){ if(cfg==null)cfg=new commonAPK();return cfg;};


       public commonAPK(){clear();}

       public void clear(){
              list_apk=new ArrayList<rAPK>(); 
       }
       public ArrayList<rAPK>   getAPK() {return list_apk;}
       
       public void init(Node node_cfg){
           if(node_cfg==null)return;
           logger.info("The configuration node:"+node_cfg.getNodeName());
           NodeList glist=node_cfg.getChildNodes();     
           init(glist);
       }
       public void init(NodeList glist){
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  //else
              }
              logger.info("The configuration rcmd global");
       }




}

