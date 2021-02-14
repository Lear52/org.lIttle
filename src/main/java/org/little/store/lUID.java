package org.little.store;

public class lUID {
       //private static int count=(int)((System.currentTimeMillis()-(((24L*3600L)*365L)*50L)*1000L)/1000L);
       private static int count=10;
       
       public synchronized  static int get() {return count++;}  
       public synchronized  static int getNext() {return count;}  
}
