/*
 * @(#)file      Client.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.1
 * @(#)lastedit  04/01/12
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */
package org.little.monitor.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
//import java.lang.management.ThreadInfo;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

//import java.util.Iterator;
//import java.util.Set;
//import javax.management.Attribute;
//import javax.management.MBeanServerConnection;
//import javax.management.MBeanServerInvocationHandler;
//import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class jmxClient {
       final private static String CLASS_NAME="prj0.jmx.jmx2";
       final private static int    CLASS_ID  =801;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}

       private JMXConnector jmxc=null;

       public jmxClient(String _url){
            try {
                JMXServiceURL url = new JMXServiceURL(_url);
                jmxc = JMXConnectorFactory.connect(url, null);
            } catch (Exception e) {
                e.printStackTrace();
                jmxc = null;
            }
       }
       public void start(){
            if(jmxc!=null)
            try {
                jmxc.connect();
            } catch (Exception e) {
                stop();
                e.printStackTrace();
            }
       }
       public void stop(){
            if(jmxc!=null)
            try {
                jmxc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            jmxc = null;
       }
       public long getHeap(){
            if(jmxc!=null)
            try {
            //MBeanServerConnection mbsc    = jmxc.getMBeanServerConnection();
            MemoryMXBean          membean = ManagementFactory.getMemoryMXBean() ;
            MemoryUsage           heap    = membean.getHeapMemoryUsage();
            System.out.println(String.format("Heap:         Init: %d, Used: %d, Committed: %d, Max.: %d"    , heap.getInit(), heap.getUsed(), heap.getCommitted(), heap.getMax()));
            //mbsc.unregisterMBean(membean);
            } catch (Exception e) {
                stop();
                e.printStackTrace();
            }
            return 0;

       }
       public long getNonHeap(){
            if(jmxc!=null)
            try {
            //MBeanServerConnection mbsc     = jmxc.getMBeanServerConnection();
            MemoryMXBean          membean  = ManagementFactory.getMemoryMXBean() ;
            MemoryUsage           nonHeap  = membean.getNonHeapMemoryUsage();
            System.out.println(String.format("Non-Heap:     Init: %d, Used: %d, Committed: %d, Max.: %d", nonHeap.getInit(), nonHeap.getUsed(), nonHeap.getCommitted(), nonHeap.getMax()));

            //mbsc.unregisterMBean(membean);
            } catch (Exception e) {
                stop();
                e.printStackTrace();
            }
            return 0;
       }

    public static void main(String[] args) {
           jmxClient cln = new jmxClient("service:jmx:rmi:///jndi/rmi://127.0.0.1:9999/jmxrmi");
           cln.start();
           cln.getHeap();
           cln.getNonHeap();

           cln.stop();

    }
    public static void main0(String[] args) {
        try {
            // Create an environment map to hold connection properties                
            // like credentials etc... We will later pass this map
            // to the JMX Connector.
            //
            //System.out.println("\nInitialize the environment map");
            //final Map<String,Object> env = new HashMap<String,Object>();

            // Provide the credentials required by the server
            // to successfully perform util authentication
            //
            //final String[] credentials = new String[] { "guest" , "guestpasswd" };
            //env.put("jmx.remote.credentials", credentials);

            // Provide the SSL/TLS-based RMI Client Socket Factory required
            // by the JNDI/RMI Registry Service Provider to communicate with
            // the SSL/TLS-protected RMI Registry
            //
            //env.put("com.sun.jndi.rmi.factory.socket",new SslRMIClientSocketFactory());

            // Create the RMI connector client and                                            getHeap()
            // connect it to the RMI connector server
            // args[0] is the server's host - localhost
            // args[1] is the secure server port - 4545
            // Create an RMI connector client and
            // connect it to the RMI connector server
            //
            echo("\nCreate an RMI connector client and connect it to the RMI connector server");

            //                                     service:jmx:rmi:///jndi/rmi://10.6.112.36:19999/jmxrmi
            //JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://10.93.128.11:9999/jmxrmi");
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:9999/jmxrmi");

            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

            jmxc.connect();
	    // Create listener
	    //
            //ClientListener listener = new ClientListener();

            // Get an MBeanServerConnection
            //
            echo("\nGet an MBeanServerConnection");

            //MBeanServerConnection mbsc = 
            		jmxc.getMBeanServerConnection();


            // Retrieve memory managed bean from management factory.
            MemoryMXBean memBean = ManagementFactory.getMemoryMXBean() ;

            MemoryUsage heap     = memBean.getHeapMemoryUsage();
            MemoryUsage nonHeap  = memBean.getNonHeapMemoryUsage();
            // Retrieve the four values stored within MemoryUsage:
            // init: Amount of memory in bytes that the JVM initially requests from the OS.
            // used: Amount of memory used.
            // committed: Amount of memory that is committed for the JVM to use.
            // max: Maximum amount of memory that can be used for memory management.
            System.out.println(String.format("Heap:         Init: %d, Used: %d, Committed: %d, Max.: %d"    , heap.getInit(), heap.getUsed(), heap.getCommitted(), heap.getMax()));
            System.out.println(String.format("Non-Heap:     Init: %d, Used: %d, Committed: %d, Max.: %d", nonHeap.getInit(), nonHeap.getUsed(), nonHeap.getCommitted(), nonHeap.getMax()));


            ThreadMXBean treadBean = ManagementFactory.getThreadMXBean();
            System.out.println("ThreadMXBean                ThreadCount:"+treadBean.getThreadCount()+" PeakThreadCount:"+treadBean.getPeakThreadCount());

            OperatingSystemMXBean operBean = ManagementFactory.getOperatingSystemMXBean();
            System.out.println("OperatingSystemMXBean        AvailableProcessors:"+operBean.getAvailableProcessors()+" SystemLoad:"+operBean.getSystemLoadAverage());

            //OperatingSystemMXBean operBean1 = ManagementFactory.getOperatingSystemMXBean();
            //System.out.println("OperatingSystemMXBean        "+" ProcessCpuTime:"+operBean.getProcessCpuTime());

            RuntimeMXBean   runBean = ManagementFactory.getRuntimeMXBean();

            System.out.println("RuntimeMXBean                Uptime:"+runBean.getUptime() +"(sec) StartTime:"+ runBean.getStartTime());


            //mbsc.unregisterMBean(mbean);

            // Close MBeanServer connection
            //

            echo("\nClose the connection to the server");
            jmxc.close();
            echo("\nBye! Bye!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void echo(String msg) {
	System.out.println(msg);
    }

    public static void sleep(int millis) {
	try {
	    Thread.sleep(millis);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public static void waitForEnterPressed() {
	try {
	    echo("\nPress <Enter> to continue...");
	    System.in.read();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }


}
