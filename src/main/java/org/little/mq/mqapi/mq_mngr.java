package org.little.mq.mqapi;

//import com.ibm.mq.MQC;
import java.util.ArrayList;
import java.util.Properties;

import org.little.util.Logger;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;

public class mq_mngr extends cfg_conn{
       final private static String CLASS_NAME="org.little.statmq.mq.mq_mngr";
       final private static int    CLASS_ID  =1702;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       ArrayList<mq_queue>     list;

       private MQQueueManager  qm;
       private Properties      properties;
       private boolean         isUserIDPassword=false;
       private boolean         isTrace=true;//false

       public  mq_mngr(){clear();}
      
       public mq_mngr(String _qmname) {
              clear();
              setQMName(_qmname);
              isLocal(true);
       }
       public mq_mngr(String _qmname,String _host,int _port,String _channel) {
              clear();
              setQMName (_qmname);
              setHost   (_host   );
              setPort   (_port   );
              setChannel(_channel);
              isLocal(false);
       }
       public synchronized void clear() {
              super.clear();
              qm        =null;
              properties=new Properties();
              list      =new ArrayList<mq_queue>(256);
       }

       protected MQQueueManager getQM(){return qm;}
      
       public void   setUser    (String u){super.setUser(u);     isUserIDPassword=true;}
       public void   setPassword(String p){super.setPassword(p); isUserIDPassword=true;}

       public synchronized void open() throws  mqExcept{
              if(qm!=null)return ;

              MQException.log=null;/**/
              com.ibm.msg.client.commonservices.trace.Trace.isOn=isTrace;

             //properties.put(com.ibm.mq.MQC.MQ_QMGR_ASSOCIATION_PROPERTY,com.ibm.mq.MQC.ASSOCIATE_NONE);//"QMgr_Association"
             //properties.put(MQ_QMGR_ASSOCIATION_PROPERTY,ASSOCIATE_NONE);//"QMgr_Association"
             //properties.put(MQC.MQ_QMGR_ASSOCIATION_PROPERTY,MQC.ASSOCIATE_NONE);//define

              if (isUserIDPassword){   
                  if(getUser()    !=null){
                     properties.put(CMQC.USER_ID_PROPERTY,getUser());
                     properties.put(CMQC.USE_MQCSP_AUTHENTICATION_PROPERTY,false);/**/
                  }
                  if(getPassword()!=null){
                     properties.put(CMQC.PASSWORD_PROPERTY,getPassword());
                     properties.put(CMQC.USE_MQCSP_AUTHENTICATION_PROPERTY,false);/**/
                  }
              }

              if(!isLocal()){
                 properties.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);/**/
                 properties.put(CMQC.HOST_NAME_PROPERTY, getHost());//"hostname"
                 properties.put(CMQC.PORT_PROPERTY     , getPort());//"port"
                 properties.put(CMQC.CHANNEL_PROPERTY  , getChannel());//"channel"
              }

              try {
                  if(isLocal())qm = new com.ibm.mq.MQQueueManager(getQMName(), MQConstants.MQCNO_STANDARD_BINDING);
                  else         qm = new com.ibm.mq.MQQueueManager(getQMName(), properties);
              } 
              catch (MQException e) {
                    mqExcept me=new mqExcept("open manager:"+getQMName(),e);
                    log.trace("open manager:"+getQMName()+" ex:"+me);
                    throw me;
              }

              log.trace("open manager:"+getQMName());
              
       }
      
       public synchronized void close() throws  mqExcept{
              if(qm==null)return ;
              for(int i=0;i<list.size();i++){
                  mq_queue q=list.get(i);
                  try {
                       q.close();
                  } 
                  catch (mqExcept e1){}
              }

              try {
                   qm.disconnect();
              } 
              catch (MQException e) {
                    throw new mqExcept("close manager:"+getQMName(),e);
              }
              finally{
                clear();
              }

       }
      
       public synchronized mq_queue openReadQ(String _qname)  throws  mqExcept{
              return openReadQ(_qname,def.DEFAULT_TIMEOUT);
       }
       public synchronized mq_queue openReadQ(String _qname,int _wait)  throws  mqExcept{
              if(qm==null)return null;
              mq_queue q=new mq_queue();
              q.open(this,null,_qname,def.QMODE_READ,_wait);
              return q;
       }
       public synchronized mq_queue openExclReadQ(String qname)  throws  mqExcept{
              return openExclReadQ(qname,def.DEFAULT_TIMEOUT);
       }
       public synchronized mq_queue openExclReadQ(String qname,int wait)  throws  mqExcept{
              if(qm==null)return null;
              mq_queue q=new mq_queue();
              q.open(this,null,qname,def.QMODE_READ_EX,wait);
              return q;
       }
       public synchronized mq_queue openBrowseQ(String qname)  throws  mqExcept{
              return openBrowseQ(qname,def.DEFAULT_TIMEOUT);
       }
       public synchronized mq_queue openBrowseQ(String qname,int wait)  throws  mqExcept{
              if(qm==null)return null;
              mq_queue q=new mq_queue();
              q.open(this,null,qname,def.QMODE_BROWSE,wait);
              return q;
       }
       public synchronized mq_queue openTruncQ(String qname)  throws  mqExcept{
              return openTruncQ(qname,def.DEFAULT_TIMEOUT);
       }
       public synchronized mq_queue openTruncQ(String qname,int wait)  throws  mqExcept{
              if(qm==null)return null;
              mq_queue q=new mq_queue();
              q.open(this,null,qname,def.QMODE_TRUNC,wait);
              return q;
       }
       public synchronized mq_queue openWriteQ(String qname)  throws  mqExcept{
              if(qm==null)return null;
              mq_queue q=new mq_queue();
              q.open(this,null,qname,def.QMODE_WRITE,0);
              return q;
       }
       public synchronized mq_queue openWriteSQ(String qname)  throws  mqExcept{
              if(qm==null)return null;
              mq_queue q=new mq_queue();
              q.open(this,null,qname,def.QMODE_WRITE_SIMPLE,0);
              return q;
       }
       public synchronized mq_queue openWriteQ(String _qname,String _qmname)  throws  mqExcept{
              if(qm==null)return null;
              mq_queue q=new mq_queue();
              q.open(this,_qmname,_qname,def.QMODE_WRITE,0);
              return q;
       }

       public synchronized void closeQ(mq_queue q)  throws  mqExcept{
              if(qm==null)return;
              for(int i=0;i<list.size();i++){
                  mq_queue cq=list.get(i);
                  if(cq==q){
                     list.remove(i);
                     q.close();
                  }

              }
       
       }      
       public synchronized void begin()  throws  mqExcept{
              if(qm==null)return;
              try {
                  qm.begin();
              } 
              catch (MQException e){
                  throw new mqExcept("begin transaction on manager:"+getQMName(),e);
              }

       }
       public synchronized void commit()  throws  mqExcept{
              if(qm==null)return;
              try {
                  qm.commit();
              } 
              catch (MQException e){
                  throw new mqExcept("commit manager:"+getQMName(),e);
              }
       }

       public synchronized void backout()  throws  mqExcept{
              if(qm==null)return;
              try {
                  qm.backout();
              } 
              catch (MQException e){
                  throw new mqExcept("backout manager:"+getQMName(),e);
              }
       }


      
       public static void main2(String[] args) {
              //mq_mngr m=new mq_mngr("KCOI_22","10.70.116.130",1414,"SYSTEM.DEF.SVRCONN");
              mq_mngr m=new mq_mngr("SBPFRONT_SIT","172.16.37.83",1616,"NSPK.SVRCONN");
              //mq_mngr m=new mq_mngr(null,"10.70.116.130",1414,"SYSTEM.DEF.SVRCONN");
              System.out.println("new mq_mngr");
              mq_queue q_in =null;
              //mq_queue q_out=null;
              mq_msg   msg=new mq_msg();
              //mq_msg   _msg=null;
              try {
                      msg.setMaxLen();
                      m.setUser("nspksbp");    
                      m.setPassword("SBP!@#45qwert");                      
                      m.open();
                      System.out.println("mq_mngr.openIn mngr:"+m.getQM().getName()+" is_con:"+m.getQM().isConnected() );                      
                      
                      //q_in =m.openReadQ("IN",1000);
                      q_in =m.openReadQ("IN1",1000);
                      m.closeQ(q_in);
                      System.out.println("mq_mngr.read mngr:"+m.getQM().getName()+" is_con:"+m.getQM().isConnected());                      
                      /*
                      q_out=m.openWriteQ("IN","KCOI_22");
                      System.out.println("mq_mngr.write1 mngr:"+m.getQM().getName()+" is_con:"+m.getQM().isConnected());                      
                      m.closeQ(q_out);

                      q_out=m.openWriteQ("UTP","QM_cc");
                      System.out.println("mq_mngr.write2 mngr:"+m.getQM().getName()+" is_con:"+m.getQM().isConnected());                      
                      m.closeQ(q_out);
                      */
                  m.close();
              } 
              catch (mqExcept e1){
                  System.out.println("ex:"+e1);

             }
              catch (Exception e2){
                  System.out.println("ex2:"+e2);

             }
              
       }
       public static void main(String[] args) {
              //mq_mngr m=new mq_mngr("KCOI_22","10.70.116.130",1414,"SYSTEM.DEF.SVRCONF");
              //mq_mngr m=new mq_mngr("QM","10.93.128.11",1414,"CLN");
              mq_mngr  m=new mq_mngr("QM");
              mq_queue q_in =null;
              mq_queue q_out=null;
              mq_msg   msg=new mq_msg();
              //mq_msg   _msg=null;
              try {
                      msg.setMaxLen();
                      m.open();
                      
                      //q=m.openBrowseQ("IN",1000);
                      q_out=m.openWriteQ("TO","QM");
                      q_in =m.openReadQ("IN",1000);
                      //q=m.openReadQ("IN");
                      int count=0;
                      System.out.println("**********************************");
                      while(count<1000){
                            msg=new mq_msg();
                            msg.setMaxLen();
                           int ret;
                           ret=q_in.get(msg);
                           if(ret==def.RET_OK){
                              byte[]  buf=null;
                              int len=msg.getLen();
                               buf = new byte[len];
                               msg.get(buf);
                               System.out.println("m:"+new String(buf));
                               q_out.put(msg);
                               m.commit();
                           }
                           else if(ret==def.RET_WARN)System.out.println("no msg");
                           else {System.out.println("error msg");break;}
                         count++;
                      }
                      System.out.println("----------------------------------");
                      m.closeQ(q_in);
                      m.closeQ(q_out);
                     
                  m.close();
              } 
              catch (mqExcept e1){
                  System.out.println("ex:"+e1);

             }
              
       }


}


