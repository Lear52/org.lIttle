package org.little.mq.test.potok;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_msg;
import org.little.util.Except;
import org.little.util.Logger;

import com.ibm.mq.MQException;


public class copyMsg extends potokMsg{
       final private static String CLASS_NAME="prj0.potok.copyMsg";
       final private static int    CLASS_ID  =2401;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       private   FileWriter   file_stat;

       public copyMsg() {
              clear();
       }
       public void clear() {
              super.clear();
              file_stat      =null;
       }

       private int init(String _qmname,String _qname_in,String _qname_out,String filename) {

              clear();

              try{
                  file_stat=new FileWriter(filename,true);
              } catch (IOException e) {log.error("open file:"+filename+" ex:"+e);}

              if(file_stat!=null)log.info("open file:"+filename+" ret:ok");

              int ret=init(_qmname,_qname_in,_qname_out);

              cmd_start();
              return ret;
       }
       private int init(String _qmname,String _host,int _port,String _channel,String _qname_in,String _qname_out,String filename,String _user,String _passwd) {
               clear();
              
               try{
                   file_stat=new FileWriter(filename,true);
               } catch (IOException e) {log.error("open file:"+filename+" ex:"+e);}
              
               if(file_stat!=null)log.info("open file:"+filename+" ret:ok");
               int ret=init(_qmname,_host,_port,_channel,_qname_in,_qname_out,_user,_passwd);
               cmd_start();
               return ret;
       }
       protected void close(){
               super.close();
               try{
               file_stat.flush();
               file_stat.close();
               } catch (IOException e) {}
       }

       public void run() {

              if(qm==null)    {close();return ;}
              if(q_in==null)  {close();return ;}

              log.trace("run copyMsg("+q_in.getQName()+") is_run:"+(!isStop()));

              while(!isStop()){
                    int ret;
                    mq_msg m=new mq_msg();
                    //-------------------------------------------------------------
                    try {
                        m.setMaxLen(100000);
                        ret = q_in.get(m);
                        if(ret==def.RET_OK){

                              //log.trace("get msg processor("+q_in.getQName ()+") ");
                              ret = q_out.put(m);
                              if(ret==def.RET_OK){
                                 qm.commit();
                              }
                              else{
                                 qm.backout();
                                 log.trace("backout msg processor("+q_out.getQName ()+") ");
                              }
                        }
                    } 
                    catch (mqExcept ex1) {
                      log.error("run copyMsg("+q_in.getQName()+" ex:"+ex1);
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
                                     log.info("potok:"+count+" qm:"+qmname+" q_in:"+qname_in+" q_out:"+qname_out+" fn:"+file_stat);
                                     copyMsg r=new copyMsg();
                                     if(r.init(qmname,qname_in,qname_out,file_stat)<0){
                                        log.error("potok:"+count+"error init");
                                     }
                                     else r.fork();
                                  }
                                  else{
                                     log.info("potok:"+count+" qm:"+qmname+" q_in:"+qname_in+" q_out:"+qname_out+" host:"+host+":"+port+" channel:"+channel+" fn:"+file_stat);
                                     copyMsg r=new copyMsg();
                                     if(r.init(qmname,host,port,channel,qname_in,qname_out,file_stat,user,passwd)<0){
                                        log.error("potok:"+count+"error init");
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
                 copyMsg.work(config_filename);
              }

       }

}