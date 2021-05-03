package org.little.util.db;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonDB{
       private static final Logger logger = LoggerFactory.getLogger(commonDB.class);

       private String      userName;
       private String      password;
       private String      db_drv  ;
       private String      db_url  ;

       public commonDB() {
              clear();
       }

       public void clear() {
              userName = "av";
              password = "123";          
              db_drv   ="";
              db_url   ="";
       }

       public String    getUser       () {return userName;}
       public String    getPasswd     () {return password;}
       public String    getDrv        () {return db_drv;}
       public String    getURL        () {return db_url;}


       public void  init(Node _node_cfg) {


              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());

                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("user"            .equalsIgnoreCase(n.getNodeName())){userName=n.getTextContent(); logger.info("user:"+userName);    }
                     else
                     if("password"        .equalsIgnoreCase(n.getNodeName())){password=n.getTextContent(); logger.info("password:"+password);}
                     else
                     if("db_drv"          .equalsIgnoreCase(n.getNodeName())){db_drv  =n.getTextContent(); logger.info("db_drv:"+db_drv);    }
                     else
                     if("db_url"          .equalsIgnoreCase(n.getNodeName())){db_url  =n.getTextContent(); logger.info("db_url:"+db_url);    }
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 


       }


}
