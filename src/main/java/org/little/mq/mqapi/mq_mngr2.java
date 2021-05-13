package org.little.mq.mqapi;

import java.util.StringTokenizer;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class mq_mngr2 extends mq_mngr{
       private static final Logger log = LoggerFactory.getLogger(mq_mngr2.class);

       public  mq_mngr2(){}
      
       public mq_mngr2(String _qmname) {
              super(_qmname);
       }
       public mq_mngr2(String _qmname,String _host,int _port,String _channel) {
              super(_qmname,_host,_port,_channel);
       }


       public int initJMS(String _jms_string) {
              StringTokenizer          parser_str;
              clear();
              isLocal(true);
              parser_str = new StringTokenizer(_jms_string, "/");

              while(parser_str.hasMoreTokens()){
                    String field=parser_str.nextToken();          

                    StringTokenizer parser_field = new StringTokenizer(field, ":");

                    if(parser_field.hasMoreTokens()==false)continue;

                    field=parser_field.nextToken();

                    if("JmsChannel".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          String _channel=parser_field.nextToken();
                          setChannel(_channel);
                          isLocal(false);
                       }
                       else{
                           log.error("error parse channel:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("QM".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          String _queueManager=parser_field.nextToken();
                          setQMName(_queueManager);
                          //log.trace("QM:"+_queueManager);
                       }
                       else{
                           log.error("error parse qm:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Port".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){

                          String _port=parser_field.nextToken();
                          //log.trace("port:"+_port);
                          try { 
                               int port=Integer.parseInt(_port, 10);
                               setPort(port);
                          } 
                          catch (Exception e) {
                                log.error("error parse port:"+_port);
                                return def.RET_ERROR;
                          }
                          isLocal(false);
                          //client = true;
                       }
                       else{
                           log.error("error parse port:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Host".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          String _host=parser_field.nextToken();
                          setHost(_host);
                          isLocal(false);
                       }
                       else{
                           log.error("error parse host:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("User".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          String _user=parser_field.nextToken();
                          setUser(_user);
                       }
                       else{
                           log.error("error parse util:"+field);
                           return def.RET_ERROR;
                       }
                    }
                    if("Passwd".equalsIgnoreCase(field)){
                       if(parser_field.hasMoreTokens()){
                          String _password=parser_field.nextToken();
                          setPassword(_password);
                       }
                       else{
                           log.error("error parse passwd:"+field);
                           return def.RET_ERROR;
                       }
                    }
              }
              return def.RET_OK;
       }
       public static void main(String[] args) {
           mq_mngr2 m=new mq_mngr2();
           System.out.println("new mq_mngr");

           try {
                   //m.initJMS("JmsChannel:MONITOR/Port:2701/Host:10.70.112.150/QM:SBPFRONT_SNT_TU/User:av/Passwd:483886416409");
                   m.initJMS("JmsChannel:SVRCONN/Port:1414/Host:10.93.134.38/QM:QM_cc/User:av/Passwd:2wsxXSW@");
                   m.open();
                   System.out.println("mq_mngr.openIn mngr:"+m.getQM().getName()+" is_con:"+m.getQM().isConnected() );                      
                   m.close();
           } 
           catch (mqExcept e1){
               System.out.println("ex:"+e1);

          }
          catch (Exception e2){
               System.out.println("ex2:"+e2);

          }
           
    }


}


