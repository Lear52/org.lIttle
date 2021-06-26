package org.little.stream.ufps;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ufps_buffer {
       private Queue<ufps_msg> queue;

       public ufps_buffer() {
            queue=new ConcurrentLinkedQueue<ufps_msg>();
       }

       public void     put(ufps_msg arg){ queue.add(arg);}  
       public ufps_msg get(){return queue.poll();}     
       public int      size(){return queue.size();}     


}

