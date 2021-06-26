package org.little.mq.stream;


import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_mngr_jms;
import org.little.mq.mqapi.mq_msg;
import org.little.mq.mqapi.mq_queue;
import org.little.mq.stream.config.cfg_stream;
import org.little.util.Logger;
import org.little.util.run.tfork;

public class Router{
       final private static String CLASS_NAME="org.little.stream.Router";
       final private static int    CLASS_ID  =1606;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       protected mq_mngr_jms     qm;
       private   mq_queue     q_cfg;
       private   Processor[]  proc;
       private   Command      cmd;
       private   int          flag_state;
       private   String       str_cfg;


       /**
         нечего просто так создавать класс делай потомков
       */
       protected Router() {
              clear();
       }
       /**
       */
       public void clear() {
              qm    =null;   
              cmd   =new Command();
              q_cfg =null;
              proc  =null;
              flag_state=def.CMD_INIT;
              str_cfg=null;
       }

       /**
       */
       private int init(String _qmname) {
              clear();
              qm=new mq_mngr_jms(_qmname);
              log.info("init router qm:"+_qmname+" bind local");
              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     close();
                     return def.RET_FATAL;
              }
              log.info("open manager:"+qm.getQMName());
              return init_cfg();
       }
       /**
       */
       private int init(String _qmname,String _host,int _port,String _channel) {
              clear();
              qm=new mq_mngr_jms(_qmname,_host,_port,_channel);
              log.info("init router qm:"+_qmname+" bind host:"+_host+" port:"+_port+" channel:"+_channel);
              try {
                  qm.open();
              }
              catch (mqExcept e) {
                     log.error("can`t open manager:"+qm.getQMName()+" ex:"+e);
                     close();
                     return def.RET_FATAL;
              }
              log.info("open manager:"+qm.getQMName());
              return init_cfg();
       }

       /**
       */
       private int init_cfg() {
               String qn="";
               if(qm==null)return def.RET_ERROR;
               try {
                 qn=def.CNFGQUEUE;
                 q_cfg=qm.openBrowseQ(qn,def.CFGWAIT);
                 if(q_cfg!=null)log.info("open qname:"+qn+" manager:"+q_cfg.getQMName());
                 else          {log.error("can't open qname:"+qn);return def.RET_ERROR;}
               } 
               catch (mqExcept e1){
                    log.error("can't open qname:"+qn+" error:"+e1.getError()+" ex:"+e1);
                    try {qm.close();} catch (mqExcept e){}
                    return -1;
               }
               log.info("run cfg router");

               cmd_start();

               return def.RET_OK;
       }
       /**
       */
       private int get_cfg() {
               if(q_cfg==null)return def.RET_ERROR;

               mq_msg m_cfg=new mq_msg();
               int ret;
               try {
                   m_cfg.setMaxLen();
                   ret=q_cfg.get(m_cfg);
               } 
               catch(mqExcept ex1){
                    log.error("get configuration"+" error:"+ex1.getError()+" ex:"+ex1);
                    try {qm.close();} catch (mqExcept e){}
                    return def.RET_ERROR;
               }
               if(ret==def.RET_WARN)return def.RET_WARN;
               if(ret==def.RET_ERROR || ret==def.RET_FATAL){
                  log.error("error get cfg msg");
                  return def.RET_FATAL;
               }

               log.info("get configuration");

               int len;
               len=m_cfg.getLen();
               byte[]  buf_cfg = new byte[len];
               try {
                   m_cfg.get(buf_cfg);
               } 
               catch(mqExcept ex3){
                    log.error("get buffer configuration ex:"+ex3);
                    try {qm.close();} catch (mqExcept e){}
                    return def.RET_ERROR;
               }
               str_cfg = new String(buf_cfg);
               log.info("get buffer configuration length:"+len);
               log.trace("\n"+str_cfg);

               return def.RET_OK;

       }
       private int start_proc() {

               if(str_cfg==null)return def.RET_ERROR;

               log.info("begin start INTERFACES");

               cfg_stream configuration = new cfg_stream();

               configuration.setConnection(qm);

               if(configuration.parse(str_cfg)<0)return def.RET_ERROR;

               log.info("parse configuration is ok");
              //-------------------------------------------
              int sz=configuration.size();
              if(sz<0){
                 str_cfg=null;
                 log.error("cannot find define INTERFACES");
                 return def.RET_WARN;
              } 

              log.info("find INTERFACES:"+sz);
              proc =new Processor [sz];
              for(int i=0;i<sz;i++){
                  proc[i]=new Processor(i+1);
                  int ret=proc[i].init(qm,configuration.getInterface(i));
                  if(ret<0){
                     cmd_close();
                     return def.RET_ERROR;
                  }
                  proc[i].fork();
                  log.info("fork INTERFACES:"+(proc[i].getID()));
              }
              log.info("start INTERFACES:"+sz+ " is ok");

              str_cfg=null;

              return def.RET_OK;
       }
       private void stop_proc(){
              if(proc == null) return;

              log.info("begin stop INTERFACES");

              for(int i=0;i<proc.length;i++){
                  proc[i].cmd_close();
              }
              boolean key=true;
              while(key){
                    key=false;
                    for(int i=0;i<proc.length;i++){
                        if(proc[i]==null) continue;
                        if(proc[i].isStop())proc[i]=null;
                        key=true;
                    }
                    tfork.delayMs(100);

              }
              proc = null;              
              log.info("end stop INTERFACES");
       }


       public String nameRouter(){return CLASS_NAME;}

       public int init(String[] args){
              int ret=0;
              if((args.length != 4) && (args.length != 1)) {
                  System.err.println("Use case 1: java "+nameRouter()+" QManager Hostname Port Channel");
                  System.err.println("Use case 2: java "+nameRouter()+" QManager");
                  return -1;
              }
              else
              if (args.length == 4) {
                  if ((args[0].length() == 0) || (args[1].length() == 0) || (args[2].length() == 0) || (args[3].length() == 0)) {
                     System.err.println((new java.util.Date()).toString() + " <> Empty parameter");
                     return def.RET_FATAL;
                  }
                  int port=0;
                  try{
                     port=new Integer(args[2]);
                  }
                  catch (Exception e) {
                     System.err.println((new java.util.Date()).toString() + " <> Bad port parameter");
                     return def.RET_FATAL;
                  }
                  ret=init(args[0],args[1],port,args[3]);
              } 
              else{
                  ret=init(args[0]);
              }

              return ret;
       }
       public void work() {
              boolean is_first_run=true;
              if(qm==null)return ;

              log.info("run router");
              //  стартуем обработчик команд
              if(cmd.start(qm)<0){
                 // обработчик команд не стартовал закрываемя
                 cmd_close();
              }
              else
              while(!isStop()){
                    // читаем конфики
                    int ret;
                    ret=get_cfg();

                    if(ret==def.RET_ERROR)break;
                    if(ret==def.RET_FATAL)break;
                    if(ret==def.RET_OK)continue;//проверим может есть еще конфиг
                    if(ret==def.RET_WARN && str_cfg==null){
                       //нет конфига и не было
                       if(is_first_run)log.info("no message with config");
                    }
                    if(ret==def.RET_WARN && str_cfg!=null){
                       //нашли последний конфиг
                       stop_proc(); //останавливаем если нужно
                       ret=start_proc();//запускаем с полученным конфигом
                       if(ret==def.RET_OK){is_first_run=true;continue;}//запустили хорошо с полученным конфигом
                       if(ret==def.RET_WARN)continue;//конфиг пустой ждем дальше
                       if(ret==def.RET_ERROR)break;
                       if(ret==def.RET_FATAL)break;
                    }
                    // кружимся пока не стоп
                    tfork.delayMs(def.CFGINTERVAL);
                    log.trace("wait router config");
              }

              close();
              //log.info("stop router");
       }
       private void close(){
               cmd_close();
               stop_proc();
               while(!isStop()){
                     log.trace("wait stop router");
                     tfork.delayMs(def.CFGINTERVAL);
               }
               try{if(q_cfg!=null)qm.closeQ(q_cfg);q_cfg=null;}catch (mqExcept e1){}
               try{if(qm   !=null)qm.close();      qm=null;}   catch (mqExcept e1){}
               log.info("stop router");
       }
       public void  cmd_start(){
              flag_state=def.CMD_RUN;
       }
       public void  cmd_close(){
              flag_state=def.CMD_STOP;
              if(proc!=null)for(int i=0;i<proc.length;i++)proc[i].cmd_close();
              cmd.cmd_close();
       }
       public boolean isStop() {
              boolean _is_stop;
                   _is_stop=(flag_state<1);
                   if(_is_stop)return true;
                   _is_stop=cmd.isStop();
                   if(_is_stop)return true;

                   _is_stop=false;
                   if(proc!=null)
                   for(int i=0;i<proc.length;i++){
                       if(proc[i].isStop()){
                          _is_stop=true;
                       }
                   }
                   if(_is_stop)return true;

             return false;
       }

       public static void main(String[] args) {
              Router r = null;
              r=new Router();
              if(r.init(args)<0)return;
              r.work();
       }


}