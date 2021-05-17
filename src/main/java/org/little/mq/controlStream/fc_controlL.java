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

       private mq_util cntrl;

       public fc_controlL(){
              clear();
              cntrl=null;
       }


       @Override
       protected void   clear() {
              super.clear();
       }
       @Override
       public void init(Node node_cfg) {
              cntrl=new mq_util();
              cfg.init(node_cfg);
              try {
                   cntrl.open(cfg.getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());
              }
              catch (mqExcept m){
                     logger.error("cntrl.open ex:"+m);
                     try {cntrl.close();}catch (mqExcept m1){}
                     cntrl=null;
              }

       }
       @Override
       protected JSONObject setFlag(boolean flag) {
                 JSONObject root=new JSONObject();

    	         controlFlag(flag);

    	         mq_util cntrl=new mq_util();

                 if(isManual()||cntrl==null){
                    logger.error("can't set flag is_manual:"+isManual());
                    root.put("control", "MANUAL");
                    return root;
                 }
                 else
                 try {
                      //cntrl.open(cfg.getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());
                      if(isFlag())cntrl.putMsg(cfg.getNameQ(),"cuntrol q "+ new Date());
                      else        cntrl.clear (cfg.getNameQ());
                      //cntrl.close();
                 }
                 catch (mqExcept m){
                       logger.error("setFlag("+flag+") ex:"+m);
                       try {cntrl.close();}catch (mqExcept m1){}
                       cntrl=null;
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
                   //cntrl.open(cfg.getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());
                   len=cntrl.lengthLocalQueues(cfg.getNameQ());
                   //cntrl.close();
              }
              catch (mqExcept m){
                    logger.error("work() ex:"+m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    cntrl=null;
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
       @Override
       public void close(){
              super.close();
              if(cntrl==null)return;
              try {cntrl.close();}catch (mqExcept m1){}
              cntrl=null;
       }

}