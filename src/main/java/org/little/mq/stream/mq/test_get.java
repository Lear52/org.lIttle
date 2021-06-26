package org.little.mq.stream.mq;

import org.little.mq.mqapi.def;
import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.util.Logger;
import org.little.util.run.tfork;

public class test_get extends tfork{
       final private static String CLASS_NAME="prj0.stream.mq.test_get";
       final private static int    CLASS_ID  =1702;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private int id;

       public test_get(){id=0;}
       public test_get(int i){id=i;}

       public void run() {
              mq_mngr_jms m=new mq_mngr_jms("SBPBACK_SNT_TU","10.70.112.150",2702,"SRV.TEST");
              mq_queue q_in=null;
              mq_msg   msg=new mq_msg();
              //mq_msg   _msg=null;
              try {
                      msg.setMaxLen();
                      m.open();
                      
                      q_in=m.openTruncQ("REPORT",1000);
                      long b_t=java.lang.System.currentTimeMillis();
                      int count=0;
                      System.out.println("**********************************");
                      //while(count<1000){
                      while(true){
                            long b=java.lang.System.currentTimeMillis();
                            msg=new mq_msg();
                            msg.setMaxLen(10000);
                            int ret = q_in.get(msg);
                            if(ret==def.RET_OK){
                               byte[] buf=null;
                               try{
                                   int len=msg.getLen();
                                   buf = new byte[len];
                                   msg.get(buf);
                                   long bb=java.lang.System.currentTimeMillis();

                                   tfork.delayMs(10);

                                   log.trace("id:"+id+" delay:"+(java.lang.System.currentTimeMillis()-bb));
                               } 
                               catch (mqExcept e1){
                                           /**/
                               }
                               m.commit();
                               log.trace("id:"+id+" i:"+count+" dt:"+(java.lang.System.currentTimeMillis()-b));
                               count++;
                            }
                            else break;
                      }
                      System.out.println("----------------------------------");
                      long e_t=java.lang.System.currentTimeMillis();
                      log.trace("id:"+id+"count:"+count+" time:"+(e_t-b_t)+" speed:"+count/((e_t-b_t)/1000));
                      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
              } 
              catch (mqExcept e1){
                     log.error("ex:"+e1);
              }
              finally{
                      try { m.closeQ(q_in);}catch (mqExcept ex3){}
                      try { m.close();     }catch (mqExcept ex4){}
                      stop();
              }
              
       }

       public static void main(String[] args) {
              test_get [] t=new test_get[100];
              int i;
              for(i=0;i<t.length;i++)  t[i]=new test_get(i+1);
              for(i=0;i<t.length;i++)  t[i].fork();

              tfork.delayMs(100);
              while(true){
                  for(i=0;i<t.length;i++)if(t[i].isRun())break;
                  tfork.delayMs(100);
              }
       }


}


