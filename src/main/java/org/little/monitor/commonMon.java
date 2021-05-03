package org.little.monitor;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.little.util.db.commonDB;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonMon  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonMon.class);

       private int         task_timeout;
       private commonDB    db_cfg;
       private String      def_page;
       private String      error_page;

       public commonMon() {
              clear();
              setNodeName("littlemon");
       }

       @Override
       public void clear() {
              super.clear();             
              db_cfg                 =new commonDB();
              def_page               ="index.html";
              error_page             ="error.html";
       }
       

       public int      getTimeout        () {return task_timeout;      }
       public commonDB getCfgDB          () {return db_cfg;            }
       public String   getDefPage        () {return def_page;          }
       public String   getErrorPage      () {return error_page;        }
       public dbMAC    getDB             () {return new dbMAC(db_cfg); }


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
                     if("db"      .equalsIgnoreCase(n.getNodeName())){db_cfg.init(n);}
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
                     if("run_timeout"       .equalsIgnoreCase(n.getNodeName())){String s          =n.getTextContent();try{task_timeout=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set run_timeout:"+s);task_timeout=10;}logger.info("run_timeout:"+task_timeout);}
                     else
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
              commonMon mngr=new commonMon();
              String xpath  =args[0];

              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.MONITOR "+ver());
              mngr.init();
              logger.info("RUN LITTLE.MONITOR "+ver());

      }

}
