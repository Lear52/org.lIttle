package org.little.rcmd;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.little.rcmd.rsh.rCommand;
import org.little.rcmd.rsh.rShell;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.string._stringQSort;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class rAPK {

       private static Logger         logger = LoggerFactory.getLogger(rAPK.class);
       private rShell                sh;
       private BufferedInputStream   buf_input;
       private commonRCMD            cfg;

       public rAPK() {
              sh     =new rShell();
              buf_input  =null;
              sh.setHost("127.0.0.1");
              cfg=new commonRCMD();
       }
       public void  setUser  (String user  ) {sh.setUser(user);}
       public void  setPasswd(String passwd) {sh.setPasswd(passwd);}
       
       public boolean open() {
              boolean ret;
              ret=sh.open();
              if(ret)buf_input = new BufferedInputStream(sh.getIN()); 
              logger.trace("open channel to:"+sh.getHost()+" ret:"+ret);
              return ret;
       }
       public void close() {
              logger.trace("close channel to:"+sh.getHost());
              sh.close();
       }
 
       private String getDefNodeName(){ return "little";};
       private String getNodeName()   { return "littlecmd";}


       public boolean loadCFG(String cfg_filename) {
              Node node_cfg = findCFG(cfg_filename);
              return loadCFG(node_cfg);
       }
       public boolean loadCFG(Node node_cfg) {
              if(node_cfg==null) {
                 logger.error("no find topic:"+getNodeName());
                 return false;
              }
              
              NodeList glist=node_cfg.getChildNodes();     
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("global_option".equals(n.getNodeName()))cfg.init(n);
                  if("commanddi".equals(n.getNodeName()))cfg.loadCMD(n);
              }
              if(cfg.getHost()  !=null)sh.setHost(cfg.getHost());
              if(cfg.getUser()  !=null)sh.setUser(cfg.getUser());
              if(cfg.getPasswd()!=null)sh.setPasswd(cfg.getPasswd());
              
              return true;
       }
       

       //-------------------------------------------------------------------------------
       public String [] listCMD() {
              ArrayList<String> list_cmds= new ArrayList<String>();

              HashMap<String,rCommand>  map=cfg.getCMD();
              Iterator<rCommand> list = map.values().iterator();
              while(list.hasNext()) {
            	  rCommand r = list.next();
                  if(r==null)continue;
                  list_cmds.add(r.getID());
              }
              
              String[] ls=list_cmds.toArray(new String[list_cmds.size()]);
              _stringQSort sorter = new _stringQSort();
              sorter.sort(ls);
              return ls;
       }
       public String[] runCMD(String name) {
              //logger.trace("cmd:"+name);
              rCommand cmd = cfg.getCMD().get(name);
              if(cmd==null) {
                 logger.trace("findn't cmd:"+name);
                 return null;
              }
              logger.trace("run cmd:"+name);
              boolean ret=cmd.run(sh,buf_input);
              if(ret==false)return null;
              return cmd.print();
       }
       //-------------------------------------------------------------------------------

       private Node findCFG(String cfg_filename) {
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

}
