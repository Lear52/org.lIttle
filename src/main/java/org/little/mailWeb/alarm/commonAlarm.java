package org.little.mailWeb.alarm;
             
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.little.mq.mqapi.commonMQ;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class commonAlarm extends commonMQ{
       private static final Logger logger = LoggerFactory.getLogger(commonAlarm.class);
       private int     alarm;
       private boolean send_alarm;

       public commonAlarm() {
              clear();
              logger.info("create commonX509DB");
       }

       public void clear() {
              super.clear();
              alarm=5;
              send_alarm=false;
       }

       
       public boolean isSendAlarm() {return send_alarm;}
       public Timestamp getTimeAlarm() {
    	      Date date_alarm;
    	      Calendar cal = Calendar.getInstance();
    	      cal.setTime(new Date());
    	      cal.add(Calendar.DAY_OF_MONTH, -alarm);   
    	      date_alarm=cal.getTime();
    	      return new Timestamp(date_alarm.getTime());
       }
       
       

       public void  init(Node _node_cfg) {
              super.init(_node_cfg);

              if(_node_cfg!=null){
                 logger.info("The configuration node:"+_node_cfg.getNodeName());


                 NodeList glist=_node_cfg.getChildNodes();     
                 for(int i=0;i<glist.getLength();i++){
                     Node n=glist.item(i);
                     if("alarm"     .equalsIgnoreCase(n.getNodeName())){String s=n.getTextContent();try{alarm=Integer.parseInt(s,10);}catch(Exception e){logger.error("error set alarm:"+s);alarm=5;}logger.info("alarm:"+alarm        );}
                     else
                     if("send_alarm".equalsIgnoreCase(n.getNodeName())){String s=n.getTextContent();try{send_alarm=Boolean.parseBoolean(s);}catch(Exception e){logger.error("error set send_alarm:"+s);send_alarm=false;}logger.info("send_alarm:"+send_alarm);}

                 }
              }
              else{
                  logger.error("The configuration node:null");
              }                 



       }


}

