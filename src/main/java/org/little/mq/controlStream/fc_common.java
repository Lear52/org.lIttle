package org.little.mq.controlStream;
       
import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.scheduler;
import org.little.util.run.task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.little.util.common;

public class fc_common  extends common{
       private static final Logger logger = LoggerFactory.getLogger(fc_mngr.class);

       public fc_common() {
              setNodeName("littlestat");
       }

       @Override
       public void init() {
            init(this.getNode());
       }



       public static void main(String args[]){
              fc_common mngr=new fc_common();
              String xpath  =args[0];

              if(mngr.loadCFG(xpath)==false){
                 logger.error("error read config file:"+xpath);
                 return;
              }
              logger.info("START LITTLE.CONTROLSTREAM "+ver());
              mngr.init();
              logger.info("RUN LITTLE.CONTROLSTREAM "+ver());

       }
}
