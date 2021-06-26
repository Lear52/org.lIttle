package org.little.mq.test.potok;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr;
import org.little.mq.mqapi.mq_queue;
import org.little.util.Logger;
import org.little.util.run.tfork;

import com.ibm.mq.MQException;

public class potokMsg extends tfork{
       final private static String CLASS_NAME="prj0.potok.potokMsg";
       final private static int    CLASS_ID  =2404;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       protected mq_mngr      qm;
       protected mq_queue     q_in;
       protected mq_queue     q_out;
       protected int          flag_state;
       protected static int   flag_global_state=def.CMD_RUN;

       public potokMsg() {
              clear();
       }
       public void clear() {
              qm             =null;   
              q_in           =null;
              q_out          =null;
              flag_state     =def.CMD_INIT;
              MQException.log=null;
       }

       protected int init(String _qmname,String _qname_in,String _qname_out){
              clear();
              qm=new mq_mngr(_qmname);

              log.info("init qm:"+_qmname+" queue_in:"+_qname_in+" queue_out:"+_qname_out+" bind local");

              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     close();
                     return def.RET_FATAL;
              }

              log.info("open manager:"+qm.getQMName()+" ret:ok");

              int ret=def.RET_OK;
              if(_qname_in !=null){ret=openIN(_qname_in);  if(ret!=def.RET_OK)return ret;}
              if(_qname_out!=null){ret=openOUT(_qname_out);if(ret!=def.RET_OK)return ret;}
              cmd_start();
              return ret;
       }
       protected int init(String _qmname,String _host,int _port,String _channel,String _qname_in,String _qname_out,String _user,String _passwd){
               clear();
               qm=new mq_mngr(_qmname,_host,_port,_channel);
               log.info("init router qm:"+_qmname+" queue_in:"+_qname_in+" queue_out:"+_qname_out+" bind host:"+_host+" port:"+_port+" channel:"+_channel);

               if(_user    !=null)qm.setUser(_user);
               if(_passwd  !=null)qm.setPassword(_passwd);
               if(_user    !=null)log.info("set router qm:"+_qmname+"util:"+_user);
               if(_passwd  !=null)log.info("set router qm:"+_qmname+"passwd:"+_passwd);

               try {
                   qm.open();
               }
               catch (mqExcept e) {
                      log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                      close();
                      return def.RET_FATAL;
               }
               log.info("open manager:"+qm.getQMName());

               int ret=def.RET_OK;
               if(_qname_in !=null){ret=openIN(_qname_in);  if(ret!=def.RET_OK)return ret;}
               if(_qname_out!=null){ret=openOUT(_qname_out);if(ret!=def.RET_OK)return ret;}
              
               cmd_start();
               return ret;
       }
       protected int  openIN(String qn){
               if(qm==null)return def.RET_ERROR;
               try {
                 q_in=qm.openTruncQ(qn,10000);
                 if(q_in!=null)log.info("open q:"+qn+" qm:"+q_in.getQMName());
                 else          {log.error("can't open q:"+qn);return def.RET_ERROR;}
               } 
               catch (mqExcept e1){
                    log.error("can't open q:"+qn+" error:"+e1.getError()+" ex:"+e1);
                    try {qm.close();} catch (mqExcept e){}
                    qm=null;
                    return def.RET_WARN;
               }
               log.info("run cfg router");

               return def.RET_OK;
       }
       protected int  openOUT(String qn){
               if(qm==null)return def.RET_ERROR;
               try {
                 q_out=qm.openWriteQ(qn);
                 if(q_out!=null)log.info("open q:"+qn+" qm:"+q_out.getQMName());
                 else          {log.error("can't open q:"+qn);return def.RET_ERROR;}
               } 
               catch (mqExcept e1){
                    log.error("can't open q:"+qn+" error:"+e1.getError()+" ex:"+e1);
                    try {qm.close();} catch (mqExcept e){}
                    qm=null;
                    return def.RET_WARN;
               }
               log.info("run cfg router");

               return def.RET_OK;
       }
       protected void close(){
               cmd_close();
               try{if(q_out!=null)qm.closeQ(q_out);q_out=null;}catch (mqExcept e1){q_out=null;}
               try{if(q_in !=null)qm.closeQ(q_in); q_in=null;} catch (mqExcept e1){q_in =null;}
               try{if(qm   !=null)qm.close();      qm=null;}   catch (mqExcept e1){qm   =null;}
       }
       public void  cmd_start(){
              flag_state=def.CMD_RUN;
       }
       public void  cmd_close(){
              flag_state=def.CMD_STOP;
       }
       public void  cmd_g_close(){
              flag_global_state=def.CMD_STOP;
       }
       public boolean isStop() {
              return (flag_state<def.CMD_RUN)||(flag_global_state<def.CMD_RUN);
       }

       public void run() { }


}