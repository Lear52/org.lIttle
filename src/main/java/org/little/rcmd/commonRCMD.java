package org.little.rcmd;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;


/**
 * 
 */
public class commonRCMD{

       private static final Logger logger = LoggerFactory.getLogger(commonRCMD.class);



       public commonRCMD(){clear();}

       public void clear(){

       }
       public void init(NodeList glist){
              if(glist==null) return;
              logger.info("The configuration rcmd");
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  //else
              }
       }



}

