package org.little.rcmd;

import java.util.HashMap;

import org.little.rcmd.rsh.rCommand;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

/**
 * 
 */
public class commonRCMD{

       private static final Logger logger = LoggerFactory.getLogger(commonRCMD.class);

       private String                         host;
       private String                         user;
       private String                         passwd;
       private HashMap<String,rCommand> command; 

       //private static commonRCMD              cfg = new commonRCMD();
       //public  static commonRCMD              get(){ if(cfg==null)cfg=new commonRCMD();return cfg;};


       public commonRCMD(){clear();}
       public commonRCMD(commonRCMD _cmd){

       }

       public void clear(){
              command=new HashMap<String,rCommand>(); 
       }
       public String                         getHost()   {return host;   }
       public String                         getUser()   {return user;   }
       public String                         getPasswd() {return passwd; }
       public HashMap<String,rCommand>       getCMD   () {return command;}
       
       public void init(Node node_cfg){
           if(node_cfg==null)return;
           logger.info("The configuration node:"+node_cfg.getNodeName());
           NodeList glist=node_cfg.getChildNodes();     
           init(glist);
       }
       public void init(NodeList glist){
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("host".equals(n.getNodeName())    ){host    =n.getTextContent();  logger.info("host:"+host);     }
                  if("user".equals(n.getNodeName())    ){user    =n.getTextContent();  logger.info("user:"+user);     }
                  if("password".equals(n.getNodeName())){passwd  =n.getTextContent();  logger.info("password:******");}
                  //else
              }
              logger.info("The configuration rcmd global");
       }

       public void loadCMD(Node node_cfg){
              if(node_cfg!=null){
                 NodeList glist=node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node     n        =glist.item(i);
                     String   name_node=n.getNodeName();
                     rCommand cmd      =new rCommand(name_node);
                     if(cmd.loadCFG(n)!=false) command.put(name_node, cmd);
                 }
                 if(command.size()==0)command=null;
              } 
              if(command==null)logger.info("The configuration rcmd command:null");
              else             logger.info("The configuration rcmd command:"+command.size());
       }



}

