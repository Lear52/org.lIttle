package org.little.mq.stream;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.util.Logger;
import org.little.util.run.tfork;

public class Command extends tfork{

       final private static String CLASS_NAME="org.little.stream.Command";
       final private static int    CLASS_ID  =1602;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private mq_queue q_cmd;
       private mq_mngr_jms  qm;
       private int      flag_state;


       public Command() {clear();}

       public void clear() {
              qm   =null;   
              q_cmd=null;
              flag_state =0;
       }


       public int start(mq_mngr_jms mngr_default){
              //-----------------------------------------------------------------------------------------------
              if(mngr_default.isLocal()){
                 qm=new mq_mngr_jms(mngr_default.getQMName());
              }
              else{
                 qm=new mq_mngr_jms(mngr_default.getQMName(),mngr_default.getHost(),mngr_default.getPort(),mngr_default.getChannel());
              }
              try{
                 qm.open();
              } 
              catch(mqExcept e1){
                    close();
                    log.error("command open manager:"+mngr_default.getQMName()+" ex:"+e1);
                    return -1;
              }
              log.info("command open manager:"+mngr_default.getQMName());
              String qn=def.CMDQUEUE;
              try {
                 q_cmd=qm.openExclReadQ(qn,def.CMDWAIT);
                 if(q_cmd!=null)log.info("open q:"+qn+" qm:"+q_cmd.getQMName());
                 else           log.error("can't open q:"+def.CMDQUEUE);
                 
              } 
              catch (mqExcept e1){
                     if(e1.getCode()==2042){
                        log.error("can't open q:"+qn+" queue is use");
                     }
                     else log.error("can't open q:"+qn+" error:"+e1.getError()+" ex:"+e1);
                    try {qm.close();} catch (mqExcept e){}
                    return -1;
              }
              flag_state=1;
              fork();
              return 0;
       }
       public static void send_stop(mq_mngr_jms mngr_default){
    	      log.info("begin send stop router");
              mq_queue q_cmd=null;
              try {
                 mq_msg m=new mq_msg();
                 m.setMaxLen();

                 //mngr_default.open();
                 q_cmd=mngr_default.openWriteQ(def.CMDQUEUE,null);
                 m.put(def.CMDSTOP);
                 q_cmd.put(m);
                 mngr_default.closeQ(q_cmd);
                 mngr_default.close();
              } 
              catch (mqExcept e1){
                 try {
                    mngr_default.closeQ(q_cmd);
                    mngr_default.close();
                 } catch (mqExcept e2){}
    	         log.error("send stop router ex:"+e1);

                 return ;
              }
    	      log.info("end send stop router");
       }

       public void run(){
              if(qm==null){close();return ;}
              if(q_cmd==null){close();return ;}

              while(!isStop()){
                    int    ret;
                    mq_msg cmd=new mq_msg();

                    //log.trace("wait cmd msg");
                    try {
                         cmd.setMaxLen(1024);
                         ret = q_cmd.get(cmd);
                         if(ret==def.RET_OK){
                            byte[]  buf=null;
                            int len=cmd.getLen();
                            buf = new byte[len];
                            cmd.get(buf);
                            String s=new String(buf);
                            log.trace("Command get msg:"+s);
                            if(s.equals(""))continue;
                            break;
                         }
                         else
                         if(ret==def.RET_WARN)log.trace("no cmd msg");
                         else{
                            log.error("error get cmd msg");
                            break;
                         }
                    } 
                    catch(mqExcept e) {
                         log.error("get cmd msg ex:"+e);
                         break;
                    }
                    delayMs(def.CMDINTERVAL);
                    //log.trace("continue cmd msg");
              }
              cmd_close();
              close();

       }
       public void  cmd_close(){
              flag_state=-1;
       }
       private void close(){
               cmd_close();
               try{if(q_cmd  !=null)qm.closeQ(q_cmd  );q_cmd=null;  }catch (mqExcept e1){}
               try{if(qm     !=null)qm.close();        qm=null;     }catch (mqExcept e1){}
               cmd_close();
               log.trace("stoping command");
               //log.trace("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
       }

       public boolean isStop() {
              return (flag_state<1);
       }






}