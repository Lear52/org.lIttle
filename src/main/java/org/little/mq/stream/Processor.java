package org.little.mq.stream;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.mq.stream.config.cfg_addr;
import org.little.mq.stream.config.cfg_interface;
import org.little.mq.stream.config.cfg_point;
import org.little.mq.stream.ufps.u_msg;
import org.little.mq.stream.ufps.ufpsParser;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.run.tfork;

public class Processor extends tfork{

       final private static String CLASS_NAME="org.little.stream.Processor";
       final private static int    CLASS_ID  =1605;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private int                        id;
       private mq_queue                   q_in;
       private mq_queue                   q_error;
       private mq_mngr_jms                   qm;
       private int                        flag_state;
       private cfg_interface              cfg_inter;
       private HashMap<String,mq_queue>   q_out;


       public Processor(int _id) {clear();id=_id;}

       public void clear() {
              qm         =null;   
              q_in       =null;
              q_error    =null;
              flag_state =0;
              cfg_inter       =null;
              id         =0;
              q_out      =new HashMap<String,mq_queue>();
       }

       public int getID() {return id;}

       public int init(mq_mngr_jms mngr_default,cfg_interface inc){
              cfg_inter=inc;
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
                    log.error("processor("+id+") openQ ex:"+e1);
                    return -1;
              }
              log.info("processor("+id+") open manager:"+mngr_default.getQMName());
              //-----------------------------------------------------------------------------------------------
              try{
                 q_in   =qm.openReadQ(cfg_inter.getSource(),def.MSGWAIT);
              } 
              catch (mqExcept e2){
                 close();
                 log.error("processor("+id+") openReadQ:"+cfg_inter.getSource()+" ex:"+e2);
                 return -1;
              }
              log.info("processor("+id+") open queue:"+cfg_inter.getSource()+" manager:"+mngr_default.getQMName());
              //-----------------------------------------------------------------------------------------------
              try{
                 q_error=qm.openWriteQ(cfg_inter.getError(),null);
              } 
              catch (mqExcept e3){
                 close();
                 log.error("processor("+id+") openErrorQ:"+cfg_inter.getError()+" ex:"+e3);
                 return -1;
              }
              log.info("processor("+id+") open queue:"+cfg_inter.getError()+" manager:"+mngr_default.getQMName());
              //-----------------------------------------------------------------------------------------------

              ArrayList<cfg_point> q_name=cfg_inter.getQ();
              int n=q_name.size();
              //log.info("processor("+id+") out queue:"+n);
              for(int i=0;i<n;i++){
                  mq_queue q_w=null;
                  cfg_point q_n=q_name.get(i);
                  try{
                      q_w =qm.openWriteQ(q_n.getQueueName(),q_n.getQMName());
                  } 
                  catch (mqExcept e2){
                     close();
                     log.error("processor("+id+") open queue:"+q_n.getQueueName()+" manager:"+q_n.getQMName()+" ex:"+e2);
                     flag_state=def.CMD_STOP;
                     return def.RET_ERROR;
                  }
                  q_out.put(q_n.getID(),q_w);
                  log.trace("processor("+id+") open queue:"+q_n.getQueueName()+" manager:"+q_n.getQMName());
              }
              
              //-----------------------------------------------------------------------------------------------
              flag_state=def.CMD_RUN;

              log.info("init processor("+id+") is ok");
              
              return def.RET_OK;
       }

       private ArrayList<cfg_addr> route(mq_msg  msg) {
               ArrayList<cfg_addr> outqueue=new ArrayList<cfg_addr>(16);
               byte[]  buf                  =null;
               try{
                  int len=msg.getLen();
                  buf = new byte[len];
                  msg.get(buf);
               } 
               catch (mqExcept e1){
              
                   return null;
               }
              
               ByteArrayInputStream input = new ByteArrayInputStream(buf);
               u_msg                ufbs  = new u_msg();
               try{
                   ufpsParser.parse(ufbs, input);
               } 
               catch (Except e1){
              
                   return null;
               }
               StringBuilder str_buf=new StringBuilder();

               str_buf.append("processor(").append(""+getID()).append(") route msg");

               
               for(int i=0;i<ufbs.getTO().size();i++){
                     String uis=ufbs.getTO().get(i);
                     str_buf.append(" to:").append(uis);

                     cfg_inter.getRoute(uis,outqueue);
               }
               str_buf.append(" from:").append(ufbs.getFROM());

               log.info(str_buf.toString());

               if(outqueue.size()==0)return null;
              
               return outqueue;
       }
        public void run() {

              if(qm==null)  {close();return ;}

              log.info("run processor("+id+") run:"+flag_state);

              if(q_in==null){close();return ;}
              int max_size_msg=def.BFRLNGTH_INIT;
              while(!isStop()){
                    int ret;

                    mq_msg m=new mq_msg();
                    //-------------------------------------------------------------
                    try {
                        m.setMaxLen(max_size_msg);
                        ret = q_in.get(m);
                        max_size_msg=m.getLen();
                    } 
                     catch (mqExcept ex1) {
                      log.error("processor("+id+") get msg input queue:"+cfg_inter.getSource()+" ex:"+ex1);
                      cmd_close();
                      continue;
                    }
                    if(ret==def.RET_WARN){
                       log.trace("processor("+id+") no msg input queue:"+cfg_inter.getSource());/**/
                       delayMs(def.MSGINTERVAL);
                       continue;
                    }
                    if(ret==def.RET_ERROR){
                       if(max_size_msg>def.BFRLNGTH_MAX){
                          log.error("processor("+id+") get msg input queue:"+cfg_inter.getSource()+" size_msg:"+max_size_msg);
                          cmd_close();
                       }
                       log.trace("processor("+id+") resize input msg:"+max_size_msg);
                       continue;
                    }
                    log.trace("processor("+id+") get msg input queue:"+cfg_inter.getSource());
                    //-------------------------------------------------------------
                    ArrayList<cfg_addr> outqueue=route(m);
                    //-------------------------------------------------------------
                    try {

                       if(outqueue!=null){
                          for(int i=0;i<outqueue.size();i++){
                               mq_queue q_w=null;
                               cfg_addr p=outqueue.get(i);
                               q_w=q_out.get(p.getID());
                               q_w.put(m);
                               log.trace("processor("+id+") put msg to output queue:"+p.getID());
                          }

                       }
                       else{
                          q_error.put(m);
                          log.trace("processor("+id+") get msg output queue:error");
                       }
                       qm.commit();
                       log.trace("processor("+id+") commit");

                    } catch (mqExcept ex2) {
                       log.error("processor.run ex:"+ex2);
                       continue;
                    }
                    //-------------------------------------------------------------
              }
              close();
              log.trace("stop processor("+id+") run:"+flag_state);
              //flag_state=-1;

       }
       public void  cmd_close(){
              flag_state=def.CMD_STOP;
       }
       private void close(){
               if((q_out != null) && (!q_out.isEmpty())) {
                   Iterator<mq_queue> q = q_out.values().iterator();
                   while(q.hasNext()) {
                        mq_queue queue = (mq_queue)q.next();
                        if (queue != null) try {qm.closeQ(queue);} catch (mqExcept e) {}
                   }
               }
               q_out =new HashMap<String,mq_queue>();
               try{if(q_in   !=null)qm.closeQ(q_in   );q_in=null;   }catch (mqExcept e1){}
               try{if(q_error!=null)qm.closeQ(q_error);q_error=null;}catch (mqExcept e1){}
               try{if(qm     !=null)qm.close();        qm=null;     }catch (mqExcept e1){}
               cmd_close();
               log.trace("stoping processor("+id+")");
               //log.trace("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
       }

       public boolean isStop() {
              return (flag_state<def.CMD_RUN);
       }






}