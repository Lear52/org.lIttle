package org.little.proxy;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

//import org.little.auth.authUser;
//import org.little.auth.authUserLDAP;
import org.little.auth.commonAUTH;
import org.little.http.auth.commonHttpAuth;
import org.little.proxy.util.listChannel;
import org.little.proxy.util.listCookie;
import org.little.proxy.util.listGuest4URL;
import org.little.proxy.util.listGuest4URLImpl;
import org.little.proxy.util.listHost4User;
import org.little.proxy.util.listHost4UserImpl;
import org.little.proxy.util.listPointHost;
import org.little.proxy.util.statProxy;
import org.little.ssl.commonSSL;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.commonServer;
import org.little.util.string.stringTransform;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

/**
 * 
 */
public class commonProxy extends common{

       private static final Logger logger = LoggerFactory.getLogger(commonProxy.class);
       private static commonProxy  cfg    = new commonProxy();
       
       private listPointHost  global_list_host               ; 
       private listHost4User  hostlist                       ;
       private listGuest4URL  guestlist                      ;
       private listChannel    channels;
       private listCookie     cookie;
       
       //private authUser       auth_user                      ;
       
       private int            type_proxy                     ;
       private boolean        transparent                    ;
       private int            specified_number_of_threads    ;
       private int            reverse_port                   ;
       private String         reverse_host                   ;

       private commonSSL      ssl_cfg;
       private commonAUTH     auth_cfg; 
       private commonServer   server_cfg;
       private commonHttpAuth auth_http_cfg; 

       public static final int PROXY_TYPE_NULL=0;
       public static final int PROXY_TYPE_HTTP=1;

       public static final String S_PROXY_TYPE_NULL="http";
       public static final String S_PROXY_TYPE_HTTP="null";
       
       public  static commonProxy  get(){ if(cfg==null)cfg=new commonProxy();return cfg;};
       
       private commonProxy(){clear();}
       
       @Override
       public String  getOldNodeName(){return "littleproxy:configuration";}
       
       @Override
       public void clear(){

              super.clear();

              auth_cfg     =new commonAUTH(); 
              server_cfg   =new commonServer();
              ssl_cfg      =new commonSSL();
              auth_http_cfg=new commonHttpAuth();
              
              setNodeName("littleproxy");

              type_proxy                     =PROXY_TYPE_NULL;
              transparent                    =false;
              specified_number_of_threads    = 1;
              reverse_port                   =8081;
              reverse_host                   ="*";


              global_list_host               = new listPointHost();
              hostlist                       = new listHost4UserImpl(global_list_host);
              guestlist                      = new listGuest4URLImpl();
              channels                       = new listChannel();
              //auth_user                      = new authUserLDAP(commonProxy.get().getCfgAuth());
              cookie                         = new listCookie();
       
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
                     if("transparent"                    .equals(n.getNodeName())){String s=n.getTextContent(); try{transparent=Boolean.parseBoolean(s);             }catch(Exception e){ transparent=false;          logger.error("transparent:"+s);             } logger.info("transparent:"+transparent);}
                     else
                     if("type"                           .equals(n.getNodeName())){
                                                         String s=n.getTextContent(); 
                                                         if(S_PROXY_TYPE_NULL.equals(s))type_proxy=PROXY_TYPE_NULL; 
                                                         if(S_PROXY_TYPE_HTTP.equals(s))type_proxy=PROXY_TYPE_HTTP; 
                                                         logger.info("Default type proxy:"+s); 
                     }
                     else
                     if("threads"                        .equals(n.getNodeName())){String s=n.getTextContent(); try{specified_number_of_threads=Integer.parseInt(s, 10);}catch(Exception e){ specified_number_of_threads=1; } logger.info("threads:"+specified_number_of_threads);}
                     else
                     if("reverse_port"                   .equals(n.getNodeName())){String s=n.getTextContent(); try{reverse_port               =Integer.parseInt(s, 10);}catch(Exception e){ reverse_port=8081; } logger.info("reverse_port:"+reverse_port);}
                     else
                     if("reverse_host"                   .equals(n.getNodeName())){reverse_host=n.getTextContent();  logger.info("reverse_host:"+reverse_host);}

                 }
              }                               
              //logger.trace("reverse server:"+getReverseHost()+":"+getReversePort());
       
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
       
              global_list_host               = new listPointHost();
              hostlist                       = new listHost4UserImpl(global_list_host);
              guestlist                      = new listGuest4URLImpl();
       
              NodeList list=getNode().getChildNodes();     
              for(int i=0;i<list.getLength();i++){
                  Node n=list.item(i);
                  if("user_host"    .equals(n.getNodeName())){hostlist.init (n); continue;}
                  if("guest"        .equals(n.getNodeName())){guestlist.init(n); continue;}
                  if("cookie"       .equals(n.getNodeName())){cookie.init(n);    continue;}
              }
       
       }
       
       public listPointHost  getPointHost              (){return global_list_host;               }
       public listHost4User  getHosts                  (){return hostlist;                       }
       public listGuest4URL  getGuest                  (){return guestlist;                      }
       public listChannel    getChannel                (){return channels;                       }
       public listCookie     getCookie                 (){return cookie;                         }
                            
       public boolean        isTransparent             (){return transparent;                    }
       public int            getNumberThreads          (){return specified_number_of_threads;    }
       public int            getType                   (){return type_proxy;                     }
       public int            getReversePort            (){return reverse_port;                   }
       public String         getReverseHost            (){return reverse_host;                   }
       
       public commonSSL      getCfgSSL                 (){return ssl_cfg;                        }
       public commonAUTH     getCfgAuth                (){return auth_cfg;                       }
       public commonServer   getCfgServer              (){return server_cfg;                     }
       public commonHttpAuth getCfgHttpAuth            (){return auth_http_cfg;                  }

       public boolean authenticate(String userName, String password){
              boolean ret=getCfgAuth().getAuthUser().checkUser(userName,password);
              logger.trace("util:"+userName + " authenticate:"+ret);
              return ret;
       }
       public boolean authenticate(String userName, String pre_serverResponse,String clientResponse){
              boolean u=getCfgAuth().getAuthUser().isUser(userName);
              if(u==false)return false;
              String ha1=getCfgAuth().getAuthUser().getDigestUser(userName,auth_cfg.getRealm());
              String ha2 = stringTransform.getMD5Hash(ha1 + pre_serverResponse); 
       
              return ha2.equals(clientResponse);
       }
       public String getFullUserName(String username){return getCfgAuth().getAuthUser().getFullUserName(username);};
       
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
                   String mbeanObjectNameStr  = "Lproxy:type="+mbeanClassName+",index="+server_cfg.getPort();
             
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

