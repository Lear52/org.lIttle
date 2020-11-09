package org.little.smtp;


import org.little.smtp.store.MailStore;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonSMTP extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonSMTP.class);
       private static commonSMTP  cfg=new commonSMTP();
      
       private int           bind_port                      ;
       private String        ldap_url                       ;
       private String        ldap_ad_username               ;
       private String        ldap_ad_password               ;
       private String        local_bind_client              ;
       private String        local_bind_server              ;
       private boolean       case_sensitive_folder  ;
      
       private String        default_domain                  ;
      
       public  static commonSMTP  get(){ if(cfg==null)cfg=new commonSMTP();return cfg;};
      
       private commonSMTP(){clear();}
      
       @Override
       public void clear(){
              super.clear();
              setNodeName("littlesmtp");
              bind_port =2525;
              ldap_url                       ="ldap://rdc22-vip01.vip.cbr.ru:3268";
              local_bind_client              ="*";
              local_bind_server              ="*";
              ldap_ad_username               ="k1svcfarmadmin";       
              ldap_ad_password               ="3edcVFR$";    
              default_domain                  ="vip.cbr.ru";
              case_sensitive_folder          =true;
              
       }
       private void initGlobal(Node node_cfg){
              if(node_cfg!=null){
                 logger.info("The configuration node:"+node_cfg.getNodeName());
                 NodeList glist=node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("port"             .equals(n.getNodeName())){String s         =n.getTextContent(); try{bind_port=Integer.parseInt(s, 10);              }catch(Exception e){ bind_port=8080;             logger.error("bind_port:"+s);               } logger.info("Bind port:"+bind_port);}
                     else
                     if("ldap_url"         .equals(n.getNodeName())){ldap_url         =n.getTextContent(); logger.info("ldap_url:"+ldap_url);                  }
                     else                                                                                                            
                     if("ldap_ad_username" .equals(n.getNodeName())){ldap_ad_username =n.getTextContent(); logger.info("ldap_ad_username:"+ldap_ad_username);  }
                     else                                                                                                            
                     if("ldap_ad_password" .equals(n.getNodeName())){ldap_ad_password =n.getTextContent(); logger.info("ldap_ad_password:**********");         }
                     else
                     if("local_bind_client".equals(n.getNodeName())){local_bind_client=n.getTextContent(); logger.info("local_bind_client:"+local_bind_client);}
                     else
                     if("default_domain"     .equals(n.getNodeName())){default_domain   =n.getTextContent(); logger.info("default_domain:"+default_domain);        }
                     else
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
       public int           getPort(){return bind_port;}
       public String        getLdapUsername           (){return ldap_ad_username;               }
       public String        getLdapPassword           (){return ldap_ad_password;               }
       public String        getLdapUrl                (){return ldap_url;                       }
       public String        getLocalServerBind        (){return local_bind_server;              }
       public String        getLocalClientBind        (){return local_bind_client;              }
       public String        getDefaultDomain          (){return default_domain;                 }
      
       public int           getDefaultDataLen()    {return 1001;} // 1000 or 1001 for extra .. -> . handling?
       public int           getDefaultCommandLen() {return 512; }
      
       //public String        getDomain()            {return "localhost";}
       //public File   getMailDir()           {return new File(System.getProperty("user.home"), "nsmtp");}
       public MailStore        getMailDB()            {return new MailStore();	}
      
       public String        getTlsKeyFile()        {return "certificates.jks";}
       public String        getTlsTrustStoreFile() {return null;	}
       public String        getTlsKeyPassword()    {return "123456";}
       
       public boolean       verifyUser(String username) {return true;}
       public boolean       isCaseSensitive           (){return case_sensitive_folder;}
      
      
      
}

