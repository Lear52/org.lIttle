package org.little.stream;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.common;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonStream  extends common{
       private static final Logger logger = LoggerFactory.getLogger(commonStream.class);

       private String        def_page;
       private String        error_page;
       
       public commonStream() {
              clear();
              setNodeName("littlestream");
              logger.info("create commonStream");
       }

       @Override
       public void clear() {
              super.clear();          
              def_page    ="index.jsp";
              error_page  ="error.jsp";
       }
       
       public String       getDefPage        () {return def_page;          }
       public String       getErrorPage      () {return error_page;        }
 

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
                     //else
                     //if("db"      .equalsIgnoreCase(n.getNodeName())){db_cfg.init(n);}
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
              commonStream cfg=new commonStream();
              String xpath  =args[0];

              if(cfg.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.STREAM "+ver());
              cfg.init();
              logger.info("RUN LITTLE.STREAM "+ver());

       }

}
