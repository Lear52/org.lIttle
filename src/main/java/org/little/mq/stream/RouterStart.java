package org.little.mq.stream;

import org.little.util.Logger;


public class RouterStart  extends Router{
       final private static String CLASS_NAME="org.little.stream.RouterStart";
       final private static int    CLASS_ID  =1607;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       public RouterStart(){
              clear();
       }
       @Override
       public String nameRouter(){return CLASS_NAME;}

       @Override
       public void work() {

    	      log.info("begin RouterStart");
              super.work();
    	      log.info("end RouterStart");
    	    
       }

       
       public static void main(String[] args) {

           RouterStart r = null;

           r=new RouterStart();

           if(r.init(args)<0)return;

           r.work();
      }


}