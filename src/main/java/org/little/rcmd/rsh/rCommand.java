package org.little.rcmd.rsh;
       
import java.io.BufferedInputStream;
import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class rCommand  implements rCMD{
       private static Logger logger = LoggerFactory.getLogger(rCommand.class);

       private String          id;
       private ArrayList<rCMD> list_request;


       public rCommand(String _id) {
              list_request=new ArrayList<rCMD>();
              this.id=_id;
       }
       public rCommand(String _id,String _request,String response,boolean is_print_res) {
              list_request=new ArrayList<rCMD>();
              this.id=_id;
              list_request.add(new rRequest(_id,1,_request,new rResponse(_id,response,is_print_res)));
       }

       public String getID() {return id;}
 
       @Override
       public String type() {return getClass().getName();}

       @Override
       public boolean run(rShell sh)  {
              BufferedInputStream buf_input = new BufferedInputStream(sh.getIN());
              return run(sh,buf_input);
       }
       @Override
       public boolean run(rShell sh,BufferedInputStream buf_input)  {
              logger.debug("begin run cmd:"+id);

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
              logger.debug("end run cmd:"+id);
              return true;
       }
       @Override
       public String [] print() {
              ArrayList<String> buf=new ArrayList<String>();  
              //StringBuilder buf=new StringBuilder();  
              for(int i=0;i<list_request.size();i++){
                  rCMD cmd=list_request.get(i);
                  if(cmd==null)continue;
                  String[] s=cmd.print();
                  if(s==null)continue;
                  if(s.length<1)continue;
                  if("".equals(s[0]))continue;
                  buf.add(s[0]);
                  //buf.append(cmd.print());
              }
              //return buf.toString();
              String[] ret=new String[buf.size()];
              return buf.toArray(ret);
       }
          
       public boolean loadCFG(Node node_cfg) {
              list_request=makeListCMD(node_cfg,id);
              if(list_request.size()>0)return true;
              else return false;
       }
       private static ArrayList<rCMD> makeListCMD(Node node_cfg,String id) {
               ArrayList<rCMD> list_request=new ArrayList<rCMD>();
               NodeList glist=node_cfg.getChildNodes();     
               int count=0;
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);
 
                   if("cmd".equals(n.getNodeName())){
                      // make request
                      rRequest cmd=makeCmd(n,count++,id);
                      if(cmd==null)continue;
                      list_request.add(cmd);
                   }            
                   if("get".equals(n.getNodeName())){
                      // make rcp
                      rCP cmd=makeR2L(n,count++,id);
                      if(cmd==null)continue;
                      list_request.add(cmd);
                   }            
                   if("put".equals(n.getNodeName())){
                      // make rcp
                      rCP cmd=makeL2R(n,count++,id);
                      if(cmd==null)continue;
                      list_request.add(cmd);
                   }            
               }
              
               return list_request;
       }

       private static rRequest makeCmd(Node node_cfg,int index,String id) {
               rRequest  cmd=null;
               if(node_cfg==null)return null;

               String    request=null;
               rResponse response=null;
               NodeList glist=node_cfg.getChildNodes();     
               if(glist!=null)
               for(int i=0;i<glist.getLength();i++){
                   Node n=glist.item(i);

                   if("req".equalsIgnoreCase(n.getNodeName())){
                      request=n.getTextContent();
                   }            
                   else
                   if("res".equalsIgnoreCase(n.getNodeName())){
                      String _id="01";
                      String res=null;
                      boolean is_print_res=false;
                      logger.info("res");

                      if(n.getAttributes().getNamedItem("res")!=null){
                         res=n.getAttributes().getNamedItem("res").getTextContent();
                         logger.info("res:"+res);
                      }
                      else{
                         res=n.getTextContent();
                      }
                      if(n.getAttributes().getNamedItem("id")!=null){
                         _id=n.getAttributes().getNamedItem("id").getTextContent();
                         logger.info("id:"+id);
                      }
                      if(n.getAttributes().getNamedItem("print")!=null){
                         String _is=n.getAttributes().getNamedItem("print").getTextContent();
                         try{is_print_res=Boolean.parseBoolean(_is);}catch(Exception e){ is_print_res=false;logger.error("is_print_res:"+_is);} 
                         logger.info("is_print_res:"+is_print_res);
                      }
                      response=new rResponse(_id,res,is_print_res);

                      NodeList res_list=n.getChildNodes();     
                      if(glist!=null)
                      for(int k=0;k<res_list.getLength();k++){
                          Node nn=res_list.item(i);
                          ArrayList<rCMD> _list_request=makeListCMD(node_cfg,id);
                          response.setCMD(_list_request);
                      }            
                  }
               }

               if(request==null && response==null)return null;

               cmd=new rRequest(id,index,request,response); 

               return cmd;
       }
        

       private static rCP makeR2L(Node node_cfg,int index,String id) {
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

               if(local!=null)cmd=new rCP_R2L(id,index,remote,local); 
               if(smtp !=null)cmd=new rCP_R2S(id,index,remote,smtp); 
               if(http !=null)cmd=new rCP_R2H(id,index,remote,http); 
               return cmd;
       }
       private static rCP makeL2R(Node node_cfg,int index,String id) {
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
               cmd=new rCP_L2R(id,index,remote,local); 
               return cmd;
       }

}
