package org.little.mq.stream.mq;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.little.util.Logger;
import org.little.util.run.tfork;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class mqCommand{
       final private static String CLASS_NAME="prj0.stream.mq.mqCommand";
       final private static int    CLASS_ID  =1702;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private ArrayList<mqCmdThread> list_run;
       
       public mqCommand(){clear();}

       public void clear(){
              list_run=new ArrayList<mqCmdThread>(100);
       }
       public boolean open(String file_name){
              
              DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
              Document doc;
              log.trace("start "+getClassName()+"config_file:"+file_name);

              clear();

              try {
                   DocumentBuilder builder;
                   builder  = factory.newDocumentBuilder();
                   doc      = builder.parse(file_name);
              } 
              catch(Exception e) {
                    log.error("error open config file:"+file_name + " ex:"+e);
                    return false;
              }
                      
              log.trace("open config file "+file_name);
              try {
                   Node common=doc.getFirstChild();
                   //System.out.println("common>>"+common.getNodeName());
                   if("parallelstructure".equals(common.getNodeName())==false){
                      log.error("error parallelstructure name");
                      log.error("error read config file:"+file_name);
                      return false;
                   }
                   String name;
                   NamedNodeMap at    = common.getAttributes();
                   Node         at_n  = at.getNamedItem("name");
                   if(at_n==null){
                      log.error("error attr name");
                      log.error("error read config file:"+file_name);
                      return false;
                   }
                   name         = at_n.getNodeValue();
                   log.info("open structure:"+name);

                   NodeList list=common.getChildNodes();     
                   if(list==null){
                      log.error("error read structure config file:"+file_name + " structure size:0");
                      return false;
                   }
                   int c=0;
                   int i;
                   for(i=0;i<list.getLength();i++){
                       Node c_thread=list.item(i);
                       if(c_thread==null)continue;
                       if("thread".equals(c_thread.getNodeName())) {
                          mqCmdThread   r=new mqCmdThread();
                          log.info("creat thread:"+c);

                          r.open(c_thread);
                          log.info("open thread:"+c);
                          list_run.add(r);
                          c++;
                          log.info("add thread:"+c);
                       }        
                   }
                   //--------------------------------------------------------------------------- 
                   //System.out.println("structure size:"+c+"/"+list.getLength());
                   log.info("structure size:"+c+"/"+list.getLength());
              }
              catch(Exception e1) {
                  log.error("error read structure config file:"+file_name + " ex:"+e1);
                  return false;
              }
              //--------------------------------------------------------------------------------------------------------------------------
              return true;
       }
       public void run() {  

              System.out.println("begin run thread");
              //--------------------------------------------------------------------------- 
              for(int i=0;i<list_run.size();i++){
                  list_run.get(i).fork();
                  System.out.println("run thread:"+i+"/"+list_run.size());
                  log.info("run thread:"+i+"/"+list_run.size());
              }
              //--------------------------------------------------------------------------- 
              System.out.println("run all thread:"+list_run.size());
              
              while(true){
                    boolean is_run=false;
                    int k=0;
                    //long cnt=0;
                    for(k=0;k<list_run.size();k++){
                        if(list_run.get(k).isRun()==true){is_run=true;break;}
                    }
                    if(is_run==true)tfork.delay(1);
                    else break;

              }

              System.out.println("STOP all");
              log.trace("stop "+getClassName()+" config_file:");

              close();
       }
       public void close(){

       }
              
       public static void main(String[] args) {
              mqCommand cntrl=new mqCommand();
              boolean ret;
              ret=cntrl.open(args[0]);
              if(ret)cntrl.run();
              cntrl.close();
       }


}


