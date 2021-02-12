package org.little.http;

//import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.commonServer;
import org.little.auth.commonAUTH;
import org.little.http.auth.commonHttpAuth;
import org.little.ssl.commonSSL;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * config for http server
 */
public class commonHTTP extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonHTTP.class);
       private static commonHTTP   cfg = new commonHTTP();
      
       private String         root_document                  ;
       private String         app_name                       ;
       private commonSSL      ssl_cfg;
       private commonAUTH     auth_cfg; 
       private commonServer   server_cfg;
       private commonHttpAuth auth_http_cfg; 

       public  static commonHTTP  get(){ if(cfg==null)cfg=new commonHTTP();return cfg;};
      
       private commonHTTP(){clear();}

       @Override
       public void preinit(){
              super.preinit(); 
       }
      
       @Override
       public void clear(){
              super.clear();
              auth_cfg     =new commonAUTH(); 
              server_cfg   =new commonServer();
              ssl_cfg      =new commonSSL();
              auth_http_cfg=new commonHttpAuth();

              setNodeName("littlehttp");
              root_document="";
              app_name     ="appkeystore";
       }
       private void initGlobal(Node node_cfg){
              if(node_cfg!=null){
                 logger.info("The configuration node:"+node_cfg.getNodeName());
                 NodeList glist=node_cfg.getChildNodes();     

                 server_cfg.init(glist);
                 auth_cfg.init(glist);
                 ssl_cfg.init(glist);
                 auth_http_cfg.init(glist);

                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("root_document"         .equals(n.getNodeName())){root_document=n.getTextContent();logger.info("root_document:"+root_document);}
                     else
                     if("app_name"              .equals(n.getNodeName())){app_name=n.getTextContent();     logger.info("app_name:"+app_name);          }
                     //else
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
       public void reinit(){}
       @Override
       public void initMBean(){}

       public String         getRootDocument           (){return root_document;                  }
       public String         getAppName                (){return app_name;                       }

       public commonSSL      getCfgSSL     (){return ssl_cfg;   }
       public commonAUTH     getCfgAuth    (){return auth_cfg;  }
       public commonServer   getCfgServer  (){return server_cfg;}
       public commonHttpAuth getCfgHttpAuth(){return auth_http_cfg;}

}

