package org.little.stream;
             
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.zeromq.jms.ZmqConnectionFactory;

public class senderUFPS2 {
       private static final Logger logger = LoggerFactory.getLogger(senderUFPS2.class);
       private String               destinationName;
       private Connection           connection;
       private Session              session;
       private Destination          destination;
       private MessageProducer      producer;
       private ZmqConnectionFactory cf1;
       
       public senderUFPS2() {
              clear();
       }

       public void clear() {
              destinationName=null;
              connection     =null;     
              session        =null;        
              destination    =null;    
              producer       =null;       
              cf1            =null;

       }
       
       public void open() {
              String QUEUE_URI = "jms:queue:queue_1?socket.addr=tcp://localhost:5555&redelivery.retry=3&acknowledge=true";

              String[] destinations=new String[] { QUEUE_URI };
              try {
                   cf1=new ZmqConnectionFactory(destinations); 
                   connection  = cf1.createConnection();
                   session     = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                   destination = session.createQueue(destinationName);
                   producer    = session.createProducer(destination);
                   connection.start();
              }
              catch (JMSException jmsex) {
                     logger.error("open sender ex:"+new Except("",jmsex));
              }
             
       }
       public void close(){
              if (producer != null) {try {producer.close();}catch (JMSException jmsex) {}  }
              //if (session != null) {try {session.close();}catch (JMSException jmsex) {}    }
              //if (connection != null) {try {connection.close();}catch (JMSException jmsex) {} }
       }
       
       public void run() {
              TextMessage message;
			try {
				message = session.createTextMessage("1111111111111111111111111111111111111111111111");
	              message.setBooleanProperty("",true);
	              producer.send(message);
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

              try{Thread.sleep(5000);}catch(Exception e){}
       }



}
