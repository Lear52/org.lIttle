package org.little.db.kir;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.commonDB;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonKIRDB extends commonDB{
       private static final Logger logger = LoggerFactory.getLogger(commonKIRDB.class);


       public commonKIRDB() {
              clear();
              logger.info("create object commonKIRDB");
       }

       public void clear() {
              super.clear();
       }


       public void  init(Node _node_cfg) {
              super.init(_node_cfg);
       }


}

