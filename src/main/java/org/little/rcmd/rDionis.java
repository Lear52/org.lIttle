package org.little.rcmd;

import java.io.BufferedInputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.little.rcmd.rsh.rShell;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class rDionis{

       private static Logger                  logger = LoggerFactory.getLogger(rDionis.class);
       //private String                         host;
       private rShell                         sh;

       private BufferedInputStream            bufin;
       private HashMap<String,rDionisCommand> command; 

       public rDionis() {
                  sh     =new rShell();
                  bufin  =null;
                  String host   ="127.0.0.1";
                  sh.setHost(host);
                  command=new HashMap<String,rDionisCommand>();
       }
       public void  setUser  (String user  ) {
    	            sh.setUser(user);
       }
       public void  setPasswd(String passwd) {
    	            sh.setPasswd(passwd);
       }
       
       public boolean open() {
                  boolean ret;
                  ret=sh.open();
                  if(ret)bufin = new BufferedInputStream(sh.getIN()); 
                  return ret;
       }
       public void close() {
                   sh.close();
       }
 
       public String getDefNodeName(){ return "little";};
       public String getNodeName()   { return "littlecmd";}

       private Node _loadCFG(String cfg_filename) {
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               try {
                     DocumentBuilder builder;
                     builder  = factory.newDocumentBuilder();
                     Document doc = builder.parse(cfg_filename);
                     logger.trace("open doc:"+cfg_filename);
                     //-----------------------------------------------------------------------
                     Node node_cfg = doc.getFirstChild();
                     //-----------------------------------------------------------------------
                     if(getNodeName().equals(node_cfg.getNodeName())){
                        logger.trace("config structure name:"+getNodeName());
                        return node_cfg;
                     }
                     //-----------------------------------------------------------------------
                     if(getDefNodeName().equals(node_cfg.getNodeName())){
                         logger.trace("default config structure name:"+getDefNodeName());
                         logger.trace("seach topic name:"+getNodeName());
                         NodeList glist=node_cfg.getChildNodes();     
                         for(int i=0;i<glist.getLength();i++){
                             Node n=glist.item(i);
                             //logger.trace("topic["+i+"] name:"+n.getNodeName());
                             if(getNodeName().equals(n.getNodeName())) {
                            	 //logger.trace("topic["+i+"] is name:"+getNodeName());
                            	 return n;
                             }
                         }
                     } 
                     //-----------------------------------------------------------------------
               }
               catch(Exception e) {
                     logger.error("Could not load xml config file:"+cfg_filename, e);
                     return null;
              }
              return null;
       }
       private void loadGlobal(Node node_cfg){
           if(node_cfg!=null){
              logger.info("The configuration node:"+node_cfg.getNodeName());
              NodeList glist=node_cfg.getChildNodes();     
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("host".equals(n.getNodeName())){String host=n.getTextContent();  logger.info("host:"+host);sh.setHost(host);}
              }
           }    
           
       }
       private void loadCMD(Node node_cfg){
           if(node_cfg!=null){
               NodeList glist=node_cfg.getChildNodes();     
               for(int i=0;i<glist.getLength();i++){
                   Node           n        =glist.item(i);
                   String         name_node=n.getNodeName();
                   rDionisCommand cmd      =new rDionisCommand(sh,name_node);
                   if(cmd.loadCFG(n)!=false) command.put(name_node, cmd);
               }
           }                               
       }
       
       public boolean loadCFG(String cfg_filename) {
              Node node_cfg = _loadCFG(cfg_filename);
              
              if(node_cfg==null) {
            	 logger.error("no find topic:"+getNodeName());
            	 return false;
              }
              
              NodeList glist=node_cfg.getChildNodes();     
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  //logger.trace("topic["+i+"] name:"+n.getNodeName());
                  if("global_option".equals(n.getNodeName()))loadGlobal(n);
                  if("commanddi".equals(n.getNodeName()))loadCMD(n);

              }
              
              return true;
       }
       public boolean runCMD(String name) {
    	      logger.trace("cmd:"+name);
    	      rDionisCommand cmd = command.get(name);
              if(cmd==null)return false;
    	      logger.trace("run cmd:"+name);
              return cmd.run(bufin);
       }
       //-------------------------------------------------------------------------------   
       public static void main(String[] arg){
              boolean ret;
              rDionis apk = new rDionis();
              
              
              if(arg.length>1)apk.setUser(arg[1]);
              else            apk.setUser("adm");
              
              if(arg.length>2)apk.setPasswd(arg[2]);
              else            apk.setPasswd("2wsxXSW@");
             
              String f_name;
              if(arg.length>3)f_name=arg[3];
              else            f_name="littleproxy.xml";
             
              ret=apk.loadCFG(f_name);
              if(ret==false) {
                 System.out.println("apk.loadCFG return:"+ret);     
                 return;     
              }
             
              ret=apk.open();
              if(ret==false) {
                 System.out.println("apk.open return:"+ret);     
                 return;     
              }

              int count=1;
              while(ret){
                    if(arg.length<=(3+count))break;
                   
                    String cmd=arg[3+count];
                    System.out.println("apk."+cmd);     
                    ret=apk.runCMD(cmd);
                    count++;
                    System.out.println("apk."+cmd+" return:"+ret);     
              }
              apk.close();
              System.out.println("close connection"); 
            
            
        }

}
