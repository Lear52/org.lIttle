package org.little.auth;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonAUTH{

       private static final Logger logger = LoggerFactory.getLogger(commonAUTH.class);

       private int           type_authenticateClients       ;
       private String        ldap_url                       ;
       private String        ldap_ad_username               ;
       private String        ldap_ad_password               ;
       private String        java_security_krb5_conf        ;
       private String        java_security_auth_login_config;
       private String        realm;
       private String        default_domain                 ;
       private boolean       auth_requared                  ; 

      
       public commonAUTH(){clear();}

       public void clear(){
              type_authenticateClients       =0;
              ldap_url                       ="ldap://rdc22-vip01.vip.cbr.ru:3268";
              ldap_ad_username               ="k1svcfarmadmin";       
              ldap_ad_password               ="3edcVFR$";             
              realm                          ="vip.cbr.ru";
              default_domain                 ="vip.cbr.ru";
              java_security_krb5_conf        ="krb5.conf";
              java_security_auth_login_config="login.conf";
              auth_requared                  =true;
       }
       public void init(NodeList glist){
              if(glist==null) return;
              logger.info("The configuration auth");
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("ldap_ad_username"               .equals(n.getNodeName())){ldap_ad_username=n.getTextContent();               logger.info("ldap_ad_username:"+ldap_ad_username);                              }
                  else                                                                                                            
                  if("ldap_ad_password"               .equals(n.getNodeName())){ldap_ad_password=n.getTextContent();               logger.info("ldap_ad_password:**********");                                     }
                  else                                                                                                            
                  if("ldap_url"                       .equals(n.getNodeName())){ldap_url=n.getTextContent();                       logger.info("ldap_url:"+ldap_url);                                              }
                  else
                  if("java_security_krb5_conf"        .equals(n.getNodeName())){java_security_krb5_conf=n.getTextContent();        logger.info("java.security.krb5.conf:"+java_security_krb5_conf);                }
                  else
                  if("java_security_auth_login_config".equals(n.getNodeName())){java_security_auth_login_config=n.getTextContent();logger.info("java.security.auth.login.config:"+java_security_auth_login_config);}
                  else
                  if("default_realm"                  .equals(n.getNodeName())){realm=n.getTextContent();                          logger.info("Default realm:"+realm);                                            }
                  else
                  if("authenticateClients"            .equals(n.getNodeName())){String s=n.getTextContent(); try{type_authenticateClients=Integer.parseInt(s, 10);}catch(Exception e){ type_authenticateClients=0; logger.error("type_authenticateClients:"+s);} logger.info("type_authenticateClients:"+type_authenticateClients);}
                  else
                  if("default_domain"                 .equals(n.getNodeName())){default_domain   =n.getTextContent(); logger.info("default_domain:"+default_domain);        }
                  else
                  if("auth_requared"                  .equals(n.getNodeName())){String s=n.getTextContent(); try{auth_requared=Boolean.parseBoolean(s);}catch(Exception e){auth_requared=true;logger.error("auth_requared:"+s);} logger.info("auth_requared:"+auth_requared);}
                  //else
              }
              System.setProperty("java.security.krb5.conf",        getPathKrb5() );
              System.setProperty("java.security.auth.login.config",getPathLogin());

       }

       public int           getTypeAuthenticateClients(){return type_authenticateClients;       }
       public String        getLdapUrl                (){return ldap_url;                       }
       public String        getRealm                  (){return realm;                          }
       public String        getLdapUsername           (){return ldap_ad_username;               }
       public String        getLdapPassword           (){return ldap_ad_password;               }
       public String        getPathKrb5               (){return java_security_krb5_conf;        }
       public String        getPathLogin              (){return java_security_auth_login_config;}
       public String        getDefaultDomain          (){return default_domain;                 }
       public boolean       getAuthRequared           (){return auth_requared;}


}

