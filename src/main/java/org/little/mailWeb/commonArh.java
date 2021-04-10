package org.little.mailWeb;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.string.stringTransform;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonArh  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonArh.class);

       private boolean     debug      ;
       private String      userName   ;
       private String      password   ;
       private String      local_bind_client;
       private String      client_host;
       private int         client_port;
       private int         task_timeout;
       private String      imap_inbox_folder;
       private String      imap_outbox_folder;
       private commonDB    db_cfg;
       private String      def_page;
       private String      error_page;

       public commonArh() {
              clear();
              setNodeName("littlekey");
       }

       @Override
       public void clear() {
              super.clear();             
              debug                  = true;           
              userName               = "av@vip.cbr.ru";
              password               = "123";          
              client_host            = "127.0.0.1";    
              client_port            = 143;            
              local_bind_client      ="*";   
              imap_inbox_folder      ="inbox";              
              imap_outbox_folder     ="outbox";              
              db_cfg                 =new commonDB();
              def_page               ="index.html";
              error_page             ="error.html";
       }
       
       public folderARH getFolder           () {return db_cfg.getFolder(); }

       public boolean  isDebug           () {return debug;             }
       public String   getUser           () {return userName;          }
       public String   getPasswd         () {return password;          }
       public String   getLocalBindClient() {return local_bind_client; }
       public String   getHost           () {return client_host;       }
       public int      getPort           () {return client_port;       }
       public int      getTimeout        () {return task_timeout;      }
       public String   getInboxFolder    () {return imap_inbox_folder; }
       public String   getOutboxFolder   () {return imap_outbox_folder;}
       public commonDB getCfgDB          () {return db_cfg;            }
       public String   getDefPage        () {return def_page;          }
       public String   getErrorPage      () {return error_page;        }


       @Override
       public void init() {
            init(this.getNode());
       }
       @Override
       public void  init(Node _node_cfg) {
              //String log_type="log";
              //Node   n_log=null;
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("global_option".equalsIgnoreCase(n.getNodeName())){initGlobal(n);}
                     else
                     if("log_arh"      .equalsIgnoreCase(n.getNodeName())){db_cfg.init(n);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

       }

       private void initGlobal(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("user"              .equalsIgnoreCase(n.getNodeName())){userName          =n.getTextContent(); logger.info("user:"+userName);}
                     else
                     if("password"          .equalsIgnoreCase(n.getNodeName())){password          =n.getTextContent(); logger.info("password:"+password);}
                     else
                     if("client_port"       .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent(); try{client_port=Integer.parseInt(s, 10);}catch(Exception e){ client_port=143;logger.error("client_port:"+s);} logger.info("client_port:"+client_port);}
                     else                   
                     if("client_host"       .equalsIgnoreCase(n.getNodeName())){client_host       =n.getTextContent(); logger.info("client_host:"+client_host);}
                     else
                     if("local_bind_client" .equalsIgnoreCase(n.getNodeName())){local_bind_client =n.getTextContent(); logger.info("local_bind_client:"+local_bind_client);}
                     else
                     if("is_debug"          .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent(); try{debug=Boolean.parseBoolean(s);}catch(Exception e){ debug=false;logger.error("is_debug:"+s);} logger.info("is_debug:"+debug);}
                     else
                     if("run_timeout"       .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent();try{task_timeout=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set run_timeout:"+s);task_timeout=10;}logger.info("run_timeout:"+task_timeout);}
                     else
                     if("inbox_folder"      .equalsIgnoreCase(n.getNodeName())){imap_inbox_folder =n.getTextContent(); logger.info("inbox_folder:"+imap_inbox_folder);}
                     else
                     if("outbox_folder"     .equalsIgnoreCase(n.getNodeName())){imap_outbox_folder=n.getTextContent(); logger.info("outbox_folder:"+imap_outbox_folder);}
                     else
                     if("def_page"          .equalsIgnoreCase(n.getNodeName())){def_page          =n.getTextContent(); logger.info("def_page:"+def_page);}
                     else
                     if("error_page"        .equalsIgnoreCase(n.getNodeName())){error_page        =n.getTextContent(); logger.info("error_page:"+error_page);}
                 }
                 if(stringTransform.isEmpty(local_bind_client))local_bind_client="*";
              }
              else{
                  logger.error("The configuration node:null");
              }                 

      }


       public static void main(String args[]){
              commonArh mngr=new commonArh();
              String xpath  =args[0];

              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+ver());
              mngr.init();
              logger.info("RUN LITTLE.CONTROLSTREAM "+ver());

       }

}
