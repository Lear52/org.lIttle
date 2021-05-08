package org.little.syslog.impl;
        
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;

import org.little.mq.mqapi.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList ;

public class printEventMQ  implements printEvent {
       private static Logger logger = LoggerFactory.getLogger(printEventLog.class);
       private String   mq_mngr     ;
       private String   mq_queue    ;
       private String   mq_host     ;
       private int      mq_port     ;
       private String   mq_user     ;
       private String   mq_passwd   ;
       private String   mq_channel  ;
       private mq_mngr  queueManager;
       private mq_queue q_out;

       public printEventMQ(){
              mq_mngr     ="QM";
              mq_queue    ="TEST";
              mq_host     ="localhost";
              mq_port     =1414;
              mq_user     ="no user";
              mq_passwd   ="";
              mq_channel  ="SYSTEM.DEF.SVRCONN";
              queueManager=null;
              q_out       =null;
       }
       public void open(){
              logger.trace("starting open manager:"+mq_queue+"@"+mq_mngr+" ch:"+mq_channel+" host:"+mq_host+":"+mq_port);

              queueManager=new mq_mngr(mq_mngr,mq_host,mq_port, mq_channel);

              queueManager.setUser    (mq_user);
              queueManager.setPassword(mq_passwd);

              try {
                  queueManager.open();
              }
              catch (mqExcept e1){
                   logger.error("can't open qm:"+mq_mngr+" error:"+e1.getError()+" ex:"+e1);
                   return;
              }

              try {
                 q_out=queueManager.openWriteSQ(mq_queue) ;
                 if(q_out==null){logger.error("can't open q:"+mq_queue+"@"+mq_mngr);return ;}
              }
              catch (mqExcept e1){
                   logger.error("can't open q:"+mq_queue+"@"+mq_mngr+" error:"+e1.getError()+" ex:"+e1);
                   return;
              }

              logger.info("open q:"+mq_queue+"@"+mq_mngr+" qm:"+q_out.getQMName());

       }

       public void close(){
              try { if(q_out!=null && queueManager!=null)queueManager.closeQ(q_out); } catch (Exception e2) {}
              try { if(queueManager!=null)queueManager.close(); } catch (Exception e2) {}
              q_out=null;
              queueManager=null;

       }

       @Override
       public void print(SyslogServerEventIF event){
              try {
                  mq_msg m=new mq_msg();
                  m.setMaxLen();
                  m.put(event.getRaw());
                  q_out.put(m);
              }
              catch (mqExcept e1){
                   logger.error("can't put q:"+mq_queue+" error:"+e1.getError()+" ex:"+e1);
                   return;
              }

              //logger.trace(new String(event.getRaw()));
       }
       
       public void setMngr   (String mq_mngr   ) {this.mq_mngr = mq_mngr;       }
       public void setQueue  (String mq_queue  ) {this.mq_queue = mq_queue;}
       public void setHost   (String mq_host   ) {this.mq_host = mq_host;       }
       public void setPort   (int    mq_port   ) {this.mq_port = mq_port;}
       public void setUser   (String mq_user   ) {this.mq_user = mq_user;}
       public void setPasswd (String mq_passwd ) {this.mq_passwd = mq_passwd;       }
       public void setChannel(String mq_channel) {this.mq_channel = mq_channel;}

       public boolean init(Node _node_cfg){
              if(_node_cfg==null)return false;
              logger.info("The configuration node:"+_node_cfg.getNodeName()+" for commonSyslog");

              NodeList glist=_node_cfg.getChildNodes();
              if(glist==null) return false;
              for(int i=0;i<glist.getLength();i++){
                  Node n=glist.item(i);
                  if("mngr"    .equals(n.getNodeName())){String _mq_mngr  =n.getTextContent();logger.info("mngr:"    +_mq_mngr   );setMngr(_mq_mngr);  }else
                  if("queue"   .equals(n.getNodeName())){String _mq_queue =n.getTextContent();logger.info("queue:"   +_mq_queue  );setQueue(_mq_queue);}else
                  if("host"    .equals(n.getNodeName())){mq_host          =n.getTextContent();logger.info("host:"    +mq_host   );                     }else
                  if("port"    .equals(n.getNodeName())){String          s=n.getTextContent();try{mq_port=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set port:"+s);mq_port=1414;}logger.info("port:"+mq_port        );}else
                  if("user"    .equals(n.getNodeName())){mq_user          =n.getTextContent();logger.info("user:"    +mq_user   );                     }else
                  if("password".equals(n.getNodeName())){mq_passwd        =n.getTextContent();logger.info("password:"+mq_passwd );                     }else
                  if("channel" .equals(n.getNodeName())){String _mq_channel=n.getTextContent();logger.info("channel:" +_mq_channel  );setChannel(_mq_channel);}
              }

              logger.info("The configuration node:"+_node_cfg.getNodeName()+" for commonSyslog ");
              return true;
       }

          
}
