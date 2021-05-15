package org.little.mailWeb;
             
import org.little.mailWeb.alarm.commonAlarm;
import org.little.mailWeb.folder.commonX509DB;
import org.little.mailWeb.folder.folderARH;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonArh  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonArh.class);

       private commonX509DB  db_cfg;
       private commonAlarm   alarm_cfg;
       private Node          node_box;
       private String        def_page;
       private String        error_page;
       
       public commonArh() {
              clear();
              setNodeName("littlekey");
              logger.info("create commonArh");
       }

       @Override
       public void clear() {
              super.clear();          
              node_box    =null;
              db_cfg      =new commonX509DB();
              alarm_cfg   =new commonAlarm();
              def_page    ="index.jsp";
              error_page  ="error.jsp";
       }
       
       public folderARH    getFolder         () {return db_cfg.getFolder();}
       public String       getDefPage        () {return def_page;          }
       public String       getErrorPage      () {return error_page;        }
       public Node         getNodeBox        () {return node_box;          }
       public commonAlarm  getAlarm          () {return alarm_cfg;         }


       @Override
       public void init() {
            init(this.getNode());
       }
       @Override
       public void  init(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("global_option".equalsIgnoreCase(n.getNodeName())){initGlobal(n);}
                     else
                     if("log_arh"      .equalsIgnoreCase(n.getNodeName())){db_cfg.init(n);}
                     else
                     if("load"         .equalsIgnoreCase(n.getNodeName())){node_box=n;}
                     else
                     if("alarm_option" .equalsIgnoreCase(n.getNodeName())){alarm_cfg.init(n);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

       }

       private void initGlobal(Node _node_cfg) {
              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());
                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("def_page"          .equalsIgnoreCase(n.getNodeName())){def_page          =n.getTextContent(); logger.info("def_page:"+def_page);}
                     else
                     if("error_page"        .equalsIgnoreCase(n.getNodeName())){error_page        =n.getTextContent(); logger.info("error_page:"+error_page);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

      }


       public static void main(String args[]){
              commonArh cfg=new commonArh();
              String xpath  =args[0];

              if(cfg.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+ver());
              cfg.init();
              logger.info("RUN LITTLE.CONTROLSTREAM "+ver());

       }

}
