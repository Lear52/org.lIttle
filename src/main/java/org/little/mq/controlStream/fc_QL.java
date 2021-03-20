package org.little.mq.controlStream;

import org.json.JSONObject;
import org.little.mq.mqapi.mq_contrl;
import org.little.mq.mqapi.mqExcept;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class fc_QL extends fc_Q{
       private static final Logger logger = LoggerFactory.getLogger(fc_QL.class);
       
       private String mq_host;
       private int    mq_port;
       private String mq_user;
       private String mq_passwd;
       private int    deep_alarm;

       @Override
       public void   clear() {
    	      super.clear();
              mq_host   =null;
              mq_port   =1414;
              mq_user   =null;
              mq_passwd =null;
              deep_alarm=150;
       }

       @Override
       public JSONObject getState() {
              JSONObject q=super.getState();
              logger.info("getState() queue:"+getNameQ()+" len:"+getDeepQ());

              return q;
       }
       @Override
       public void work() {
              logger.info("1 work() queue:"+getNameQ()+" len:"+getDeepQ());
              mq_contrl cntrl=new mq_contrl();
              int len=0;
              try {
                   cntrl.open(getNameMngr(),mq_host,mq_port,"SYSTEM.ADMIN.SVRCONN",mq_user,mq_passwd);
                   len=cntrl.lengthLocalQueues(getNameQ());

                   if(deep_alarm<len)isAlarm(true);
                   else isAlarm(false);

                   cntrl.close();
              }
              catch (mqExcept m){
                    logger.error("work() ex:"+m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    setDeepQ(-1);
                    return;
              }
              setDeepQ(len);
              logger.info("2 work() queue:"+getNameQ()+" len:"+getDeepQ());
       }
       @Override
       public void init(Node node_cfg) {
              if(node_cfg==null)return;
              logger.info("The configuration node:"+node_cfg.getNodeName());

              NodeList glist=node_cfg.getChildNodes();
              if(glist==null) return;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("mngr"    .equals(n.getNodeName())){String mq_mngr   =n.getTextContent();logger.info("mngr:"    +mq_mngr   );setNameMngr(mq_mngr);}else
                  if("queue"   .equals(n.getNodeName())){String mq_queue  =n.getTextContent();logger.info("queue:"   +mq_queue  );setNameQ(mq_queue);  }else
                  if("host"    .equals(n.getNodeName())){mq_host          =n.getTextContent();logger.info("host:"    +mq_host   );                     }else
                  if("port"    .equals(n.getNodeName())){String          s=n.getTextContent();try{mq_port=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set port:"+s);mq_port=1414;}logger.info("port:"+mq_port        );}else
                  if("user"    .equals(n.getNodeName())){mq_user          =n.getTextContent();logger.info("user:"    +mq_user   );                     }else
                  if("password".equals(n.getNodeName())){mq_passwd        =n.getTextContent();logger.info("password:"+mq_passwd );                     }else
                  if("deep"    .equals(n.getNodeName())){String          s=n.getTextContent();try{deep_alarm=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set deep:"+s);deep_alarm=150;}logger.info("deep:"+deep_alarm);}
              }


       }

}

