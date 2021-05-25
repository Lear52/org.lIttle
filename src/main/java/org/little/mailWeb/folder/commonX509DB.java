package org.little.mailWeb.folder;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.commonDB;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonX509DB extends commonDB{
       private static final Logger logger = LoggerFactory.getLogger(commonX509DB.class);

       private String      db_table_eml;
       private String      db_table_x509;
       private String      db_table_serial;
       private String      db_seq;

       private String      log_type;
       private folderARH   log_obj;

       public commonX509DB() {
              clear();
              logger.info("create object commonX509DB");
       }

       public void clear() {
              super.clear();
              db_table_eml  ="arh_log";
              db_table_x509 ="arh_x509";
              db_table_eml  ="arh_serial";
              db_seq        ="arh_seq";
              log_type      ="log";
              log_obj       =null;
       }

       public String    getTableEml   () {return db_table_eml;}
       public String    getTableX509  () {return db_table_x509;}
       public String    getTableSerial() {return db_table_serial;}
       public String    getSeq        () {return db_seq;}

       public folderARH getFolder     () {return log_obj; }

       public void  init(Node _node_cfg) {
              super.init(_node_cfg);

              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());

                 if(_node_cfg.getAttributes().getNamedItem("type")!=null) {
                    log_type=_node_cfg.getAttributes().getNamedItem("type").getNodeValue();
                 }

                 logger.info("log_type:"+log_type);

                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("db_table_eml"    .equalsIgnoreCase(n.getNodeName())){db_table_eml=n.getTextContent();    logger.info("db_table_eml:"+db_table_eml);}
                     else
                     if("db_table_x509"   .equalsIgnoreCase(n.getNodeName())){db_table_x509=n.getTextContent();    logger.info("db_table_x509:"+db_table_x509);}
                     else
                     if("db_table_serial" .equalsIgnoreCase(n.getNodeName())){db_table_serial=n.getTextContent(); logger.info("db_table_serial:"+db_table_serial);}
                     else
                     if("db_seq"          .equalsIgnoreCase(n.getNodeName())){db_seq=n.getTextContent();          logger.info("db_seq:"+db_seq);}
                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 

              if("db" .equalsIgnoreCase(log_type)){log_obj=new folderDB(this); }
              else
              if("log".equalsIgnoreCase(log_type)){log_obj=new folderLogFile();}
              else{
                  logger.error("log_type not fined");
                  log_obj=new folderLogFile();
              }

              log_obj.open();

       }


}

