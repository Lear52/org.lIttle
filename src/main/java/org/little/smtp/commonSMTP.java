package org.little.smtp;


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
public class commonSMTP extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonSMTP.class);
       private static commonSMTP   cfg=new commonSMTP();
      

       private boolean       case_sensitive_folder;
       private boolean       is_proxy             ;
       private String        client_host          ;
       private int           client_port          ;
       private boolean       auth_requared        ; 

       private commonSSL     ssl_cfg;
       private commonAUTH    auth_cfg; 
       private commonServer  server_cfg;
      
       public  static commonSMTP  get(){ if(cfg==null)cfg=new commonSMTP();return cfg;};
      
       private commonSMTP(){clear();}
      
       @Override
       public void clear(){
              super.clear();
              auth_cfg  =new commonAUTH(); 
              server_cfg=new commonServer();
              ssl_cfg=new commonSSL();

              setNodeName("littlesmtp");

              client_host                    ="127.0.0.1";
              client_port                    =25;
              case_sensitive_folder          =true;
              is_proxy                       =false;
              auth_requared                  =true;
              
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
                     if("client_port"          .equals(n.getNodeName())){String s         =n.getTextContent(); try{client_port=Integer.parseInt(s, 10);            }catch(Exception e){ client_port=25;               logger.error("client_port:"+s);     } logger.info("client_port:"+client_port);}
                     else                     
                     if("client_host"          .equals(n.getNodeName())){client_host      =n.getTextContent(); logger.info("client_host:"+client_host);               }
                     else
                     if("case_sensitive_folder".equals(n.getNodeName())){String s=n.getTextContent(); try{case_sensitive_folder=Boolean.parseBoolean(s);}catch(Exception e){ case_sensitive_folder=true;logger.error("case_sensitive_folder:"+s);} logger.info("case_sensitive_folder:"+case_sensitive_folder);}
                     else
                     if("smtp_proxy"           .equals(n.getNodeName())){String s=n.getTextContent(); try{is_proxy=Boolean.parseBoolean(s);}catch(Exception e){ is_proxy=false;logger.error("smtp_proxy:"+s);} logger.info("smtp_proxy:"+is_proxy);}
                     else
                     if("auth_requared"        .equals(n.getNodeName())){String s=n.getTextContent(); try{auth_requared=Boolean.parseBoolean(s);}catch(Exception e){auth_requared=true;logger.error("auth_requared:"+s);} logger.info("auth_requared:"+auth_requared);}
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
       public int           getPort(){return server_cfg.getPort();}
       public String        getLocalServerBind        (){return server_cfg.getLocalServerBind();              }
       public String        getLocalClientBind        (){return server_cfg.getLocalClientBind();              }

       public int           getClientPort             (){return client_port;                    }
       public String        getClientHost             (){return client_host;                    }
      
       public int           getDefaultDataLen()    {return 1001;} // 1000 or 1001 for extra .. -> . handling?
       public int           getDefaultCommandLen() {return 512; }
      
      
       public String        getTlsKeyFile()        {return "certificates.jks";}
       public String        getTlsTrustStoreFile() {return null;    }
       public String        getTlsKeyPassword()    {return "123456";}
       
       public boolean       verifyUser(String username) {return true;}
       public boolean       isCaseSensitive           (){return case_sensitive_folder;}
       public boolean       isProxy                   (){return is_proxy;}
       public boolean       isDumpLog                 (){return server_cfg.isDumpLog();}
      
       public boolean       getAuthRequared(){return auth_requared;}
      
       public String        getLdapUsername           (){return auth_cfg.getLdapUsername();     }
       public String        getLdapPassword           (){return auth_cfg.getLdapPassword();     }
       public String        getLdapUrl                (){return auth_cfg.getLdapUrl();          }
       public String        getDefaultDomain          (){return auth_cfg.getDefaultDomain();    }

       public commonSSL     getCfgSSL   (){return ssl_cfg;   }
       public commonAUTH    getCfgAuth  (){return auth_cfg;  }
       public commonServer  getCfgServer(){return server_cfg;}

}

