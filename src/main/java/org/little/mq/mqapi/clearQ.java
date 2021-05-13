package org.little.mq.mqapi;

import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class clearQ {
       private static final Logger logger = LoggerFactory.getLogger(clearQ.class);

       static public void clear(String _qmname,String _host,int _port,String _channel,String _qname,String _user,String _passwd) {
              mq_mngr      qm;  
              mq_queue     q_in;
              qm    =null;   
              q_in  =null;
              qm=new mq_mngr(_qmname,_host,_port,_channel);
              logger.info("init router qm:"+_qmname+" queue:"+_qname+" bind host:"+_host+" port:"+_port+" channel:"+_channel);
              
              if(_user    !=null)qm.setUser(_user);
              if(_passwd  !=null)qm.setPassword(_passwd);
              if(_user    !=null)logger.info("set router qm:"+_qmname+" util:"+_user);
              if(_passwd  !=null)logger.info("set router qm:"+_qmname+" passwd:"+_passwd);
              
              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     logger.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     try {qm.close();} catch (mqExcept e1){}
                     return ;
              }
              logger.info("open manager:"+qm.getQMName());
              logger.info("open manager:"+qm.getQMName());
              try {
                q_in=qm.openTruncQ(_qname,1000);
                if(q_in!=null)logger.info("open q:"+_qname+" qm:"+q_in.getQMName());
                else          {logger.error("can't open q:"+_qname);return ;}
              } 
              catch (mqExcept e1){
                   logger.error("can't open q:"+_qname+" error:"+e1.getError()+" ex:"+e1);
                   try {qm.close();} catch (mqExcept e){}
                   qm=null;
                   return;
              }
              logger.trace("run clearQ("+q_in.getQName()+")");

              int count=0;
              int ret=-1;
              while(true){
                    mq_msg m=new mq_msg();
                    //-------------------------------------------------------------
                    try {
                        m.setMaxLen(0);
                        ret = q_in.get(m);
                        count++;
                        if(ret==def.RET_OK)logger.trace("get msg("+count+") processor("+q_in.getQName ()+") ");
                        else logger.trace("get msg("+count+") processor("+q_in.getQName ()+") ret:"+ret);

                    } 
                    catch (mqExcept ex1) {
                      logger.error("run clear("+q_in.getQName()+" ex:"+ex1);
                      continue;
                    }
                    if(ret!=def.RET_OK)break;
                    //-------------------------------------------------------------
              }
              logger.trace("stop processor("+q_in.getQName ()+") ret:"+ret);


              try{if(q_in !=null)qm.closeQ(q_in); q_in=null;} catch (mqExcept e1){}
              try{if(qm   !=null)qm.close();      qm=null;}   catch (mqExcept e1){}
       }
       static public void clear(String _qmname,String _qname) {
              mq_mngr      qm   =null;  
              mq_queue     q_in =null;

              qm=new mq_mngr(_qmname);
              logger.info("init qm:"+_qmname+" queue:"+_qname+" bind local");
              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     logger.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     try {qm.close();} catch (mqExcept e1){}
                     return;
              }
              logger.info("open manager:"+qm.getQMName());
              try {
                q_in=qm.openTruncQ(_qname,1000);
                if(q_in!=null)logger.info("open q:"+_qname+" qm:"+q_in.getQMName());
                else          {logger.error("can't open q:"+_qname);return ;}
              } 
              catch (mqExcept e1){
                   logger.error("can't open q:"+_qname+" error:"+e1.getError()+" ex:"+e1);
                   try {qm.close();} catch (mqExcept e){}
                   qm=null;
                   return;
              }

              logger.trace("run clearQ("+q_in.getQName()+")");

              int count=0;
              int ret=-1;
              while(true){
                    mq_msg m=new mq_msg();
                    //-------------------------------------------------------------
                    try {
                        m.setMaxLen(0);
                        ret = q_in.get(m);
                        count++;
                        if(ret==def.RET_OK)logger.trace("get msg("+count+") processor("+q_in.getQName ()+") ");
                        else logger.trace("get msg("+count+") processor("+q_in.getQName ()+") ret:"+ret);
                    } 
                    catch (mqExcept ex1) {
                      logger.error("run clear("+q_in.getQName()+" ex:"+ex1);
                      continue;
                    }
                    if(ret!=def.RET_OK)break;
                    //-------------------------------------------------------------
              }
              logger.trace("stop processor("+q_in.getQName ()+") ret:"+ret);

              try{if(q_in !=null)qm.closeQ(q_in); q_in=null;} catch (mqExcept e1){}
              try{if(qm   !=null)qm.close();      qm=null;}   catch (mqExcept e1){}

       }

}