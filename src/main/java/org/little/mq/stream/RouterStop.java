package org.little.mq.stream;

import org.little.util.Logger;

public class RouterStop extends Router{

       final private static String CLASS_NAME="org.little.stream.RouterStop";
       final private static int    CLASS_ID  =1608;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);


       public RouterStop(){
              clear();
       }
       @Override
       public void work() {

    	      log.info("begin RouterStop");
              Command.send_stop(qm);
    	      log.info("end RouterStop");
    	    
       }

       public String nameRouter(){return CLASS_NAME;}

       public static void main(String[] args) {

         RouterStop r = null;
         r=new RouterStop();
         if(r.init(args)<0)return;
         r.work();

       }

}
