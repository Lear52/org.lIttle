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
      
       //private int           bind_port                      ;
       //private String        local_bind_client              ;
       //private String        local_bind_server              ;

       //private String        ldap_url                       ;
       //private String        ldap_ad_username               ;
       //private String        ldap_ad_password               ;
       //private String        default_domain                 ;

       private boolean       case_sensitive_folder          ;
       //private boolean       is_dump_log                    ;
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
              //bind_port=1143;
              //local_bind_client              ="*";
              //local_bind_server              ="*";
              //is_dump_log                    =false;
              //ldap_ad_username               ="k1svcfarmadmin";       
              //ldap_ad_password               ="3edcVFR$";             
              //default_domain                 ="vip.cbr.ru";
              //ldap_url                       ="ldap://rdc22-vip01.vip.cbr.ru:3268";
              
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
                     //else
                     //if("port"                 .equals(n.getNodeName())){String s=n.getTextContent(); try{bind_port=Integer.parseInt(s, 10);             }catch(Exception e){ bind_port=8080; logger.error("bind_port:"+s);} logger.info("Bind port:"+bind_port);}
                     //else                     
                     //if("local_bind_client"    .equals(n.getNodeName())){local_bind_client=n.getTextContent(); logger.info("local_bind_client:"+local_bind_client);  }
                     //else                     
                     //if("local_bind_server"    .equals(n.getNodeName())){local_bind_server=n.getTextContent(); logger.info("local_bind_server:"+local_bind_server);  }
                     //else
                     //if("default_domain"       .equals(n.getNodeName())){default_domain   =n.getTextContent(); logger.info("default_domain:"+default_domain);        }
                     //else
                     //if("dump_log"                       .equals(n.getNodeName())){String s=n.getTextContent(); try{is_dump_log=Boolean.parseBoolean(s);                }catch(Exception e){ is_dump_log=false;logger.error("dump_log:"+s);} logger.info("dump_log:"+is_dump_log);}
                     //else                     
                     //if("ldap_url"             .equals(n.getNodeName())){ldap_url=n.getTextContent();          logger.info("ldap_url:"+ldap_url);                    }
                     //else                                                                                                               
                     //if("ldap_ad_username"     .equals(n.getNodeName())){ldap_ad_username=n.getTextContent();  logger.info("ldap_ad_username:"+ldap_ad_username);    }
                     //else                                                                                                  
                     //if("ldap_ad_password"     .equals(n.getNodeName())){ldap_ad_password=n.getTextContent();  logger.info("ldap_ad_password:**********");           }
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
       
       //public int           getPort(){return server_cfg.getPort();}
       //public String        getLocalServerBind        (){return server_cfg.getLocalServerBind();    }
       //public String        getLocalClientBind        (){return server_cfg.getLocalClientBind();    }
       //public String        getLdapUsername           (){return auth_cfg.getLdapUsername();     }
       //public String        getLdapPassword           (){return auth_cfg.getLdapPassword();     }
       //public String        getLdapUrl                (){return auth_cfg.getLdapUrl();             }
       //public String        getDefaultDomain          (){return auth_cfg.getDefaultDomain();       }
       //public boolean       isSSL                     (){return ssl_cfg.isSSL();                }
       //public boolean       isDumpLog                 (){return server_cfg.isDumpLog();          }
/*
       public int           getPort(){return bind_port;}
       public String        getLdapUsername           (){return ldap_ad_username;     }
       public String        getLdapPassword           (){return ldap_ad_password;     }
       public String        getLdapUrl                (){return ldap_url;             }
       public String        getLocalServerBind        (){return local_bind_server;    }
       public String        getLocalClientBind        (){return local_bind_client;    }
       public String        getDefaultDomain          (){return default_domain;       }
       public boolean       isSSL                     (){return false;                }
       public boolean       isCaseSensitive           (){return case_sensitive_folder;}
       public boolean       isDumpLog                 (){return is_dump_log;          }
  
 
 */
       public commonSSL     getCfgSSL   (){return ssl_cfg;   }
       public commonAUTH    getCfgAuth  (){return auth_cfg;  }
       public commonServer  getCfgServer(){return server_cfg;}
       
       
}

