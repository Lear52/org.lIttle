package org.little.mq.stream.mq;

import org.little.mq.mqapi.mq_contrl_jms;
import org.little.mq.mqapi.mqExcept;
import org.little.util.Logger;
import org.little.util.run.tfork;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class mqCmdThread  extends tfork{
       final private static String CLASS_NAME="prj0.stream.mq.mqCmdThread";
       final private static int    CLASS_ID  =1702;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private mqArrayContrl list_cntrl;
       private NodeList      run_cmd;
       private String        name;

       public mqCmdThread(){
              clear();
       }
       public void clear(){
              list_cntrl=new mqArrayContrl();
              run_cmd=null;
              name=null;
       }
       public void open(Node t_node){
              NamedNodeMap at    = t_node.getAttributes();
              Node         at_n  = at.getNamedItem("name");
              //if(at==null){
              //   log.error(" thread node:null");
              //   return;
              //}
              if(at_n==null){
                 log.error(" thread name:null");
                 return;
              }
              name = at_n.getNodeValue();
              log.trace("Open thread:"+name);

              run_cmd=t_node.getChildNodes();

       }
       private mq_contrl_jms openMQContrl(NodeList qm_list,int index){
               Node         qm_node=null;
               NamedNodeMap qm_at  =null;
               String       jms_connection=null;
               int i=0;
               for(int j=0;j<qm_list.getLength();j++){
                   qm_node =qm_list.item(j);
                   if(qm_node==null){
                      continue;
                   }
                   qm_at=qm_node.getAttributes();
                   if(qm_at==null){
                      continue;
                   }
                   if(i==index)break;
                   i++;
               }
               if(i!=index|| qm_at==null){
                      log.error("error get node index:"+index);
                      return null;
               }
               //log.trace("get node attr index:"+index);

               if(qm_at.getNamedItem("connect")!=null)jms_connection=qm_at.getNamedItem("connect").getNodeValue();

               if(jms_connection==null){
                  log.error("error open connection string connection==null");
                  return null;
               }
               mq_contrl_jms c1=null;
               try{
                  c1=list_cntrl.open(jms_connection);
               }catch (mqExcept e){
                  log.error("error open  connection:"+jms_connection);
                  return null;
               }
               
               return c1;
       }
       private void closeMQContrl(mq_contrl_jms m){if(m!=null)list_cntrl.close(m);}

       public void run() {  
              if(run_cmd==null){
                 log.error(" thread:"+name+" size:0");
                 return;
              }
              log.trace("start thread:"+name+" size:"+run_cmd.getLength());
              for(int j=0;j<run_cmd.getLength();j++) {

                  //if("portion".equals(run_docs.item(j).getNodeName())==false )continue;

                  Node         p_node =run_cmd.item(j);
                  if(p_node==null)continue;
                  NamedNodeMap p_at   =p_node.getAttributes();
                  if(p_at==null)continue;
               	  String command_name ="";
                  String object_name  ="";
                  String ip_name      ="";
                  String local_name   ="";
                  String type         ="";
                  String force        ="";
                  String is_stop      ="";
               	  if(p_at.getNamedItem("name"   )!=null)command_name  =p_at.getNamedItem("name"   ).getNodeValue();
               	  if(p_at.getNamedItem("object" )!=null)object_name   =p_at.getNamedItem("object" ).getNodeValue();
               	  if(p_at.getNamedItem("addr"   )!=null)ip_name       =p_at.getNamedItem("addr"   ).getNodeValue();
               	  if(p_at.getNamedItem("local"  )!=null)local_name    =p_at.getNamedItem("local"  ).getNodeValue();
               	  if(p_at.getNamedItem("type"   )!=null)type          =p_at.getNamedItem("type"   ).getNodeValue();
               	  if(p_at.getNamedItem("force"  )!=null)type          =p_at.getNamedItem("force"  ).getNodeValue();
               	  if(p_at.getNamedItem("is_stop")!=null)type          =p_at.getNamedItem("is_stop").getNodeValue();

                  log.trace(" thread:"+name+" command_name:"+command_name+" cnt:"+j+"/"+run_cmd.getLength());

               	  if("TIMEOUT".equals(command_name)){
                     String timeout=object_name;
               	     int _timeout  =0;
                     if(timeout!=null) try { _timeout=Integer.parseInt(timeout, 10);} catch (Exception e) {_timeout=0;}
                     log.trace("open:"+command_name+" thread:"+name+" command_name:"+command_name+" "+" "+_timeout);
                  }
               	  else
               	  if("SETCHANNEL".equals(command_name)){
                     NodeList     qm_list=p_node.getChildNodes();
                     mq_contrl_jms     c0=openMQContrl(qm_list,0);
                     if(c0!=null){
                     try{
                         c0.alterChannel(object_name,type,ip_name,local_name);
                     }
                     catch(mqExcept e){
                           log.error("error cmd:"+command_name+" ex:"+e);
                           continue;
                     }
                     catch(Exception e1){
                           log.error("error cmd:"+command_name+" ex:"+e1);
                           continue;
                     }
                     log.trace("exec:"+command_name+" thread:"+name);
                     }
                     closeMQContrl(c0);
                     log.trace("close:"+command_name+" thread:"+name);
               	  }
               	  else
               	  if("RESETCHANNEL".equals(command_name)){
                     NodeList     qm_list=p_node.getChildNodes();
                     mq_contrl_jms c0=openMQContrl(qm_list,0);
                     mq_contrl_jms c1=openMQContrl(qm_list,1);
                     if(c0!=null&&c1!=null){
                     try{
                         c0.resetChannel(object_name,1);
                         c1.resetChannel(object_name,1);
                     }
                     catch(mqExcept e){
                           log.error("error cmd:"+command_name+" ex:"+e);
                           continue;
                     }
                     catch(Exception e1){
                           log.error("error cmd:"+command_name+" ex:"+e1);
                           continue;
                     }
                     log.trace("exec:"+command_name+" thread:"+name);
                     }
                     closeMQContrl(c0);
                     closeMQContrl(c1);
                     log.trace("close:"+command_name+" thread:"+name);
               	  }
               	  else
               	  if("STARTCHANNEL".equals(command_name)){
                     NodeList     qm_list=p_node.getChildNodes();
                     mq_contrl_jms c0=openMQContrl(qm_list,0);
                     mq_contrl_jms c1=openMQContrl(qm_list,1);
                     if(c0!=null&&c1!=null){
                     try{
                         c0.startChannel(object_name);
                         c1.startChannel(object_name);
                     }
                     catch(mqExcept e){
                           log.error("error cmd:"+command_name+" ex:"+e);
                           continue;
                     }
                     catch(Exception e1){
                           log.error("error cmd:"+command_name+" ex:"+e1);
                           continue;
                     }
                     log.trace("exec:"+command_name+" thread:"+name);
                     }
                     closeMQContrl(c0);
                     closeMQContrl(c1);
                     log.trace("close:"+command_name+" thread:"+name);

               	  }
               	  else
               	  if("STOPCHANNEL".equals(command_name)){
                     NodeList     qm_list=p_node.getChildNodes();
                     log.trace("begin:"+command_name+" thread:"+name);
                     mq_contrl_jms c0=openMQContrl(qm_list,0);
                     mq_contrl_jms c1=openMQContrl(qm_list,1);

                     if(c0!=null&&c1!=null){
                        log.trace("start:"+command_name+" thread:"+name);
                        try{
                            boolean _is_force=true;
                            boolean _is_stop =false;

                            if(force  .equals("false"))_is_force=false;
                            if(is_stop.equals("true" ))_is_stop =true;

                            c0.stopChannel(object_name,_is_force,_is_stop);
                            c1.stopChannel(object_name,_is_force,_is_stop);
                        }
                        catch(mqExcept e){
                              log.error("error cmd:"+command_name+" ex:"+e);
                              continue;
                        }
                        catch(Exception e1){
                              log.error("error cmd:"+command_name+" ex:"+e1);
                              continue;
                        }
                        log.trace("exec:"+command_name+" thread:"+name);
                     }
                     closeMQContrl(c0);
                     closeMQContrl(c1);
                     log.trace("close:"+command_name+" thread:"+name);
                  }
               	  else continue;
              }
              //-----------------------------------------------------------------------
              // end for
              //-----------------------------------------------------------------------
              log.trace("stop thread:"+name+" size:"+run_cmd.getLength());
              list_cntrl.closeAll();
              stop();
       }
              
       //public static void main(String[] args) {
       //       mqCmdThread cntrl=new mqCmdThread();
       //}


}


