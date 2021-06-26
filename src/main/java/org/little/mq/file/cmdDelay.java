package org.little.mq.file;



import org.little.util.run.tfork;

public class cmdDelay implements cmdElement{

       private int    timeout;
       private boolean  is_run;

       public cmdDelay() {
              clear();
       }
       public cmdDelay(int _timeout){
              timeout=_timeout;
       }

       @Override
       public void clear() {
              timeout=0;
              isRun(true);
       }


       @Override
       public void open(){}
       @Override
       public void work(){
              if(timeout!=0)tfork.delayMs(timeout);
              isRun(false);
       }
       @Override
       public void close(){}
       @Override
       public long getCount(){return 0;}
       @Override
       public boolean isRun(){return is_run;}
       @Override
       public void isRun(boolean r){is_run=r;}

}
