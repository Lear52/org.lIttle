package org.little.rcmd;

import java.io.BufferedInputStream;
import java.util.ArrayList;

import org.little.rcmd.rsh.rCMD;
import org.little.rcmd.rsh.rCP;
import org.little.rcmd.rsh.rCP_L2R;
import org.little.rcmd.rsh.rCP_R2L;
import org.little.rcmd.rsh.rCP_R2S;
import org.little.rcmd.rsh.rCP_R2H;
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
       private ArrayList<rCMD> list_rcommand;


       public rDionisCommand(rShell sh,String name) {
              list_rcommand=new ArrayList<rCMD>();
              this.sh=sh;
              this.name=name;
       }
 
       public boolean run(BufferedInputStream bufin)  {
              logger.debug("begin run cmd:"+name);

              for(int i=0;i<list_rcommand.size();i++){
                  rCMD cmd=list_rcommand.get(i);
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

               list_rcommand=new ArrayList<rCMD>();
               NodeList glist=node_cfg.getChildNodes();     
               int count=0;
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);
                   if("cmd".equals(n.getNodeName())){
                      rCommand cmd=makeCmd(n,count++);
                      if(cmd==null)continue;
                      list_rcommand.add(cmd);
                   }            
                   if("get".equals(n.getNodeName())){
                      rCP cmd=makeR2L(n,count++);
                      if(cmd==null)continue;
                      list_rcommand.add(cmd);
                   }            
                   if("put".equals(n.getNodeName())){
                      rCP cmd=makeL2R(n,count++);
                      if(cmd==null)continue;
                      list_rcommand.add(cmd);
                   }            
               }

               //logger.debug("end cfg cmd:"+name+" size:"+list.size());
               return true;
        }

        private rCommand makeCmd(Node node_cfg,int index) {
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
        

        private rCP makeR2L(Node node_cfg,int index) {
               rCP  cmd=null;
               if(node_cfg==null)return null;

               String remote=null;
               String local=null;
               String smtp=null;
               String http=null;
               NodeList glist=node_cfg.getChildNodes();     
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);

                   if("remote".equals(n.getNodeName())){
                      remote=n.getTextContent();
                   }            
                   else
                   if("local".equals(n.getNodeName())){
                       local=n.getTextContent();
                   }            
                   else
                   if("smtp".equals(n.getNodeName())){
                       smtp=n.getTextContent();
                   }            
                   else
                   if("http".equals(n.getNodeName())){
                       http=n.getTextContent();
                   }            

               }
               if(remote==null)return null;
               cmd=null;

               if(local!=null)cmd=new rCP_R2L(sh,name,index,remote,local); 
               if(smtp!=null)cmd=new rCP_R2S(sh,name,index,remote,smtp); 
               if(http!=null)cmd=new rCP_R2H(sh,name,index,remote,http); 
               return cmd;
        }
        private rCP makeL2R(Node node_cfg,int index) {
               rCP  cmd=null;
               if(node_cfg==null)return null;

               String remote=null;
               String local=null;
               NodeList glist=node_cfg.getChildNodes();     
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);

                   if("remote".equals(n.getNodeName())){
                      remote=n.getTextContent();
                   }            
                   else
                   if("local".equals(n.getNodeName())){
                       local=n.getTextContent();
                   }            

               }
               if(remote==null && local==null)return null;

               cmd=new rCP_L2R(sh,name,index,remote,local); 
               return cmd;
        }

}
