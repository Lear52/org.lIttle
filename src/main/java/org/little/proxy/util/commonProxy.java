package org.little.proxy.util;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.utilTransform;
import org.little.auth.authUser;
import org.little.auth.authUserLDAP;
/**
 * 
 */
public class commonProxy extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonProxy.class);
       private static commonProxy  cfg = new commonProxy();
       
       private int           type_proxy                     ;
       private int           type_authenticateClients       ;
       private int           bind_port                      ;
       private boolean       transparent                    ;
       private String        default_realm                  ;
       private String        default_domain                  ;
       private String        ldap_ad_username               ;
       private String        ldap_ad_password               ;
       private String        java_security_krb5_conf        ;
       private String        java_security_auth_login_config;
       private listPointHost global_list_host               ; 
       private listHost4User hostlist                       ;
       private listGuest4URL guestlist                      ;
       private listChannel   channels;
       private listCookie    cookie;
       
       private authUser      auth_user                      ;
       
       private String        ldap_url                       ;
       private String        local_bind_client              ;
       private String        local_bind_server              ;
       
       private int           specified_number_of_threads    ;

       private boolean       is_dump_log                    ;
       
       public  static commonProxy  get(){ if(cfg==null)cfg=new commonProxy();return cfg;};
       
       private commonProxy(){clear();}
       
       @Override
       public String  getOldNodeName(){return "littleproxy:configuration";}
       
       @Override
       public void clear(){
              super.clear();
              setNodeName("littleproxy");
              type_proxy                     =0;
              bind_port                      =8080;
              type_authenticateClients       =0;            
              transparent                    =false;
              ldap_ad_username               ="k1svcfarmadmin";       
              ldap_ad_password               ="3edcVFR$";             
              java_security_krb5_conf        ="krb5.conf";            
              java_security_auth_login_config="login.conf";           
              global_list_host               = new listPointHost();
              hostlist                       = new listHost4UserImpl(global_list_host);
              guestlist                      = new listGuest4URLImpl();
              channels                       = new listChannel();
              default_realm                  ="vip.cbr.ru";
              default_domain                  ="vip.cbr.ru";
              auth_user                      = new authUserLDAP();
              cookie                         = new listCookie();
       
              ldap_url                       ="ldap://rdc22-vip01.vip.cbr.ru:3268";
              specified_number_of_threads    = 1;
              local_bind_client              ="*";
              local_bind_server              ="*";
              is_dump_log                    =false;
       }
       private void initGlobal(Node node_cfg){
              if(node_cfg!=null){
                 logger.info("The configuration node:"+node_cfg.getNodeName());
                 NodeList glist=node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("ldap_ad_username"               .equals(n.getNodeName())){ldap_ad_username=n.getTextContent();               logger.info("ldap_ad_username:"+ldap_ad_username);               }
                     else                                                                                                           
                     if("ldap_ad_password"               .equals(n.getNodeName())){ldap_ad_password=n.getTextContent();               logger.info("ldap_ad_password:**********");                      }
                     else                                                                                                           
                     if("ldap_url"                       .equals(n.getNodeName())){ldap_url=n.getTextContent();                       logger.info("ldap_url:"+ldap_url);                               }
                     else
                     if("local_bind_client"              .equals(n.getNodeName())){local_bind_client=n.getTextContent();              logger.info("local_bind_client:"+local_bind_client);             }
                     else
                     if("local_bind_server"              .equals(n.getNodeName())){local_bind_server=n.getTextContent();              logger.info("local_bind_server:"+local_bind_server);             }
                     else
                     if("java_security_krb5_conf"        .equals(n.getNodeName())){java_security_krb5_conf=n.getTextContent();        logger.info("java.security.krb5.conf:"+java_security_krb5_conf); }
                     else
                     if("java_security_auth_login_config".equals(n.getNodeName())){java_security_auth_login_config=n.getTextContent();logger.info("java.security.auth.login.config:"+java_security_auth_login_config);}
                     else
                     if("port"                           .equals(n.getNodeName())){String s=n.getTextContent(); try{bind_port=Integer.parseInt(s, 10);               }catch(Exception e){ bind_port=8080;             logger.error("bind_port:"+s);               } logger.info("Bind port:"+bind_port);}
                     else
                     if("authenticateClients"            .equals(n.getNodeName())){String s=n.getTextContent(); try{type_authenticateClients=Integer.parseInt(s, 10);}catch(Exception e){ type_authenticateClients=0; logger.error("type_authenticateClients:"+s);} logger.info("type_authenticateClients:"+type_authenticateClients);}
                     else
                     if("transparent"                    .equals(n.getNodeName())){String s=n.getTextContent(); try{transparent=Boolean.parseBoolean(s);             }catch(Exception e){ transparent=false;          logger.error("transparent:"+s);             } logger.info("transparent:"+transparent);}
                     else
                     if("default_realm"                  .equals(n.getNodeName())){default_realm=n.getTextContent();   logger.info("Default realm:"+default_realm); }
                     else
                     if("default_domain"                  .equals(n.getNodeName())){default_domain   =n.getTextContent(); logger.info("default_domain:"+default_domain);        }
                     else
                     if("type"                           .equals(n.getNodeName())){
                                                         String s=n.getTextContent(); 
                                                         if("null".equals(s))type_proxy=0; 
                                                         if("http".equals(s))type_proxy=1; 
                                                         logger.info("Default type proxy:"+s); 
                     }
                     else
                     if("threads"                        .equals(n.getNodeName())){String s=n.getTextContent(); try{specified_number_of_threads=Integer.parseInt(s, 10);}catch(Exception e){ specified_number_of_threads=1; } logger.info("threads:"+specified_number_of_threads);}
                     else
                     if("dump_log"                       .equals(n.getNodeName())){String s=n.getTextContent(); try{is_dump_log=Boolean.parseBoolean(s);                }catch(Exception e){ is_dump_log=false;logger.error("dump_log:"+s);} logger.info("dump_log:"+is_dump_log);}
                 }
              }                               
       
              System.setProperty("java.security.krb5.conf",        getPathKbr5()        );
              System.setProperty("java.security.auth.login.config",getPathLogin());
              if(utilTransform.isEmpty(local_bind_server))local_bind_server="*";
              if(utilTransform.isEmpty(local_bind_client))local_bind_server="*";
       
       }
       @Override
       public void init(){
       
              java_security_krb5_conf        ="krb5.conf";
              java_security_auth_login_config="login.conf";
       
              auth_user                      = new authUserLDAP() ;
       
              NodeList list=getNode().getChildNodes();     
              for(int i=0;i<list.getLength();i++){
                  Node n=list.item(i);
                  if("global_option".equals(n.getNodeName())){initGlobal    (n); continue;}
              }
       
              reinit();
       
       }
       @Override
       public void reinit(){
       
              global_list_host               = new listPointHost();
              hostlist                       = new listHost4UserImpl(global_list_host);
              guestlist                      = new listGuest4URLImpl();
       
              NodeList list=getNode().getChildNodes();     
              for(int i=0;i<list.getLength();i++){
                  Node n=list.item(i);
                  if("user_host"    .equals(n.getNodeName())){hostlist.init (n); continue;}
                  if("guest"        .equals(n.getNodeName())){guestlist.init(n); continue;}
                  if("cookie"       .equals(n.getNodeName())){cookie.init(n); continue;}
              }
       
       }
       
       public int           getNumberThreads          (){return specified_number_of_threads;    }
       public int           getType                   (){return type_proxy;                     }
       public int           getPort                   (){return bind_port;                      }
       public int           getTypeAuthenticateClients(){return type_authenticateClients;       }
       public String        getLdapUsername           (){return ldap_ad_username;               }
       public String        getLdapPassword           (){return ldap_ad_password;               }
       public String        getLdapUrl                (){return ldap_url;                       }
       public String        getPathKbr5               (){return java_security_krb5_conf;        }
       public String        getPathLogin              (){return java_security_auth_login_config;}
       public String        getRealm                  (){return default_realm;                  }
       public String        getLocalServerBind        (){return local_bind_server;              }
       public String        getLocalClientBind        (){return local_bind_client;              }
       public listPointHost getPointHost              (){return global_list_host;               }
       public listHost4User getHosts                  (){return hostlist;                       }
       public listGuest4URL getGuest                  (){return guestlist;                      }
       public boolean       isTransparent             (){return transparent;                    }
       public listChannel   getChannel                (){return channels;                       }
       public listCookie    getCookie                 (){return cookie;                         }
       public String        getDefaultDomain          (){return default_domain;                 }

       public boolean       isDumpLog                 (){return is_dump_log;                    }
       
       public boolean       authenticate(String userName, String password){
              boolean ret=auth_user.checkUser(userName,password);
              logger.trace("util:"+userName + " authenticate:"+ret);
              return ret;
       }
       public boolean authenticate(String userName, String pre_serverResponse,String clientResponse){
              boolean u=auth_user.isUser(userName);
              if(u==false)return false;
              String ha1=auth_user.getDigestUser(userName,default_realm);
              String ha2 = utilTransform.getMD5Hash(ha1 + pre_serverResponse); 
       
              return ha2.equals(clientResponse);
              
              //return auth_user.getServerResponse(pre_serverResponse, clientResponse);
              
       }
       public String getFullUserName(String username){return auth_user.getFullUserName(username);};
       
       public void initMBean(){
              MBeanServer mbs    = null;
              //String      domain = null;
              try {
                  // Instantiate the MBean server
                  mbs = ManagementFactory.getPlatformMBeanServer();
                  //mbs = MBeanServerFactory.createMBeanServer();
                  String domain = mbs.getDefaultDomain();
                  logger.info("Default Domain = " + domain);
              } catch (Exception ex) {
                       //mbs    = null;
                       //domain = null;
                       Except ex1=new Except(ex.toString(),ex);
                       logger.error("get factory the statProxyMBean ex:"+ex1);
                      return;
              }
       
	    try {
	         String mbeanClassName      = statProxy.class.getName();
	         String mbeanObjectNameStr  = "Lproxy:type="+mbeanClassName+",index="+bind_port;
       
	         ObjectName mbeanObjectName=ObjectName.getInstance(mbeanObjectNameStr);
       
	         //LOG.info("mbeanObjectName = " + mbeanObjectName);
       
	         mbs.createMBean(mbeanClassName, mbeanObjectName);
	         logger.info("create:" + mbeanObjectNameStr);
       
	    } catch (Exception ex) {
                  Except ex1=new Except(ex.toString(),ex);
                  logger.error("Create the statProxyMBean ex:"+ex1);
                  return;
	    }
       
       
       }
       
}

