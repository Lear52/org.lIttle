package org.little.zmq.zmq;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zeromq.jms.ZmqConnectionFactory;
import org.zeromq.jms.ZmqQueue;
import org.zeromq.jms.ZmqTextMessageBuilder;
/**
 * Unit tests for Zero MQ JMS Queue behaviour.
 */
public class client03 {

    private static final String QUEUE_NAME = "queue_1";
    private static final String QUEUE_ADDR = "tcp://*:9727";
    private static final String QUEUE_URI = "jms:queue:" + QUEUE_NAME + "?socket.addr=" + QUEUE_ADDR + "&event=stomp";

    private static final String MESSAGE_1 = "this is the text message 1";
    private static final String MESSAGE_2 = "this is the text message 2";
    private static final String MESSAGE_3 = "this is the text message 3";

    private static InitialContext context;

    public static void setup() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

        context = new InitialContext();

        context.createSubcontext("java:");
        context.createSubcontext("java:/comp");
        context.createSubcontext("java:/comp/env");
        context.createSubcontext("java:/comp/env/jms");

        context.bind("java:/comp/env/jms/queueConnectionFactory", new ZmqConnectionFactory(new String[] { QUEUE_URI }));
        context.bind("java:/comp/env/jms/queueTest", new ZmqQueue(QUEUE_NAME));
    }

    public static void pulldown() throws NamingException {

        context = new InitialContext();

        context.destroySubcontext("java:");
        context.close();
    }

    static public void testSendAndReceiveMessageWithoutTransaction() {

        try {
            final QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("java:/comp/env/jms/queueConnectionFactory");
            final QueueConnection connection = factory.createQueueConnection();
            final QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            final Queue queue = (Queue) context.lookup("java:/comp/env/jms/queueTest");

            QueueSender sender = null;
            QueueReceiver receiver = null;

            try {
                sender = session.createSender(queue);
                receiver = session.createReceiver(queue);

                try {
                    sender.send(ZmqTextMessageBuilder.create().appendText(MESSAGE_1).toMessage());
                    sender.send(ZmqTextMessageBuilder.create().appendText(MESSAGE_2).toMessage());
                    sender.send(ZmqTextMessageBuilder.create().appendText(MESSAGE_3).toMessage());
                } catch (Exception ex) {
                    throw ex;
                }

                try {
                    TextMessage message1 = (TextMessage) receiver.receive(1000);

                    Thread.sleep(1000);

                    TextMessage message2 = (TextMessage) receiver.receive();
                    TextMessage message3 = (TextMessage) receiver.receiveNoWait();

                    // sometime too quick to pickup since a transaction, so try again with a wait.
                    if (message3 == null) {
                        message3 = (TextMessage) receiver.receive(1000);
                    }

                    System.out.println(MESSAGE_1+" = "+ message1.getText());
                    System.out.println(MESSAGE_2+" = "+ message2.getText());
                    System.out.println(MESSAGE_3+" = "+ message3.getText());
                } catch (Exception ex) {
                    throw ex;
                }
            } finally {
                session.close();
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static public void testSendAndReceiveMessageWithTransaction() {

        try {
            final QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("java:/comp/env/jms/queueConnectionFactory");
            final QueueConnection connection = factory.createQueueConnection();
            final QueueSession session = connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            final Queue queue = (Queue) context.lookup("java:/comp/env/jms/queueTest");

            QueueSender sender = null;
            QueueReceiver receiver = null;

            try {
                sender = session.createSender(queue);
                receiver = session.createReceiver(queue);

                try {
                    sender.send(ZmqTextMessageBuilder.create().appendText(MESSAGE_1).toMessage());
                    sender.send(ZmqTextMessageBuilder.create().appendText(MESSAGE_2).toMessage());
                    sender.send(ZmqTextMessageBuilder.create().appendText(MESSAGE_3).toMessage());

                    session.commit();
                } catch (Exception ex) {
                    throw ex;
                }

                try {
                    TextMessage message1 = (TextMessage) receiver.receive(2000);

                    Thread.sleep(1000);

                    TextMessage message2 = (TextMessage) receiver.receive();
                    TextMessage message3 = (TextMessage) receiver.receiveNoWait();

                    // sometime too quick to pickup since a transaction, so try again with a wait.
                    if (message3 == null) {
                        message3 = (TextMessage) receiver.receive(1000);
                    }

                    System.out.println(MESSAGE_1+" = "+ message1.getText());
                    System.out.println(MESSAGE_2+" = "+ message2.getText());
                    System.out.println(MESSAGE_3+" = "+ message3.getText());
                } catch (Exception ex) {
                    throw ex;
                }
            } finally {
                session.close();
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args){
           System.out.println("Start");
           try {
			   setup();
           } catch (NamingException e) {
			  e.printStackTrace();
			  return;
		   }
           testSendAndReceiveMessageWithoutTransaction();
           testSendAndReceiveMessageWithTransaction();
           try {
           pulldown();
           } catch (NamingException e) {
			  e.printStackTrace();
			  return;
		   }
           System.out.println("Ok");

    }

}
