package org.little.mq.mqapi;

import org.little.util.Logger;

public class mq_util{
    private static Logger log=new Logger(mq_util.class);

       private mq_mngr         queueManager;
       private boolean         flag_stop;
       public static final int CFGWAIT = 1000;

       public mq_util(){
              clear();
       }

       public void clear() {
              queueManager = new mq_mngr();
              flag_stop     = false;
       }

       public int open(String _qmname,String _host,int _port,String _channel,String user,String passwd) throws  mqExcept {
              queueManager=new mq_mngr(_qmname,_host,_port, _channel);
              queueManager.setUser    (user);
              queueManager.setPassword(passwd);

              queueManager.open();
              return 0;
       }

      
       public void close() throws  mqExcept{
              try {
                  if(queueManager!=null)queueManager.close();
              }
              catch (Exception e2) {
                     mqExcept e1=new mqExcept("pcf agent disconnect",e2);
                     if(!queueManager.isLocal()) log.error("Error open mngr:"+queueManager.getQMName()+" host:"+queueManager.getHost()+" port:"+queueManager.getPort()+" channel:"+queueManager.getChannel()+" ex:"+e1);
                     else       log.error("Error open mngr:"+queueManager.getQMName()+" ex:"+e1);
                     throw e1;
              }

       }

       public boolean isStop() {return flag_stop;}
       public void isStop(boolean f) {flag_stop=f;}

       public void clear(String _qname) throws  mqExcept{
               mq_queue q_in=null;

               try {
                 q_in=queueManager.openTruncQ(_qname,CFGWAIT);
                 if(q_in==null){log.error("can't open q:"+_qname+" qm:");return ;}
               } 
               catch (mqExcept e1){
                    log.error("can't open q:"+_qname+" error:"+e1.getError()+" ex:"+e1);
                    return;
               }

               log.info("open q:"+_qname+" qm:"+q_in.getQMName());

               while(!isStop()){
                     int ret;
                     mq_msg m=new mq_msg();
                     try {
                         m.setMaxLen(0);
                         ret = q_in.get(m);
                         if(ret==def.RET_OK)log.trace("clear msg processor("+q_in.getQName ()+") ");
                         else
                         if(ret==def.RET_WARN)break;
                     } 
                     catch (mqExcept ex1) {
                         log.error("run clear("+q_in.getQName()+" ex:"+ex1);
                         break;
                    }
               }

               log.info("clear q:"+_qname+" qm:"+q_in.getQMName());

               try{if(q_in !=null)queueManager.closeQ(q_in); q_in=null;} catch (mqExcept e1){}

       }
       public void putMsg(String _qname,String txt) throws  mqExcept{
              mq_queue q_out=null;

              try {
                 q_out=queueManager.openWriteSQ(_qname) ;
                 if(q_out==null){log.error("can't open q:"+_qname);return ;}
              }
              catch (mqExcept e1){
                   log.error("can't open q:"+_qname+" error:"+e1.getError()+" ex:"+e1);
                   return;
              }

              log.info("open q:"+_qname+" qm:"+q_out.getQMName());

              try {
                  mq_msg m=new mq_msg();
                  m.setMaxLen();
                  m.put(txt);
                  q_out.put(m);
              }
              catch (mqExcept e1){
                   log.error("can't put q:"+_qname+" error:"+e1.getError()+" ex:"+e1);
                   return;
              }

              log.info("put q:"+_qname+" qm:"+q_out.getQMName());

              try{if(q_out !=null)queueManager.closeQ(q_out); q_out=null;} catch (mqExcept e1){}
       }
      
              
       public static void main(String[] args) {
              mq_util cntrl=new mq_util();
              try {
                   System.out.println("create");
                   cntrl.open("QM_cc","10.93.134.211",1414,"SYSTEM.ADMIN.SVRCONN","av","5tgbBGT%");
                   System.out.println("open");
              }
              catch (mqExcept m){
                    System.out.println(m);
                    try {cntrl.close();}catch (mqExcept m1){}
                    return;
              }
              try {
                  cntrl.clear("control");
              }
              catch (mqExcept m){
                   System.out.println(m);
              }

              try {
                   cntrl.close();
                   System.out.println("close");
              }
              catch (mqExcept m){
                   System.out.println(m);
              }

       }


}


