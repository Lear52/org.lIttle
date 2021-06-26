package org.little.mq.stream.mq;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.util.Logger;
import org.little.util.run.tfork;

public class test_put extends tfork{
       final private static String CLASS_NAME="prj0.stream.mq.test_put";
       final private static int    CLASS_ID  =1702;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       //private int id;

       public test_put(){
    	   //id=0;
       }

       public void run() {
              mq_mngr_jms m=new mq_mngr_jms("SBPBACK_SNT_TU","10.70.112.150",2702,"SRV.TEST");
              mq_queue q_out=null;
              mq_msg   msg=new mq_msg();
              //mq_msg   _msg=null;
              byte [] buf = new byte[4096];
              for(int i=0;i<buf.length;i++)buf[i]=(byte)(65+i%10);
              try {
                      msg.setMaxLen();
                      m.open();
                      
                      q_out=m.openWriteQ("OUT_1");
                      long b_t=java.lang.System.currentTimeMillis();
                      int count=0;
                      System.out.println("**********************************");
                      while(count<1000){
                            long b=java.lang.System.currentTimeMillis();
                            msg=new mq_msg();
                            //msg.setReportQ("REPORT");
                            //msg.setReportQM("SBPBACK_SNT_TU");
                            //msg.setReport(0x700);
                            msg.setMaxLen(4096);
                            msg.put(buf);
                            q_out.put(msg);
                            m.commit();
                            log.trace("i:"+count+" dt:"+(java.lang.System.currentTimeMillis()-b));
                            count++;
                      }
                      System.out.println("----------------------------------");
                      long e_t=java.lang.System.currentTimeMillis();
                      log.trace("count:"+count+" time:"+(e_t-b_t)+" speed:"+count/((e_t-b_t)/1000));
                      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

              } 
              catch (mqExcept e1){
                      log.error("ex:"+e1);
              }
              finally{
                      try { m.closeQ(q_out);}catch (mqExcept ex3){}
                      try { m.close();     }catch (mqExcept ex4){}
                      stop();
              }
              
       }

       public static void main(String[] args) {
              test_put t1=new test_put();
              test_put t2=new test_put();
              test_put t3=new test_put();
              test_put t4=new test_put();
              test_put t5=new test_put();
              //t.run();
              t1.fork();
              t2.fork();
              t3.fork();
              t4.fork();
              t5.fork();
              tfork.delayMs(100);
              while(t1.isRun()||t2.isRun()||t3.isRun()||t4.isRun()||t5.isRun())tfork.delayMs(100);
       }


}


