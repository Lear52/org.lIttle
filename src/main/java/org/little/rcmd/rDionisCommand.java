package org.little.rcmd;

import java.io.BufferedInputStream;
import java.util.ArrayList;

import org.little.rcmd.rsh.rCommand;
import org.little.rcmd.rsh.rShell;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class rDionisCommand{
       private static Logger logger = LoggerFactory.getLogger(rDionisCommand.class);

       private String              name;
       private rShell              sh;
       private ArrayList<rCommand> list;

       public rDionisCommand(rShell sh,String name) {
              list=new ArrayList<rCommand>();
              this.sh=sh;
              this.name=name;
       }
 
       public boolean run(BufferedInputStream bufin)  {
              logger.debug("begin run cmd:"+name);

              for(int i=0;i<list.size();i++){
                  rCommand cmd=list.get(i);
                  if(cmd==null)continue;
                  boolean ret;
                  ret=cmd.run(bufin);
                  if(ret==false) {
                     logger.trace("cmd:"+cmd.toString()+" ret:"+ret);
                     logger.error("cmd:"+cmd.toString()+" ret:"+ret);
                     return ret;
                  }
              } 
              logger.debug("end run cmd:"+name);
           return true;
       }
          

        public boolean loadCFG(Node node_cfg) {
               //logger.debug("begin cfg cmd:"+name);

               list=new ArrayList<rCommand>();
               NodeList glist=node_cfg.getChildNodes();     
               int count=0;
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);
                   if("cmd".equals(n.getNodeName())){
                      rCommand cmd=makeCmd(n,count++);
                      if(cmd==null)continue;
                      list.add(cmd);
                   }            
               }

               //logger.debug("end cfg cmd:"+name+" size:"+list.size());
               return true;
        }

        public rCommand makeCmd(Node node_cfg,int index) {
               rCommand  cmd=null;
               if(node_cfg==null)return null;

               String request=null;
               String response=null;
               NodeList glist=node_cfg.getChildNodes();     
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);

                   if("req".equals(n.getNodeName())){
                           request=n.getTextContent();
                   }            
                   else
                   if("res".equals(n.getNodeName())){
                           response=n.getTextContent();
                   }            

               }
               if(request==null && response==null)return null;

               cmd=new rCommand(sh,name,index,request,response); 

               //logger.trace("make cmd:"+cmd.toString());
               return cmd;
        }
        
}
