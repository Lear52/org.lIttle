package org.little.mailWeb;
             
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
       private String      db_table_eml;
       private String      db_table_x509;
       private String      db_table_serial;
       private String      db_seq;

       private String      log_type;
       private folderARH      log_obj;

       public commonDB() {
              clear();
       }

       public void clear() {
              userName = "av";
              password = "123";          
              db_drv   ="";
              db_url   ="";
              db_table_eml  ="arh_log";
              db_table_x509 ="arh_x509";
              db_table_eml  ="arh_serial";
              db_seq        ="arh_seq";
              log_type      ="log";
       }

       public String    getUser       () {return userName;}
       public String    getPasswd     () {return password;}
       public String    getDrv        () {return db_drv;}
       public String    getURL        () {return db_url;}
       public String    getTableEml   () {return db_table_eml;}
       public String    getTableX509  () {return db_table_x509;}
       public String    getTableSerial() {return db_table_serial;}
       public String    getSeq        () {return db_seq;}

       public folderARH getFolder   () {return log_obj; }

       public void  init(Node _node_cfg) {


              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());

                 if(_node_cfg.getAttributes().getNamedItem("type")!=null) {
                    log_type=_node_cfg.getAttributes().getNamedItem("type").getNodeValue();
                 }

                 logger.info("log_type:"+log_type);

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
                     else
                     //if("db_table"        .equalsIgnoreCase(n.getNodeName())){db_table_eml=n.getTextContent(); logger.info("db_table:"+db_table_eml);}
                     //else
                     if("db_table_eml"    .equalsIgnoreCase(n.getNodeName())){db_table_eml=n.getTextContent(); logger.info("db_table_eml:"+db_table_eml);}
                     else
                     if("db_table_x509"   .equalsIgnoreCase(n.getNodeName())){db_table_eml=n.getTextContent(); logger.info("db_table_x509:"+db_table_x509);}
                     else
                     if("db_table_serial" .equalsIgnoreCase(n.getNodeName())){db_table_serial=n.getTextContent(); logger.info("db_table_serial:"+db_table_serial);}
                     else
                     if("db_seq"          .equalsIgnoreCase(n.getNodeName())){db_seq=n.getTextContent(); logger.info("db_seq:"+db_seq);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

              if("db" .equalsIgnoreCase(log_type)){log_obj=new folderDB0(this); }
              if("log".equalsIgnoreCase(log_type)){log_obj=new folderLogFile();}

              log_obj.open();

       }


}