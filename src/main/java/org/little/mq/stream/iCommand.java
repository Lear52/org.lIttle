package org.little.mq.stream;

import org.little.mq.mqapi.mq_mngr_jms;

public interface iCommand{


       public abstract void  clear();

       public abstract int   start(mq_mngr_jms mngr_default);
       public abstract void  run();
       public abstract void  cmd_close();
       public abstract void  close();


}