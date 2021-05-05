package org.little.zmq.zmq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.zeromq.jms.ZmqConnectionFactory;
public class client04 {


  public static void main(String[] args) {
    //String host = "localhost";
    //int    port = 1515;
    //String channel = "SYSTEM.DEF.SVRCONN";
    //String user = "nspk";
    //String password = "123";
    //String queueManagerName = "FROMTGATE";
    String destinationName = "queue_1";

    Connection      connection = null;
    Session         session = null;
    Destination     destination;
    MessageProducer producer;
    producer = null;

    try {
      // Create a connection factory
      //com.ibm.msg.client.jms.JmsFactoryFactory ff = com.ibm.msg.client.jms.JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);


      //com.ibm.msg.client.jms.JmsConnectionFactory cf = ff.createConnectionFactory();
      //String QUEUE_NAME = "queue_1";                                                                                   
      //String QUEUE_ADDR = "tcp://*:5555";                                                                              
      //String QUEUE_URI = "jms:queue:queue_1?gateway.addr=tcp://*:5555&redelivery.retry=0&event=stomp";
      //String QUEUE_URI = "jms:queue:queue_1?gateway.addr=tcp://*:5555&gateway.bind=true&gateway.type=";
      String QUEUE_URI = "jms:queue:queue_1?socket.addr=tcp://localhost:5555&redelivery.retry=3&acknowledge=true";
      //String QUEUE_URI = "jms:queue:queue_1?gateway.addr=tcp://localhost:5555&redelivery.retry=3&acknowledge=true";

      String[] destinations=new String[] { QUEUE_URI };
      System.out.println("----------- START -----------------");
      ZmqConnectionFactory cf1=new ZmqConnectionFactory(destinations); 
      System.out.println("----------- ZmqConnectionFactory -----------------");
      // Set the properties
      /*
      cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
      cf.setIntProperty(WMQConstants.WMQ_PORT, port);
      cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
      cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
      cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManagerName);
      if (user != null) {
          cf.setStringProperty(WMQConstants.USERID, user);
          cf.setStringProperty(WMQConstants.PASSWORD, password);
          cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
      }
      */
      // Create JMS objects
      connection  = cf1.createConnection();
      System.out.println("----------- createConnection -----------------:"+connection);
      //connection  = cf1.createConnection(final String userName, final String password);

      //session     = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      session     = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

      System.out.println("----------- createSession -----------------:"+session);

      destination = session.createQueue(destinationName);

      System.out.println("----------- createQueue("+destinationName+") -----------------:"+destination);
      /*
      ((JmsDestination) destination).setBooleanProperty(WMQConstants.WMQ_MQMD_WRITE_ENABLED, true);
      */
      producer    = session.createProducer(destination);
      System.out.println("----------- createProducer("+destinationName+") -----------------:"+producer);

      connection.start();
      System.out.println("----------- connection.start() -----------------:");

      TextMessage message = session.createTextMessage("1111111111111111111111111111111111111111111111");
      message.setBooleanProperty("",true);
      System.out.println("----------- createTextMessage -----------------:");

      producer.send(message);
      System.out.println("----------- producer.send("+message+") -----------------:");
      producer.send(message);
      System.out.println("----------- producer.send("+message+") -----------------:");

            try{
                Thread.sleep(5000);
            }
            catch(Exception e){}


    }
    catch (JMSException jmsex) {
      System.out.println("ex:"+jmsex);
      System.exit(0);
    }
    finally {
      System.out.println("----------- CLOSE BEGIN -----------------");
      if (producer != null) {try {producer.close();}catch (JMSException jmsex) {}  }
      System.out.println("----------- CLOSE PRODUCER -----------------");
      //if (session != null) {try {session.close();}catch (JMSException jmsex) {}    }
      //System.out.println("----------- CLOSE SESSION -----------------");
      //if (connection != null) {try {connection.close();}catch (JMSException jmsex) {} }
      //System.out.println("----------- CLOSE CONNECTION -----------------");
      System.out.println("----------- CLOSE END -----------------");
    }

    System.out.println("----------- END -----------------:");
    System.exit(0);

  } // end main()


} // end class

