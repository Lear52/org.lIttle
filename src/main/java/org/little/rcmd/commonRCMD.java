package org.little.rcmd;

import java.util.HashMap;
//import java.util.Iterator;

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
       /*
       public commonRCMD(commonRCMD _cmd){
              apk_id=_cmd.apk_id;  
              host  =_cmd.host  ;  
              user  =_cmd.user  ;  
              passwd=_cmd.passwd;
              Iterator<rCommand> list = _cmd.command.values().iterator();
              while(list.hasNext()) {
                  rCommand r = list.next();
                  if(r==null)continue;
                  rCommand new_r=new rCommand(r);
                  command.put(new_r.getID(),new_r);
              }
              
       }
       */
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
       
       public void init(Node node_apk){
              NodeList glist=node_apk.getChildNodes();
              for(int i=0;i<glist.getLength();i++){
                  Node  n=glist.item(i);
                  if("host".equals(n.getNodeName())    ){host    =n.getTextContent();  logger.info("host:"+host);     }
                  if("user".equals(n.getNodeName())    ){user    =n.getTextContent();  logger.info("user:"+user);     }
                  if("password".equals(n.getNodeName())){passwd  =n.getTextContent();  logger.info("password:******");}
              }
              logger.info("The configuration apk set");
       }
       public void initCMD(NodeList glist){
           for(int i=0;i<glist.getLength();i++){
               Node     n        =glist.item(i);
               String   name_node=n.getNodeName();
               rCommand cmd      =new rCommand(name_node);
               if(cmd.loadCFG(n)!=false) command.put(name_node, cmd);
           }
           if(command.size()==0)command=null;
        
        if(command==null)logger.info("The configuration rcmd command:null");
        else             logger.info("The configuration rcmd command:"+command.size());
    	   
    	   
       }

}

