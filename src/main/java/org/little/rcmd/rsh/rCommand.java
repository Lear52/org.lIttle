package org.little.rcmd.rsh;
       
import java.io.BufferedInputStream;
import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class rCommand  implements rCMD{
       private static Logger logger = LoggerFactory.getLogger(rCommand.class);

       private String          command_id;
       private ArrayList<rCMD> list_request;


       public rCommand(String _command_id) {
              list_request=new ArrayList<rCMD>();
              this.command_id=_command_id;
       }
       public rCommand(String _command_id,String _request,String response,boolean is_print_res) {
              list_request=new ArrayList<rCMD>();
              this.command_id=_command_id;
              list_request.add(new rRequest(_command_id,1,_request,new rResponse(_command_id,response,is_print_res)));
       }

       public String getID() {return command_id;}
 
       @Override
       public String type() {return getClass().getName();}

       //@Override
       //public boolean run(rShell sh)  {
       //       BufferedInputStream buf_input = new BufferedInputStream(sh.getIN());
       //       return run(sh,buf_input);
       //}
       @Override
       public boolean run(rShell sh,BufferedInputStream buf_input)  {
              logger.debug("begin run cmd:"+command_id);

              for(int i=0;i<list_request.size();i++){
                  rCMD cmd=list_request.get(i);
                  if(cmd==null)continue;
                  boolean ret;

                  ret=cmd.run(sh,buf_input);

                  if(ret==false) {
                     logger.trace("cmd:"+cmd.toString()+" ret:"+ret);
                     logger.error("cmd:"+cmd.toString()+" ret:"+ret);
                     return ret;
                  }
              } 
              logger.debug("end run cmd:"+command_id);
              return true;
       }
       @Override
       public String [] print() {
              ArrayList<String> buf=new ArrayList<String>();  
              for(int i=0;i<list_request.size();i++){
                  rCMD cmd=list_request.get(i);
                  if(cmd==null)continue;
                  String[] s=cmd.print();
                  if(s==null)continue;
                  for(int j=0;j<s.length;j++) {
                      if("".equals(s[j]))continue;
                      buf.add(s[j]);
                  }
              }
              String[] ret=new String[buf.size()];
              return buf.toArray(ret);
       }
          
       public boolean loadCFG(Node node_cfg) {
              list_request=rCommand.makeListCMD(node_cfg,command_id);
              if(list_request.size()>0)return true;
              else return false;
       }
       private static ArrayList<rCMD> makeListCMD(Node node_cfg,String _id_command_id) {
               ArrayList<rCMD> list_request=new ArrayList<rCMD>();
               NodeList glist=node_cfg.getChildNodes();     
               //logger.trace("makeListCMD:"+node_cfg.getNodeName());
               int count=0;
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);
 
                   if("cmd".equals(n.getNodeName())){
                      // make request
                      rRequest cmd=makeCmd(n,count++,_id_command_id);
                      if(cmd==null)continue;
                      list_request.add(cmd);
                   }            
                   if("get".equals(n.getNodeName())){
                      // make rcp
                      rCP cmd=makeRemote2Local(n,count++,_id_command_id);
                      if(cmd==null)continue;
                      list_request.add(cmd);
                   }            
                   if("put".equals(n.getNodeName())){
                      // make rcp
                      rCP cmd=makeLocal2Remote(n,count++,_id_command_id);
                      if(cmd==null)continue;
                      list_request.add(cmd);
                   }            
               }
              
               return list_request;
       }

       private static rRequest makeCmd(Node node_cfg,int index,String _command_id) {
               rRequest  cmd=null;
               if(node_cfg==null)return null;

               String    txt_request=null;
               rResponse response=null;
               String    _id_response="01";//id_res
               String    txt_response=null;
               boolean   is_print_res=false;

               NodeList glist=node_cfg.getChildNodes();     
               if(glist!=null)
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);

                   if("req".equalsIgnoreCase(n.getNodeName())){
                      txt_request=n.getTextContent();
                   }            
                   else
                   if("res".equalsIgnoreCase(n.getNodeName())){
                      //logger.info("res");

                      if(n.getAttributes().getNamedItem("res")!=null){
                         txt_response=n.getAttributes().getNamedItem("res").getTextContent();
                         //logger.info("res:"+res);
                      }
                      else{
                         txt_response=n.getTextContent();
                      }
                      if(n.getAttributes().getNamedItem("id")!=null){
                         _id_response=n.getAttributes().getNamedItem("id").getTextContent();
                         //logger.info("id:"+id);
                      }
                      if(n.getAttributes().getNamedItem("print")!=null){
                         String _is=n.getAttributes().getNamedItem("print").getTextContent();
                         try{is_print_res=Boolean.parseBoolean(_is);}catch(Exception e){ is_print_res=false;logger.error("is_print_res:"+_is);} 
                         //logger.info("is_print_res:"+is_print_res);
                      }
                      response=new rResponse(_id_response,txt_response,is_print_res);

                      NodeList res_list=n.getChildNodes();     
                      if(glist!=null){
                         logger.trace("make sub command for command:"+_command_id+" request:"+index+" response:"+_id_response);
                         for(int k=0;k<res_list.getLength();k++){
                             Node nn=res_list.item(i);
                             if(nn==null)continue;
                             String _cmd_id=nn.getNodeName();
                             if(_cmd_id==null)continue;
                             if(nn.getChildNodes()==null)continue;
                             ArrayList<rCMD> _list_request=makeListCMD(nn,_cmd_id);
                             response.setCMD(_list_request);
                         }            
                      }
                  }

               }

               if(txt_request==null && response==null) {
                   logger.error("command:"+_command_id+" txt_request==null and response==null");
            	   return null;
               }
               String prn_request=txt_request;
               if(prn_request==null)prn_request=" is null"; 
               logger.trace("command:"+_command_id+" make rRequest:"+prn_request+" index:"+index+" response:"+_id_response+" print:"+is_print_res);

               cmd=new rRequest(_command_id,index,txt_request,response); 

               return cmd;
       }
        

       private static rCP makeRemote2Local(Node node_cfg,int index,String _command_id) {
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

               if(local!=null)cmd=new rCP_Remote2Local(_command_id,index,remote,local); 
               if(smtp !=null)cmd=new rCP_Remote2SMTP(_command_id,index,remote,smtp); 
               if(http !=null)cmd=new rCP_Remote2Buffer(_command_id,index,remote,http); 
               return cmd;
       }
       private static rCP makeLocal2Remote(Node node_cfg,int index,String _command_id) {
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

               //cmd=new rCP_L2R(sh,name,index,remote,local); 
               cmd=new rCP_Local2Remote(_command_id,index,remote,local); 
               return cmd;
       }
	   @Override
	   public byte[] getBuffer() {
		      // TODO Auto-generated method stub
		      return null;
	   }
	   @Override
	   public void setBuffer(byte[] buffer) {
		      // TODO Auto-generated method stub
		
       }

}
