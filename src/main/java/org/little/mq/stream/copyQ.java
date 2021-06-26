package org.little.mq.stream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.mq.stream.ufps.u_msg;
import org.little.mq.stream.ufps.ufpsParser;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.run.tfork;

import com.ibm.mq.MQException;


public class copyQ extends tfork{
       final private static String CLASS_NAME="org.little.stream.copyQ";
       final private static int    CLASS_ID  =1603;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       protected mq_mngr_jms      qm;
       private   mq_queue     q_in;
       private   mq_queue     q_out;
       private   int          flag_state;
       private   static int   flag_global_state=def.CMD_RUN;
       private   FileWriter   file_stat;

       public copyQ() {
              clear();
       }
       public void clear() {
              qm             =null;   
              q_in           =null;
              q_out          =null;
              flag_state     =def.CMD_INIT;
              MQException.log=null;
              file_stat      =null;
       }

       private int init(String _qmname,String _qname_in,String _qname_out,String filename) {
              clear();
              qm=new mq_mngr_jms(_qmname);

              log.info("init qm:"+_qmname+" queue_in:"+_qname_in+" queue_out:"+_qname_out+" bind local"+" file:"+filename);

              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     close();
                     return def.RET_FATAL;
              }

              log.info("open manager:"+qm.getQMName()+" ret:ok");

              int ret;

              try{
                  file_stat=new FileWriter(filename,true);
              } catch (IOException e) {log.error("open file:"+filename+" ex:"+e);}

              if(file_stat!=null)log.info("open file:"+filename+" ret:ok");

              ret=openIN(_qname_in);  if(ret!=def.RET_OK)return ret;
              ret=openOUT(_qname_out);if(ret!=def.RET_OK)return ret;
              cmd_start();
              return ret;
       }
       private int init(String _qmname,String _host,int _port,String _channel,String _qname_in,String _qname_out,String filename,String _user,String _passwd) {
               clear();
               qm=new mq_mngr_jms(_qmname,_host,_port,_channel);
               log.info("init router qm:"+_qmname+" queue_in:"+_qname_in+" queue_out:"+_qname_out+" bind host:"+_host+" port:"+_port+" channel:"+_channel+" file:"+filename);

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
               int ret;
              
               try{
                   file_stat=new FileWriter(filename,true);
               } catch (IOException e) {log.error("open file:"+filename+" ex:"+e);}
              
               if(file_stat!=null)log.info("open file:"+filename+" ret:ok");
              
               ret=openIN(_qname_in);  if(ret!=def.RET_OK)return ret;
               ret=openOUT(_qname_out);if(ret!=def.RET_OK)return ret;
              
               cmd_start();
               return ret;
       }
       private int  openIN(String qn){
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
       private int  openOUT(String qn){
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

               //cmd_start();

               return def.RET_OK;
       }
       private void close(){
               cmd_close();
               try{if(q_out!=null)qm.closeQ(q_out);q_out=null;}catch (mqExcept e1){q_out=null;}
               try{if(q_in !=null)qm.closeQ(q_in); q_in=null;} catch (mqExcept e1){q_in =null;}
               try{if(qm   !=null)qm.close();      qm=null;}   catch (mqExcept e1){qm   =null;}
               try{
               file_stat.flush();
               file_stat.close();
               } catch (IOException e) {}
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
       public  u_msg parse(mq_msg msg){
               byte[] buf=null;
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
               ufpsParser.is_debug  =false;

               try{
                   int ret=ufpsParser.parse(ufbs, input);
                   if(ret!=org.little.mq.stream.ufps.def.RET_OK)ufbs=null;
               } 
               catch (Except e1){
                   return null;
               }
               return ufbs;
       }
       public void print(u_msg ufbs,mq_msg m){
              if(ufbs==null)return;
              try {
                  String msg_id        =ufbs.getID();
                  String corl_id       =ufbs.getCorID();
                  String doc_type      =ufbs.getDocType();
                  //String msg_t         =m.getDateTime().toString();
                  SimpleDateFormat sfd =new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_S");
                  String _msg_t        =sfd.format(m.getDateTime().getTime());
                  String _perst        ="PERSISTENCE:"+m.isPersistence();
                  int    len           =m.getLen();    

                  if(msg_id  ==null)msg_id  ="NULL";
                  if(corl_id ==null)corl_id ="NULL";
                  if(doc_type==null)doc_type="NULL";
                  if(msg_id.length()  ==0)msg_id  ="NULL";
                  if(corl_id.length() ==0)corl_id ="NULL";
                  if(doc_type.length()==0)doc_type="NULL";

                  file_stat.write(java.lang.System.currentTimeMillis()+" "+msg_id+" "+corl_id+" "+doc_type+" "+len+" "+_msg_t+" "+_perst+"\n");
                  file_stat.flush();

              } catch (IOException e) {}

       }

       public void run() {

              if(qm==null)    {close();return ;}
              if(q_in==null)  {close();return ;}

              log.trace("run copyQ("+q_in.getQName()+") is_run:"+(!isStop()));

              while(!isStop()){
                    int ret;
                    mq_msg m=new mq_msg();
                    //-------------------------------------------------------------
                    try {
                        m.setMaxLen(100000);
                        ret = q_in.get(m);
                        if(ret==def.RET_OK){

                           log.trace("get msg processor("+q_in.getQName ()+") ");

                           u_msg ufbs=parse(m);
                           if(ufbs!=null){

                              ret = q_out.put(m);
                              if(ret==def.RET_OK){
                                 qm.commit();
                                 log.trace("put msg processor("+q_out.getQName ()+") ");
                                 print(ufbs,m);
                              }
                              else{
                                 qm.backout();
                                 log.trace("backout msg processor("+q_out.getQName ()+") ");
                              }

                           }
                        }
                    } 
                    catch (mqExcept ex1) {
                      log.error("run copyQ("+q_in.getQName()+" ex:"+ex1);
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
                               String qname_in =null;
                               String qname_out=null;
                               String qmname   =null;
                               String host     =null;
                               int    port     =0;
                               String channel  =null;
                               String file_stat=null;
                               String user=null;
                               String passwd=null;
                               s=parser_line.nextToken();
                               StringTokenizer parser_q=new StringTokenizer(s, " \t");
                               int ind=0;
                               while(parser_q.hasMoreTokens()) {
                                     String ss=parser_q.nextToken();
                                     switch(ind){
                                     case 0: qmname    =ss; break;  
                                     case 1: qname_in  =ss; host=ss;break;  
                                     case 2: qname_out =ss; if(ss!=null) try { port=Integer.parseInt(ss, 10);} catch (Exception e) {port=0;host=null;} break;  
                                     case 3: file_stat =ss; channel  =ss; break;  
                                     case 4: qname_in  =ss; break;  
                                     case 5: qname_out =ss; break;  
                                     case 6: file_stat =ss; break;  
                                     case 7: user      =ss; break;  
                                     case 8: passwd    =ss; break;  
                                     }
                                     ind++;
                               }
                               if(ind<6)channel=null;
                               if(qmname!=null){
                                  if(channel==null){
                                     log.info("stream:"+count+" qm:"+qmname+" q_in:"+qname_in+" q_out:"+qname_out+" fn:"+file_stat);
                                     copyQ r=new copyQ();
                                     if(r.init(qmname,qname_in,qname_out,file_stat)<0){
                                        log.error("stream:"+count+"error init");
                                     }
                                     else r.fork();
                                  }
                                  else{
                                     log.info("stream:"+count+" qm:"+qmname+" q_in:"+qname_in+" q_out:"+qname_out+" host:"+host+":"+port+" channel:"+channel+" fn:"+file_stat);
                                     copyQ r=new copyQ();
                                     if(r.init(qmname,host,port,channel,qname_in,qname_out,file_stat,user,passwd)<0){
                                        log.error("stream:"+count+"error init");
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
                 copyQ.work(config_filename);
              }

       }

}