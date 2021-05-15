package org.little.mq.controlStream;
        
import java.util.Date;

import org.json.JSONObject;
import org.little.mq.mqapi.mq_contrl;
import org.little.mq.mqapi.mq_util;
import org.little.mq.mqapi.mqExcept;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

public class fc_controlL extends fc_control{
       private static final Logger logger = LoggerFactory.getLogger(fc_controlL.class);

       //private String mq_host;
       //private int    mq_port;
       //private String mq_user;
       //private String mq_passwd;
       //private String mq_channel;
       

       public fc_controlL(){
              clear();
       }


       @Override
       protected void   clear() {
              super.clear();
              //mq_host   =null;
              //mq_port   =1414;
              //mq_user   =null;
              //mq_passwd =null;
              //mq_channel=null;
       }
       @Override
       public void init(Node node_cfg) {
           cfg.init(node_cfg);
           /*
    	   if(node_cfg==null)return;
              logger.info("The configuration node:"+node_cfg.getNodeName());

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("mngr"    .equals(n.getNodeName())){String mq_mngr   =n.getTextContent();logger.info("mngr:"    +mq_mngr   );setNameMngr(mq_mngr);}else
                  if("queue"   .equals(n.getNodeName())){String mq_queue  =n.getTextContent();logger.info("queue:"   +mq_queue  );setNameQ(mq_queue);  }else
                  if("host"    .equals(n.getNodeName())){mq_host   =n.getTextContent();logger.info("host:"    +mq_host   );}else
                  if("port"    .equals(n.getNodeName())){String    s=n.getTextContent();try{mq_port=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set deep:"    +s);mq_port=1414;}logger.info("port:"    +mq_port   );}else
                  if("user"    .equals(n.getNodeName())){mq_user   =n.getTextContent();logger.info("user:"    +mq_user   );}else
                  if("password".equals(n.getNodeName())){mq_passwd =n.getTextContent();logger.info("password:"+mq_passwd );}else
                  if("channel" .equals(n.getNodeName())){mq_channel       =n.getTextContent();logger.info("channel:" +mq_channel  );}
             }
            */

       }
       @Override
       protected JSONObject setFlag(boolean flag) {
                 JSONObject root=new JSONObject();

    	         controlFlag(flag);

    	         mq_util cntrl=new mq_util();

                 if(isManual()){
                    logger.error("can't set flag is_manual:"+isManual());
                    root.put("control", "MANUAL");
                    return root;
                 }
                 else
                 try {
                      cntrl.open(cfg.getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());
                      if(isFlag())cntrl.putMsg(cfg.getNameQ(),"cuntrol q "+ new Date());
                      else        cntrl.clear (cfg.getNameQ());
                      cntrl.close();
                 }
                 catch (mqExcept m){
                       logger.error("setFlag("+flag+") ex:"+m);
                       try {cntrl.close();}catch (mqExcept m1){}
                       isFlag(false);
                       root.put("control", "ERROR");
                       return root;
                 }

                 root.put("control", "OK");

                 return root;
       }

       @Override
       public void work(){
              logger.info("1 work() queue:"+cfg.getNameQ()+" flag:"+isFlag());
              mq_contrl cntrl=new mq_contrl();
              int len=0;
              try {
                   cntrl.open(cfg.getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());
                   len=cntrl.lengthLocalQueues(cfg.getNameQ());
                   cntrl.close();
              }
              catch (mqExcept m){
                    logger.error("work() ex:"+m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    isFlag(false);
                    return;
              }

              if(len>0){
                 if(isFlag()==false)isManual(true);
                 if(isFlag()==true )isManual(false);
                 //isFlag(true); else isFlag(false);
              }
              else{
                 isManual(false);
                 isFlag(false);
              }
              logger.info("2 work() queue:"+cfg.getNameQ()+" flag:"+isFlag());

       }

}