package org.little.mailWeb;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.string.stringTransform;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonIMAPClient  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonIMAPClient.class);

       private boolean     debug      ;
       private String      userName   ;
       private String      password   ;
       private String      local_bind_client;
       private String      client_host;
       private int         client_port;
       private int         task_timeout;
       private String      imap_folder;

       public commonIMAPClient() {
              clear();
              setNodeName("littlekey");
       }

       @Override
       public void clear() {
              super.clear();             
              debug           = true;           
              userName        = "av@vip.cbr.ru";
              password        = "123";          
              client_host     = "127.0.0.1";    
              client_port     = 143;            
              local_bind_client="*";   
              imap_folder      ="inbox";              
       }

       public boolean isDebug           () {return debug;}
       public String  getUser           () {return userName;}
       public String  getPasswrd        () {return password;}
       public String  getLocalBindClient() {return local_bind_client;}
       public String  getHost           () {return client_host;}
       public int     getPort           () {return client_port;}
       public int     getTimeout        () {return task_timeout;}
       public String  getFolder         () {return imap_folder;}


       @Override
       public void init() {
            init(this.getNode());
       }
       @Override
       public void         init(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("user"              .equals(n.getNodeName())){userName         =n.getTextContent(); logger.info("user:"+userName);}
                     else
                     if("password"          .equals(n.getNodeName())){password         =n.getTextContent(); logger.info("password:"+password);}
                     else
                     if("client_port"       .equals(n.getNodeName())){String s         =n.getTextContent(); try{client_port=Integer.parseInt(s, 10);}catch(Exception e){ client_port=143;logger.error("client_port:"+s);} logger.info("client_port:"+client_port);}
                     else                   
                     if("client_host"       .equals(n.getNodeName())){client_host      =n.getTextContent(); logger.info("client_host:"+client_host);}
                     else
                     if("local_bind_client" .equals(n.getNodeName())){local_bind_client=n.getTextContent(); logger.info("local_bind_client:"+local_bind_client);}
                     else
                     if("is_debug"          .equals(n.getNodeName())){String s         =n.getTextContent(); try{debug=Boolean.parseBoolean(s);}catch(Exception e){ debug=false;logger.error("is_debug:"+s);} logger.info("is_debug:"+debug);}
                     else
                     if("run_timeout"       .equals(n.getNodeName())){String s         =n.getTextContent();try{task_timeout=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set run_timeout:"+s);task_timeout=10;}logger.info("run_timeout:"+task_timeout);}
                     else
                     if("folder"           .equals(n.getNodeName())){imap_folder      =n.getTextContent(); logger.info("folder:"+imap_folder);}
                 }
                 if(stringTransform.isEmpty(local_bind_client))local_bind_client="*";
              }                               

      }



       public static void main(String args[]){
              commonIMAPClient mngr=new commonIMAPClient();
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