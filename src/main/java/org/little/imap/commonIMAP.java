package org.little.imap;


import org.little.auth.commonAUTH;
import org.little.ssl.commonSSL;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.commonServer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonIMAP extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonIMAP.class);
       private static commonIMAP   cfg    = new commonIMAP();
      

       private boolean       case_sensitive_folder          ;
       private commonSSL     ssl_cfg;
       private commonAUTH    auth_cfg; 
       private commonServer  server_cfg;
      
       public  static commonIMAP  get(){ if(cfg==null)cfg=new commonIMAP();return cfg;};
      
       private commonIMAP(){clear();}
      
       @Override
       public void clear(){
              super.clear();
              auth_cfg  =new commonAUTH(); 
              server_cfg=new commonServer();
              ssl_cfg=new commonSSL();
              setNodeName("littleimap");
              case_sensitive_folder          =true;
              
       }
       private void initGlobal(Node node_cfg){
              if(node_cfg!=null){
                 logger.info("The configuration node:"+node_cfg.getNodeName());
                 NodeList glist=node_cfg.getChildNodes();     
                 server_cfg.init(glist);
                 auth_cfg.init(glist);
                 ssl_cfg.init(glist);
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("case_sensitive_folder".equals(n.getNodeName())){String s=n.getTextContent(); try{case_sensitive_folder=Boolean.parseBoolean(s);}catch(Exception e){ case_sensitive_folder=true;logger.error("case_sensitive_folder:"+s);} logger.info("case_sensitive_folder:"+case_sensitive_folder);}
                 }
              }                               
       }
       @Override
       public void init(){
      
              NodeList list=getNode().getChildNodes();     
              for(int i=0;i<list.getLength();i++){
                  Node n=list.item(i);
                  if("global_option".equals(n.getNodeName())){initGlobal    (n); continue;}
              }
      
              reinit();
      
       }
       @Override
       public void reinit(){
      
       }
       @Override
       public void initMBean(){
       }
       
       public boolean       isCaseSensitive           (){return case_sensitive_folder;}
       
       public commonSSL     getCfgSSL   (){return ssl_cfg;   }
       public commonAUTH    getCfgAuth  (){return auth_cfg;  }
       public commonServer  getCfgServer(){return server_cfg;}
       
       
}

