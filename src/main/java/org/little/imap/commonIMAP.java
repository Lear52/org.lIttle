package org.little.imap;


import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonIMAP extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonIMAP.class);
       private static commonIMAP  cfg=new commonIMAP();
      
       private int           bind_port                      ;
       private String        ldap_url                       ;
       private String        ldap_ad_username               ;
       private String        ldap_ad_password               ;
       private String        local_bind_client              ;
       private String        local_bind_server              ;
       private String        default_domain                 ;
       private boolean       case_sensitive_folder          ;
       private boolean       is_dump_log                    ;
      
      
       public  static commonIMAP  get(){ if(cfg==null)cfg=new commonIMAP();return cfg;};
      
       private commonIMAP(){clear();}
      
       @Override
       public void clear(){
              super.clear();
              setNodeName("littleimap");
              bind_port=1143;
              ldap_url                       ="ldap://rdc22-vip01.vip.cbr.ru:3268";
              local_bind_client              ="*";
              local_bind_server              ="*";
              ldap_ad_username               ="k1svcfarmadmin";       
              ldap_ad_password               ="3edcVFR$";             
              default_domain                 ="vip.cbr.ru";
              case_sensitive_folder          =true;
              is_dump_log                    =false;
              
       }
       private void initGlobal(Node node_cfg){
              if(node_cfg!=null){
                 logger.info("The configuration node:"+node_cfg.getNodeName());
                 NodeList glist=node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("port"                 .equals(n.getNodeName())){String s=n.getTextContent(); try{bind_port=Integer.parseInt(s, 10);             }catch(Exception e){ bind_port=8080; logger.error("bind_port:"+s);} logger.info("Bind port:"+bind_port);}
                     else                     
                     if("ldap_url"             .equals(n.getNodeName())){ldap_url=n.getTextContent();          logger.info("ldap_url:"+ldap_url);                    }
                     else                                                                                                               
                     if("ldap_ad_username"     .equals(n.getNodeName())){ldap_ad_username=n.getTextContent();  logger.info("ldap_ad_username:"+ldap_ad_username);    }
                     else                                                                                                  
                     if("ldap_ad_password"     .equals(n.getNodeName())){ldap_ad_password=n.getTextContent();  logger.info("ldap_ad_password:**********");           }
                     else                     
                     if("local_bind_client"    .equals(n.getNodeName())){local_bind_client=n.getTextContent(); logger.info("local_bind_client:"+local_bind_client);  }
                     else                     
                     if("local_bind_server"    .equals(n.getNodeName())){local_bind_server=n.getTextContent(); logger.info("local_bind_server:"+local_bind_server);  }
                     else
                     if("default_domain"       .equals(n.getNodeName())){default_domain   =n.getTextContent(); logger.info("default_domain:"+default_domain);        }
                     else
                     if("case_sensitive_folder".equals(n.getNodeName())){String s=n.getTextContent(); try{case_sensitive_folder=Boolean.parseBoolean(s);}catch(Exception e){ case_sensitive_folder=true;logger.error("case_sensitive_folder:"+s);} logger.info("case_sensitive_folder:"+case_sensitive_folder);}
                     else
                     if("dump_log"                       .equals(n.getNodeName())){String s=n.getTextContent(); try{is_dump_log=Boolean.parseBoolean(s);                }catch(Exception e){ is_dump_log=false;logger.error("dump_log:"+s);} logger.info("dump_log:"+is_dump_log);}
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
      
}

