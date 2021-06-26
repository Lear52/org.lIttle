package org.little.db.kir;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonKIR  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonKIR.class);

       private commonKIRDB  db_cfg;
       private Node          node_box;
       private String        def_page;
       private String        error_page;
       
       public commonKIR() {
              clear();
              setNodeName("littlekey");
              logger.info("create commonArh");
       }

       @Override
       public void clear() {
              super.clear();          
              db_cfg      =new commonKIRDB();
              def_page    ="index.jsp";
              error_page  ="error.jsp";
       }
       
       public String       getDefPage        () {return def_page;          }
       public String       getErrorPage      () {return error_page;        }
       public Node         getNodeBox        () {return node_box;          }
 

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
              commonKIR cfg=new commonKIR();
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
