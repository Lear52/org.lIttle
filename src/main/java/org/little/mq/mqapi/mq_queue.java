package org.little.mq.mqapi;

import org.little.util.Logger;

import  com.ibm.mq.MQException;
import  com.ibm.mq.MQGetMessageOptions;
import  com.ibm.mq.MQPutMessageOptions;
import  com.ibm.mq.MQQueue;
import  com.ibm.mq.MQQueueManager;
import  com.ibm.mq.constants.MQConstants;

public class mq_queue {
       final private static String CLASS_NAME="prj0.stream.mq.mq_queue";
       final private static int    CLASS_ID  =1705;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       final private static int    READ   =1;
       final private static int    WRITE  =2;
       private   String                  qmname;
       private   String                  qname;
       private   mq_mngr                 parent;
       private   MQQueue                 q;
       private   MQGetMessageOptions     gmo;
       private   MQPutMessageOptions     pmo;
       private   int                     mode;
       private   int                     opt;
       private   int                     type;
       private   boolean                 is_first;

       public    void  clear(){
                 qmname=null;
                 qname =null; 
                 parent=null;
                 q     =null;     
                 gmo   =null;   
                 pmo   =null;   
                 type  =0;  
                 opt   =0;
                 mode  =0;  
                 
                 //wait  =-1;  
                 reset();
       }
       protected mq_queue(){
                 clear();
       }
       public String  getQMName(){return qmname;}
       public String  getQName (){return qname ;}


       protected void open(mq_mngr _parent,String _qmname,String _qname,int _mode,int _wait) throws  mqExcept{
              int open_mode=0;  

              parent=_parent;
              qmname=_qmname;
              qname =_qname; 
              switch(_mode){
              case def.QMODE_READ    :type=mq_queue.READ; 
                                      open_mode=def.QOPEN_READ;   
                                      opt=def.Q_OPT_READ;   
                                      if(_wait>0)opt=opt|MQConstants.MQGMO_WAIT;
                                      mode=_mode;
                                      break;
              case def.QMODE_READ_EX :type=mq_queue.READ; 
                                      open_mode=def.QOPEN_READ_EX;
                                      opt=def.Q_OPT_READ_EX;
                                      if(_wait>0)opt=opt|MQConstants.MQGMO_WAIT;
                                      mode=_mode;
                                      break;
              case def.QMODE_BROWSE  :type=mq_queue.READ; 
                                      open_mode=def.QOPEN_BROWSE; 
                                      opt=def.Q_OPT_BROWSE; 
                                      if(_wait>0)opt=opt|MQConstants.MQGMO_WAIT;
                                      mode=_mode;
                                      break;
              case def.QMODE_TRUNC   :type=mq_queue.READ; 
                                      open_mode=def.QOPEN_TRUNC;  
                                      opt=def.Q_OPT_TRUNC;  
                                     if(_wait>0)opt=opt|MQConstants.MQGMO_WAIT;
                                     mode=_mode;
                                     break;
              case def.QMODE_WRITE   :type=mq_queue.WRITE;
                                      open_mode=def.QOPEN_WRITE;  
                                      opt=def.Q_OPT_WRITE;  
                                      mode=_mode;
                                      break;
              case def.QMODE_WRITE_SIMPLE:
                                      type=mq_queue.WRITE;
                                      open_mode=def.QOPEN_WRITE;  
                                      opt=def.Q_OPT_WRITE_SIMPLE;  
                                      mode=_mode;
                                      break;
              default: throw new mqExcept("Error mode open queue");
              }
              gmo              =new MQGetMessageOptions();
              gmo.options      =opt;
              gmo.waitInterval = _wait;
              pmo              =new MQPutMessageOptions();
              pmo.options      =opt;

              try {
                   MQQueueManager  qm=parent.getQM();
                   if(qm==null){
                      mqExcept new_ex=new mqExcept("manager is'n open");
                      clear();
                      throw    new_ex;
                   }
                   if(qmname!=null && !qmname.equals(qm.getName())){
                      //System.out.println("q:"+qname+" m:"+ qmname);
                      q = qm.accessQueue(qname, open_mode,qmname,null,null);
                   }
                   else{
                      //System.out.println("q:"+qname+" m is local");
                      q = qm.accessQueue(qname, open_mode);
                   }
              } 
              catch (MQException e) {
                    mqExcept new_ex=new mqExcept("open queue:"+qname+" manager;"+qmname+" opt:"+def.getOptOO(open_mode)+ " reasonCode:"+e.reasonCode+" error:"+def.getRC(e.reasonCode),e);
                    clear();
                    throw    new_ex;
              }

              try {if(qmname==null)qmname=parent.getQM().getName();}catch (MQException e1) {}

              log.trace("open queue:"+qname+" manager;"+qmname+" opt:"+def.getOptOO(open_mode)+" optGMO:"+def.getOpt(opt));

       }
       protected void close() throws  mqExcept{
                 String _qm=qmname;
                 String _q=qname;
                 try{
                     q.close();
                     clear();
                 }
                 catch (MQException e){
                    clear();
                    throw  new mqExcept("close qname;"+_q+"manager:"+_qm + " reasonCode:"+e.reasonCode+" error:"+def.getRC(e.reasonCode),e);
                 }
              log.trace("close qname;"+_q+"manager:"+_qm);
       }

       public    void reset(){is_first=true;}


       public int get(mq_msg m)  throws  mqExcept{
                 if((type&mq_queue.READ)==0)return def.RET_FATAL;
                 if((mode&def.QMODE_BROWSE)!=0){
                     if(is_first)gmo.options=opt| MQConstants.MQGMO_BROWSE_FIRST;
                     else        gmo.options=opt| MQConstants.MQGMO_BROWSE_NEXT;
                     is_first=false;
                 }
                 else gmo.options=opt;

                 int max_len=m.getMaxLen();
                 try {
                     q.get(m.getQ(), gmo, max_len);
                 } 
                 catch (MQException e) {
                      String err=new String(
                               " reasonCode:"+e.reasonCode + " error:"     +def.getRC(e.reasonCode) 
                             + " max_len:"   +max_len
                             + " opt:"       +def.getOpt(gmo.options)
                             + " mode:"      +def.getMode(mode)
                             + " qname:"     +qname
                             + " manager:"   +qmname
                             );

                     if (e.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE)return def.RET_WARN; 
                     if (e.reasonCode == MQConstants.MQRC_TRUNCATED_MSG_ACCEPTED && mode==def.QMODE_TRUNC)return def.RET_WARN; 
                     if (e.reasonCode == MQConstants.MQRC_TRUNCATED_MSG_FAILED)return def.RET_ERROR; 
                     if (e.reasonCode == MQConstants.MQRC_BUFFER_LENGTH_ERROR )return def.RET_ERROR;

                     throw  new mqExcept("get "+err,e);
                 }

                 return def.RET_OK;
       }
       public int put(mq_msg m)  throws  mqExcept{
                 if((type&mq_queue.WRITE)==0)return def.RET_ERROR;
                 try {
                     q.put(m.getQ(), pmo);
                 } 
                 catch (MQException e) {
                      String err=new String(
                               " reasonCode:"+e.reasonCode 
                             + " error:"     +def.getRC(e.reasonCode) 
                             //+ " max_len:"   +max_len
                             + " opt:"       +def.getOpt(gmo.options)
                             + " qname:"     +qname
                             + " manager:"   +qmname
                             );
                     throw  new mqExcept("put "+err,e);
                 }
                 return def.RET_OK;
       }

      
      


}


  