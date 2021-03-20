package org.little.mq.mqapi;

import  com.ibm.mq.MQMessage;
import  com.ibm.mq.constants.CMQC;


public class mq_msg {

       private MQMessage qm_msg;
       private int       max_len;
     

       public mq_msg() {
    	      clear();
    	      //setMaxLen();
       }
       public void clear() {
  	      qm_msg=new MQMessage();
              max_len=0;

       }
       public void setMaxLen() throws  mqExcept{
              setMaxLen(def.INIT_MSG_SIZE);
       }
       public void setMaxLen(int s) throws  mqExcept{
    	      try {
    	           qm_msg.resizeBuffer(s);
    	      } 
              catch (java.io.IOException ex) {
                    max_len=0;
                    throw  new mqExcept("msg.resizeBuffer:"+s,ex);
                    //System.out.println("msg.resizeBuffer:"+s+" ex:"+ex);
              }
              max_len=s;

       }
       public int getMaxLen(){return max_len;}

       public int getLen(){ 
              int len=0;
 	      try {             
                  len=qm_msg.getMessageLength();
	      } 
              catch (Exception ex) {
                 len=0;
              }

              return len;
       }
       public int getTotalLen(){
                  int len=qm_msg.getTotalMessageLength();
                  return len;
       }
       public void clearMsg() throws  mqExcept{
 	      try {             
 	          qm_msg.clearMessage();
	      } 
              catch (java.io.IOException ex) {
                 throw  new mqExcept("msg.getLen",ex);
              }
       }


       protected MQMessage getQ() {
		return qm_msg;
       }

       public String getMsgID() {
		return new String(qm_msg.messageId);
       }
       public int getMsgType() {
		return qm_msg.messageType;
       }

       public java.util.GregorianCalendar getDateTime() {
		return qm_msg.putDateTime;
       }

       public boolean isPersistence() {
		return qm_msg.persistence!=0;
       }
       public void setPersistence(boolean is_persistence) {
		if(is_persistence) qm_msg.persistence=CMQC.MQPER_PERSISTENT;/**/
		else               qm_msg.persistence=CMQC.MQPER_NOT_PERSISTENT;
       }
       public int getFeedback(){
              return qm_msg.feedback;
       }
       public String getReportQ() {
              return qm_msg.replyToQueueName;
       }
       public void setReportQ(String q) {
              qm_msg.replyToQueueName=q;
       }
       public String getReportQM() {
              return qm_msg.replyToQueueManagerName;
       }
       public void setReportQM(String qm) {
              qm_msg.replyToQueueManagerName=qm;
       }
       public void setReport(int r){
              qm_msg.report=r;
       }
       public int get(byte[] buf) throws  mqExcept{
 	      try {
                  qm_msg.readFully(buf);
	      } 
              catch (java.io.IOException ex) {
                 throw  new mqExcept("msg.get buffer",ex);
              }

       return buf.length;
       }
       public void put(String buf) throws  mqExcept{
 	      try {
                  qm_msg.writeString(buf);
	      } 
              catch (java.io.IOException ex) {
                 throw  new mqExcept("msg.put string length:"+buf.length(),ex);
              }
       }
       public void put(byte[] buf) throws  mqExcept{
 	      try {
                  qm_msg.write(buf);
	      } 
              catch (java.io.IOException ex) {
                 throw  new mqExcept("msg.put byte length:"+buf.length,ex);
              }
       }

}