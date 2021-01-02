package org.little.util.db;


/** 
 * Класс Query_Info содержит информацию о базе
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2009-2021
 * @version 1.4
 */

public class query_info{
       private final static String CLASS_NAME="org.little.util.db.query_info";
       private final static int    CLASS_ID  =206;
             public        static String getClassName(){return CLASS_NAME;}
             public        static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       private              long close_time;   
       private              long connect_time;


       //-----------------------------------------------------------------------------
       // if(connect_time>0 && close_time==0)  connect run
       // if(connect_time>0 && close_time>0)   connect stop
       // if(connect_time==0 && close_time==0) empty
       // if(connect_time==0 && close_time==0) is bad
       //-----------------------------------------------------------------------------
       public query_info(){
              close_time=0;
              connect_time=0;
       }
       public void start(){ 
              connect_time=System.currentTimeMillis();
              restart();
       } 
       public void restart(){ 
              close_time=0;
       } 
       public void stop(){ 
              close_time=System.currentTimeMillis();
       } 
       public boolean isStop(){ 
              return close_time!=0 && connect_time!=0;
       } 
       public boolean isRun(){ 
              return close_time==0 && connect_time!=0;
       } 
       public boolean isEmpty(){ 
              return close_time==0 && connect_time==0;
       } 
      
      
      
       public long getStop(){ 
              return close_time;
       } 
       public long getConnect(){ 
              return connect_time;
       } 
       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
}

