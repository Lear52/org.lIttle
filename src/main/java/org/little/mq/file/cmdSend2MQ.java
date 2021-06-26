package org.little.mq.file;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.tfork;


public class cmdSend2MQ extends tfork{
       private static final  Logger log = LoggerFactory.getLogger(cmdSend2MQ.class);
       public  static String getClassName(){return cmdSend2MQ.class.getName();}

       private Node                  root_cfg_node;
       private String                global_folder_name;
       private String                connect_qm_name;
       private ArrayList<cmdElement> list;
       private String                name;


       public cmdSend2MQ(String _connect_qm_name,Node _t_node,String  _global_folder_name) {
              clear();
              root_cfg_node=_t_node;
              global_folder_name=_global_folder_name;
              connect_qm_name=_connect_qm_name;
              name="";
       }

       public long getCount(){
              long cnt=0;
              for(int j=0;j<list.size();j++) {
                  cmdElement s=list.get(j);
                  cnt+=s.getCount();
              }
              return cnt;
       }

       @Override
       public void clear() {
              super.clear();
       }
       public void open(){

              NamedNodeMap at    = root_cfg_node.getAttributes();
              Node         at_n  = at.getNamedItem("name");
              if(at_n==null)return;
              name= at_n.getNodeValue();

              System.out.println("Start thread:"+name);
              log.trace("Start thread:"+name);

              NodeList     run_docs=root_cfg_node.getChildNodes();

              list=new ArrayList<cmdElement>(5);

              for(int j=0;j<run_docs.getLength();j++) {

                  if("portion".equals(run_docs.item(j).getNodeName())==false )continue;

                  NamedNodeMap p_at   =run_docs.item(j).getAttributes();
                  String pname        =null;
                  String command_send =null;
                  String timeout      =null;
                  //String tps          =null;
                  //String amount     =null;
                  String local_folder_name      =null;
                  String jms_destination=null;
                  //String needzip    =null;
                  if(p_at.getNamedItem("name"       )!=null)pname         =p_at.getNamedItem("name"       ).getNodeValue();
                  if(p_at.getNamedItem("directive"  )!=null)command_send  =p_at.getNamedItem("directive"  ).getNodeValue();

                  if("TIMEOUT".equals(command_send)){
                     if(p_at.getNamedItem("timeout"    )!=null)timeout    =p_at.getNamedItem("timeout"    ).getNodeValue();
                     //if(p_at.getNamedItem("amount"     )!=null)amount     =p_at.getNamedItem("amount"     ).getNodeValue();
                     int _timeout=0;
                     if(timeout!=null) try { _timeout=Integer.parseInt(timeout, 10);} catch (Exception e) {_timeout=0;}
                     cmdElement s=new cmdDelay(_timeout);
                     list.add(s);
                     s.open();
                     log.trace("open:"+command_send+" thread:"+name+" portion:"+pname+" "+" "+_timeout);
                  }
                  else
                  if("JMSGET".equals(command_send)){

                  }
                  else
                  if("JMSSEND".equals(command_send)){
                     if(p_at.getNamedItem("time"       )!=null)timeout           =p_at.getNamedItem("time"       ).getNodeValue();
                     //if(p_at.getNamedItem("tps"        )!=null)tps               =p_at.getNamedItem("tps"        ).getNodeValue();
                     //if(p_at.getNamedItem("amount"     )!=null)amount     =p_at.getNamedItem("amount"     ).getNodeValue();
                     if(p_at.getNamedItem("issue"      )!=null)local_folder_name =p_at.getNamedItem("issue"      ).getNodeValue();
                     if(p_at.getNamedItem("destination")!=null)jms_destination   =p_at.getNamedItem("destination").getNodeValue();
                     //if(p_at.getNamedItem("needzip"    )!=null)needzip           =p_at.getNamedItem("needzip"    ).getNodeValue();
                     long _timeout=0;
                     if(timeout!=null) try { _timeout=Long.parseLong(timeout, 10);} catch (Exception e) {_timeout=0;}

                     cmdElement s=new sendDir2MQ(connect_qm_name,jms_destination,global_folder_name,local_folder_name,_timeout,name);

                     list.add(s);
                     s.open();
                     log.trace("open:"+command_send+" thread:"+name+" portion:"+pname+" "+timeout+" "+global_folder_name+local_folder_name);

                  }
                  else continue;
              }
       }
       @Override
       public void run(){
              int j;

              if(list==null)return;

              for(j=0;j<list.size();j++) {
                  cmdElement s=list.get(j);
                  //log.trace("start:"+command_send+" thread:"+name+" portion:"+pname+" "+timeout+" "+global_folder_name+local_folder_name);
                  s.work();
                  //log.trace("end:"+command_send+" thread:"+name+" portion:"+pname+" "+" "+_timeout);
              }
              for(j=0;j<list.size();j++) {
                  cmdElement s=list.get(j);
                  s.close();
              }

              System.out.println("Shutdown thread:"+name);
              log.trace("Shutdown thread:"+name);

              shutdown();

       }

       public static void help(String[] args) {
              System.out.println("run java "+getClassName()+" connect_q_manager filename_config.xml");
       }

       public static void main(String[] args) {
              ArrayList<cmdSend2MQ> list_run=new ArrayList<cmdSend2MQ>(100);

              if(args.length!=2){
                 help(args);
                 return;
              }
              String q_manager=args[0];
              String file_name=args[1];
              DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
              Document doc;
              log.trace("start "+getClassName()+" qmanager:"+q_manager+"config_file:"+file_name);

              try {
                   DocumentBuilder builder;
                   builder  = factory.newDocumentBuilder();
                   doc      = builder.parse(file_name);
              } 
              catch(Exception e) {
                    log.error("error open config file:"+file_name + " ex:"+e);
                    return;
              }
                      
              log.trace("open config file "+file_name);
              try {
                   Node common=doc.getFirstChild();
                   //System.out.println("common>>"+common.getNodeName());
                   if("parallelstructure".equals(common.getNodeName())==false){
                      log.error("error parallelstructure name");
                      log.error("error read config file:"+file_name);
                      return;
                   }
                   
                   NamedNodeMap at    = common.getAttributes();
                   Node         at_n  = at.getNamedItem("name");
                   if(at_n==null){
                      log.error("error attr name");
                      log.error("error read config file:"+file_name);
                      return;
                   }
                   String       name  = at_n.getNodeValue();
                   Node         at_d  = at.getNamedItem("datafolder");
                   if(at_d==null){
                      log.error("error attr datafolder");
                      log.error("error read config file:"+file_name);
                      return;
                   }
                   String       global_folder_name=at_d.getNodeValue();
                   log.info("open structure:"+name);
                   log.info("open data_folder:"+global_folder_name);
                   System.out.println("common>>"+name);


                   NodeList list=common.getChildNodes();     

                   int c=0;
                   int i;
                   for(i=0;i<list.getLength();i++){
                       Node c_thread=list.item(i);
                       if("thread".equals(c_thread.getNodeName())) {
                          cmdSend2MQ   r=new cmdSend2MQ(q_manager,c_thread,global_folder_name);
                          list_run.add(r);
                          r.open();
                          c++;
                       }        
                   }
                   //--------------------------------------------------------------------------- 
                   for(i=0;i<list_run.size();i++){
                       list_run.get(i).fork();
                       System.out.println("run thread:"+i);
                       log.info("run thread:"+i);
                   }
                   //--------------------------------------------------------------------------- 

                   System.out.println("structure size:"+c);
                   log.info("structure size:"+c);
              }
              catch(Exception e) {
                  log.error("error read config file:"+file_name + " ex:"+e);
                  return;
              }

              //--------------------------------------------------------------------------------------------------------------------------
              int count=1;
              
              while(true){
                    boolean is_run=false;
                    int k=0;
                    long cnt=0;
                    for(k=0;k<list_run.size();k++){
                        cnt+=list_run.get(k).getCount();
                    }
                    for(k=0;k<list_run.size();k++){
                        if(list_run.get(k).isRun()==true){is_run=true;break;}
                    }
                    //long t=System.currentTimeMillis();
                    double pts=0;
                    //t-=st;
                    pts=(double)cnt/(double)count;

                    System.out.println("t:"+count+" cnt:"+cnt+" pts:"+pts);

                    if(is_run==true)tfork.delay(1);
                    else break;

                    count++;
              }
              System.out.println("STOP all");
              log.trace("stop "+getClassName()+" qmanager:"+q_manager+" config_file:"+file_name);

       }

}
/*
<?xml version="1.0" encoding="windows-1251"?>
<parallelstructure name="Тестовая подача" datafolder="C:\generatorv16v2\send_ckps_od20180207(moscow_nn)\" fillid="true">
  <thread name="Поток ASBU">
    <portion name="11" directive="JMSSEND" time="1200000" amount="0" issue="ASBU\11\"  destination="JmsChannel:SYSTEM.ADMIN.SVRCONN/Port:1414/Host:10.70.112.86/Queue:UTP@QM_cc" needzip="false"/>
    <portion name="12" directive="JMSSEND" time="1200000" amount="0" issue="ASBU\12\"  destination="JmsChannel:SYSTEM.ADMIN.SVRCONN/Port:1414/Host:10.70.112.86/Queue:UTP@QM_cc" needzip="false"/>
  </thread>
  <thread name="Поток AUR">
    <portion name="10" directive="JMSSEND" time="1200000" amount="0" issue="AUR\10\"  destination="JmsChannel:SYSTEM.ADMIN.SVRCONN/Port:1414/Host:10.70.112.86/Queue:UTP@QM_cc" needzip="false"/>
  </thread>
</parallelstructure>


*/