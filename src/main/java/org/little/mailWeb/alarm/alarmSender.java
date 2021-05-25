package org.little.mailWeb.alarm;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.little.lmsg.lMessage;
import org.little.mailWeb.commonArh;
import org.little.mailWeb.folder.folderARH;
import org.little.mq.mqapi.mqExcept;
import org.little.mq.mqapi.mq_util;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.run.task;

public class alarmSender extends task{
       private static Logger logger = LoggerFactory.getLogger(alarmSender.class);
       private commonArh all_cfg;
       private commonAlarm cfg;

       public alarmSender(){
              all_cfg=null;
              cfg=null;
       }
       public void setCfg(commonArh _cfg) {all_cfg=_cfg;cfg=all_cfg.getAlarm();}

       @Override
       public void work() {
              logger.info("Start Servers");
              folderARH folder = all_cfg.getFolder();
              Timestamp alarm=cfg.getTimeAlarm();

              ArrayList<lMessage> list = folder.loadArrayAlarm(alarm,new Timestamp(new Date().getTime()));

              for(int i=0;i<list.size();i++) {
            	  lMessage msg=list.get(i);
                  send(msg,alarm);
            	  folder.setSend(msg.getX509ID());
              }
              
              logger.info("End Server");
       }
       private void send(lMessage msg,Date alarm){
              logger.info("send() queue:"+cfg.getNameQ());
              String user=msg.getX509Subject();
              //String user=msg.getX509Issuer();
              String uic="";              //CN=TSH-Dionis-TU, OU=4511111111, L=KSTSHKBR, O=CBR-UOS, ST=45, DC=ru
              //---------------------------------------------------------------------------------------------------------
              String[] fld=user.split(", ");
              for(int i=0;i<fld.length;i++) {
            	  String[] f=user.split("=");
            	  if("OU".equalsIgnoreCase(f[0])) {uic=f[1];break;}
              }
              //---------------------------------------------------------------------------------------------------------
              Date   expire=msg._getX509EndDate();
              String str_expire=(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(expire));
              String str_current=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());
              String str_alarm=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(alarm);
              String str_id=UUID.randomUUID().toString().replace("-","");
              //---------------------------------------------------------------------------------------------------------
              String txt = "у пользовател€ "+user+" истекает врем€ действи€ сертификата в "+ str_expire+", необходимо помен€ть";
              String file="";
              try {file=Base64.getEncoder().encodeToString(txt.getBytes("UTF-8"));} catch (Exception e){}
              //---------------------------------------------------------------------------------------------------------
              
              String xml = MSG.replace("[[id]]", str_id)
                      .replace("[[date]]",str_current)
                      .replace("[[uic]]",uic)
                      .replace("[[expire]]",str_expire)
                      .replace("[[file]]", file);

              //---------------------------------------------------------------------------------------------------------

              logger.info("alarm user:"+user+" uic:"+uic+" expire:"+str_expire+" current:"+str_current+" alarm:"+str_alarm+" id:"+str_id);
              logger.trace("alarm msg:"+xml);

              if(cfg.isSendAlarm()) {
                 mq_util cntrl=new mq_util();
                 try {
                      cntrl.open(cfg.getNameMngr(),cfg.getHost(),cfg.getPort(),cfg.getChannel(),cfg.getUser(),cfg.getPasswd());
                      cntrl.putMsg(cfg.getNameQ(),xml);
                      cntrl.close();
                 }
                 catch (mqExcept m){
                       try {cntrl.close();}catch (mqExcept m1){}
                 }
              }
              logger.info("send () queue:"+cfg.getNameQ());

       }

       public static void main(String[] args){
              alarmSender server=new alarmSender();
              server.work();
       }
       private final String MSG=""
                               +"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                               +"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">"
                               +"<soapenv:Header>"
                               +"    <props:MessageInfo xmlns:props=\"urn:cbr-ru:msg:props:v1.3\">"
                               +"        <props:To>[[uic]]</props:To>"
                               +"        <props:From>uic:KBRGATE</props:From>"
                               +"        <props:MessageID>[[id]]</props:MessageID>"
                               +"        <props:MessageType>3</props:MessageType>"
                               +"        <props:Priority>5</props:Priority>"
                               +"        <props:CreateTime>[[date]]</props:CreateTime>"
                               +"        <props:SendTime>[[date]]</props:SendTime>"
                               +"    </props:MessageInfo>"
                               +"    <props:AcknowledgementInfo xmlns:props=\"urn:cbr-ru:msg:props:v1.3\">"
                               +"        <props:AcknowledgementType>2</props:AcknowledgementType>"
                               +"        <props:ResultCode>0000</props:ResultCode>"
                               +"        <props:ResultText>у пользовател€ \"[[user]]\" истекает врем€ действи€ парол€ в \"[[expire]]\", необходимо помен€ть</props:ResultText>"
                               +"    </props:AcknowledgementInfo>"
                               +"</soapenv:Header>"
                               +"<soapenv:Body/>"
                               +"</soapenv:Envelope>"
                               ;

}
