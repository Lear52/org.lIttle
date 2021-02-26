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

       private String                         apk_id;
       private String                         host;
       private String                         user;
       private String                         passwd;
       private HashMap<String,rCommand>       command; 

       public commonRCMD(String _apk_id){clear();apk_id=_apk_id;}
       public void clear(){
              command=new HashMap<String,rCommand>(); 
       }
       public String                         getID    () {return apk_id; }
       public String                         getHost  () {return host;   }
       public String                         getUser  () {return user;   }
       public String                         getPasswd() {return passwd; }
       public HashMap<String,rCommand>       getCMD   () {return command;}

       public void                           setHost  (String h) {host=h;   }
       public void                           setUser  (String u) {user=u;   }
       public void                           setPasswd(String p) {passwd=p; }
       
       public synchronized void init(Node node_apk){
              logger.info("The configuration apk begin");
              NodeList glist=node_apk.getChildNodes();
              for(int i=0;i<glist.getLength();i++){
                  Node  n=glist.item(i);
                  if("host".equals(n.getNodeName())    ){host    =n.getTextContent();  logger.info("host:"+host);     }
                  if("user".equals(n.getNodeName())    ){user    =n.getTextContent();  logger.info("user:"+user);     }
                  if("password".equals(n.getNodeName())){passwd  =n.getTextContent();  logger.info("password:******");}
              }
              logger.info("The configuration apk end");
       }
       public synchronized void initCMD(NodeList glist){
              logger.info("The configuration command begin");
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if(n==null)continue;
                  String command_id=n.getNodeName();
                  if(command_id==null)continue;
                  logger.trace("get command id:"+command_id);

                  if(n.getChildNodes()==null)continue;

                  logger.trace("command id:"+command_id+ " size:"+n.getChildNodes().getLength());

                  rCommand cmd=new rCommand(command_id);
                  if(cmd.loadCFG(n)!=false) {
               	     command.put(command_id, cmd);
                  }
              }
              if(command.size()==0)command=null;
        
              if(command==null)logger.info("The configuration rcmd command:null");
              else             logger.info("The configuration rcmd command:"+command.size());
    	   
    	   
       }

}

