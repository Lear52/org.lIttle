package org.little.mq.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.run.tfork;

import com.ibm.mq.MQException;


public class clearQ extends tfork{
       final private static String CLASS_NAME="org.little.stream.clearQ";
       final private static int    CLASS_ID  =1601;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       protected mq_mngr_jms      qm;
       private   mq_queue     q_in;
       private   int          flag_state;
       private   static int   flag_global_state=def.CMD_RUN;


       public clearQ() {
              clear();
       }
       public void clear() {
              qm    =null;   
              q_in  =null;
              flag_state=def.CMD_INIT;
              MQException.log=null;
       }

       private int init(String _qmname,String _qname) {
              clear();
              qm=new mq_mngr_jms(_qmname);
              log.info("init qm:"+_qmname+" queue:"+_qname+" bind local");
              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     close();
                     return def.RET_FATAL;
              }
              log.info("open manager:"+qm.getQMName());
              return openIN(_qname);
       }
       //private int init(String _qmname,String _host,int _port,String _channel,String _qname) {
       //        return init(_qmname,_host,_port,_channel,_qname,null,null);
      // }
       private int init(String _qmname,String _host,int _port,String _channel,String _qname,String _user,String _passwd) {
               clear();
               qm=new mq_mngr_jms(_qmname,_host,_port,_channel);
               log.info("init router qm:"+_qmname+" queue:"+_qname+" bind host:"+_host+" port:"+_port+" channel:"+_channel);
              
               if(_user    !=null)qm.setUser(_user);
               if(_passwd  !=null)qm.setPassword(_passwd);
               if(_user    !=null)log.info("set router qm:"+_qmname+" util:"+_user);
               if(_passwd  !=null)log.info("set router qm:"+_qmname+" passwd:"+_passwd);
              
               try {
                   qm.open();
               }
               catch (mqExcept e) {
                      log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                      close();
                      return def.RET_FATAL;
               }
               log.info("open manager:"+qm.getQMName());
               return openIN(_qname);
       }
       private int  openIN(String qn){
               if(qm==null)return def.RET_ERROR;
               try {
                 q_in=qm.openTruncQ(qn,def.CFGWAIT);
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

               cmd_start();

               return def.RET_OK;
       }
       private void close(){
               cmd_close();
               try{if(q_in !=null)qm.closeQ(q_in); q_in=null;} catch (mqExcept e1){}
               try{if(qm   !=null)qm.close();      qm=null;}   catch (mqExcept e1){}
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
       public void run() {

              if(qm==null)    {close();return ;}
              if(q_in==null)  {close();return ;}

              log.trace("run clearQ("+q_in.getQName()+") is_run:"+(!isStop()));

              while(!isStop()){
                    int ret;
                    mq_msg m=new mq_msg();
                    //-------------------------------------------------------------
                    try {
                        m.setMaxLen(0);
                        ret = q_in.get(m);
                        if(ret==def.RET_OK)log.trace("get msg processor("+q_in.getQName ()+") ");
                    } 
                    catch (mqExcept ex1) {
                      log.error("run clear("+q_in.getQName()+" ex:"+ex1);
                      /**/
                      cmd_close();
                      cmd_g_close();/**/

                      continue;
                    }
                    //if(ret!=def.RET_OK)break;
                    //-------------------------------------------------------------
              }
              //cmd_close();
              log.trace("stop processor("+q_in.getQName ()+") run:"+flag_state);
              close();

       }

       public static void work(String filename) {
              File            f;
              FileReader      f_r;
              BufferedReader  in;
              try {
                  f  =new File(filename);
                  f_r=new FileReader(f);
                  in =new BufferedReader(f_r,10240);
              }
              catch (Exception ex1){
                  log.error("open file:"+filename +" ex:"+Except.printException(ex1));
                  return;
              }
              int count=1;
              try{
                  String str;
                  StringTokenizer parser_line;
                  while((str = in.readLine()) != null) {
                         parser_line = new StringTokenizer(str, "\n\r");
                         //----------------------------------------------------------
                         while(parser_line.hasMoreTokens()) {
                               String s;
                               String qname   =null;
                               String qmname  =null;
                               String host    =null;
                               int    port    =0;
                               String channel=null;
                               String user=null;
                               String passwd=null;
                               s=parser_line.nextToken();
                               StringTokenizer parser_q=new StringTokenizer(s, " \t");
                               int ind=0;
                               while(parser_q.hasMoreTokens()) {
                                     String ss=parser_q.nextToken();
                                     switch(ind){
                                     case 0: qmname =ss; break;  
                                     case 1: qname  =ss; host=ss;break;  
                                     case 2: if(ss!=null) try { port=Integer.parseInt(ss, 10);} catch (Exception e) {port=0;host=null;} break;  
                                     case 3: channel=ss; break;  
                                     case 4: qname  =ss; break;  
                                     case 5: user   =ss; break;  
                                     case 6: passwd =ss; break;  
                                     }
                                     ind++;
                               }
                               if(qmname!=null){
                                  if(channel==null){
                                     log.info("init:"+count+" qm:"+qmname+" q:"+qname);
                                     clearQ r=new clearQ();
                                     if(r.init(qmname,qname)<0){
                                        log.error("init:"+count+" error init");
                                     }
                                     else r.fork();
                                  }
                                  else{
                                     log.info("init:"+count+" qm:"+qmname+" q:"+qname+" host:"+host+":"+port+" channel:"+channel);
                                     clearQ r=new clearQ();
                                     if(r.init(qmname,host,port,channel,qname,user,passwd)<0){
                                        log.error("init:"+count+" error init");
                                     }
                                     else r.fork();
                                  }
                                  count++;
                               }
                         }
                         //----------------------------------------------------------
                  }
                  in.close();
              } 
              catch (Exception ex2){
                  log.error("read file:"+filename +" ex:"+Except.printException(ex2));
              }
              try {in.close();} catch (IOException e1) {}
              try {f_r.close();} catch (IOException e1) {}

       }

       public static void main(String[] args) {


              if(args.length<1){
                 System.out.println("run java "+getClassName()+" config_filename");
              }
              else{
                 MQException.log=null;
                 String config_filename=args[0];
                 clearQ.work(config_filename);
              }

       }

}