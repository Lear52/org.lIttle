package org.little.http;

//import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.commonServer;
import org.little.auth.commonAUTH;
import org.little.ssl.commonSSL;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonHTTP extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonHTTP.class);
       private static commonHTTP   cfg = new commonHTTP();
      
       //private int           bind_port                      ;
       //private int           type_authenticateClients       ;
       //private String        ldap_url                       ;
       //private String        ldap_ad_username               ;
       //private String        ldap_ad_password               ;
       //private String        java_security_krb5_conf        ;
       //private String        java_security_auth_login_config;
       //private String        realm;
       //private String        local_bind_client              ;
       //private String        local_bind_server              ;
       //private String        default_domain                 ;
       //private boolean       is_dump_log                    ;
       private String        root_document                  ;
       private String        app_name                       ;
       private commonSSL     ssl_cfg;
       private commonAUTH    auth_cfg; 
       private commonServer  server_cfg;

       public  static commonHTTP  get(){ if(cfg==null)cfg=new commonHTTP();return cfg;};
      
       private commonHTTP(){clear();}

       @Override
       public void preinit(){
              super.preinit(); 
       }
      
       @Override
       public void clear(){
              super.clear();
              auth_cfg  =new commonAUTH(); 
              server_cfg=new commonServer();
              ssl_cfg=new commonSSL();
              setNodeName("littlehttp");
              //bind_port                      =2525;
              //type_authenticateClients       =0;
              //realm                          ="vip.cbr.ru";
              //ldap_url                       ="ldap://rdc22-vip01.vip.cbr.ru:3268";
              //local_bind_client              ="*";
              //local_bind_server              ="*";
              //ldap_ad_username               ="k1svcfarmadmin";       
              //ldap_ad_password               ="3edcVFR$";             
              //default_domain                 ="vip.cbr.ru";
              //is_dump_log                    =false;
              root_document                  ="";
              app_name                       ="appkeystore";
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
                     //if("ldap_ad_username"               .equals(n.getNodeName())){ldap_ad_username=n.getTextContent();               logger.info("ldap_ad_username:"+ldap_ad_username);                              }
                     //else                                                                                                            
                     //if("ldap_ad_password"               .equals(n.getNodeName())){ldap_ad_password=n.getTextContent();               logger.info("ldap_ad_password:**********");                                     }
                     //else                                                                                                            
                     //if("ldap_url"                       .equals(n.getNodeName())){ldap_url=n.getTextContent();                       logger.info("ldap_url:"+ldap_url);                                              }
                     //else
                     //if("java_security_krb5_conf"        .equals(n.getNodeName())){java_security_krb5_conf=n.getTextContent();        logger.info("java.security.krb5.conf:"+java_security_krb5_conf);                }
                     //else
                     //if("java_security_auth_login_config".equals(n.getNodeName())){java_security_auth_login_config=n.getTextContent();logger.info("java.security.auth.login.config:"+java_security_auth_login_config);}
                     //else
                     //if("default_realm"                  .equals(n.getNodeName())){realm=n.getTextContent();                          logger.info("Default realm:"+realm);                                            }
                     //else
                     //if("port"                           .equals(n.getNodeName())){String s=n.getTextContent(); try{bind_port=Integer.parseInt(s, 10);               }catch(Exception e){ bind_port=8080;            logger.error("bind default port:"+bind_port);} logger.info("Bind cfg port:"+bind_port);                          }
                     //else
                     //if("authenticateClients"            .equals(n.getNodeName())){String s=n.getTextContent(); try{type_authenticateClients=Integer.parseInt(s, 10);}catch(Exception e){ type_authenticateClients=0; logger.error("type_authenticateClients:"+s);} logger.info("type_authenticateClients:"+type_authenticateClients);}
                     //else
                     //if("local_bind_client"              .equals(n.getNodeName())){local_bind_client=n.getTextContent();              logger.info("local_bind_client:"+local_bind_client);             }
                     //else
                     //if("local_bind_server"              .equals(n.getNodeName())){local_bind_server=n.getTextContent();              logger.info("local_bind_server:"+local_bind_server);             }
                     //else
                     //if("default_domain"                  .equals(n.getNodeName())){default_domain   =n.getTextContent(); logger.info("default_domain:"+default_domain);        }
                     //else
                     //if("dump_log"                       .equals(n.getNodeName())){String s=n.getTextContent(); try{is_dump_log=Boolean.parseBoolean(s);                }catch(Exception e){ is_dump_log=false;logger.error("dump_log:"+s);} logger.info("dump_log:"+is_dump_log);}
                     if("root_document"         .equals(n.getNodeName())){root_document=n.getTextContent();              logger.info("root_document:"+root_document);             }
                     else
                     if("app_name"              .equals(n.getNodeName())){app_name=n.getTextContent();              logger.info("app_name:"+app_name);             }
      
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

       public String        getRootDocument           (){return root_document;                  }
       public String        getAppName                (){return app_name;                       }

       //public int           getPort                   (){return server_cfg.getPort();           }
      // public String        getLocalServerBind        (){return server_cfg.getLocalServerBind();}
       //public String        getLocalClientBind        (){return server_cfg.getLocalClientBind();}
       //public boolean       isDumpLog                 (){return server_cfg.isDumpLog();         }
       
       //public boolean       isSSL                     (){return ssl_cfg.isSSL();                }

       //public String        getDefaultDomain          (){return auth_cfg.getDefaultDomain();          }
       //public int           getTypeAuthenticateClients(){return auth_cfg.getTypeAuthenticateClients();}
       //public String        getLdapUrl                (){return auth_cfg.getLdapUrl();                }
       //public String        getRealm                  (){return auth_cfg.getRealm();                  }
       //public String        getLdapUsername           (){return auth_cfg.getLdapUsername();           }
       //public String        getLdapPassword           (){return auth_cfg.getLdapPassword();           }
       //public String        getJavaKrb5_conf          (){return auth_cfg.getPathKrb5();          }
       //public String        getJavaLogin_config       (){return auth_cfg.getPathLogin();       }
/*
       public int           getPort                   (){return bind_port;                      }
       public String        getLocalServerBind        (){return local_bind_server;              }
       public String        getLocalClientBind        (){return local_bind_client;              }

       public String        getDefaultDomain          (){return default_domain;                 }
       public int           getTypeAuthenticateClients(){return type_authenticateClients;       }
       public String        getLdapUrl                (){return ldap_url;                       }
       public String        getRealm                  (){return realm;                          }
       public String        getLdapUsername           (){return ldap_ad_username;               }
       public String        getLdapPassword           (){return ldap_ad_password;               }
       public String        getJavaKrb5_conf          (){return java_security_krb5_conf;        }
       public String        getJavaLogin_config       (){return java_security_auth_login_config;}
*/
       public commonSSL     getCfgSSL   (){return ssl_cfg;   }
       public commonAUTH    getCfgAuth  (){return auth_cfg;  }
       public commonServer  getCfgServer(){return server_cfg;}

}

