package org.little.store.dbstore.db;

//import prj0.util.Logger;

/** 
 * ����� Query_Info �������� ���������� � ����
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2009-2017
 * @version 1.2
 */

public class query_info{
       private final static String CLASS_NAME="prj0.util.db.query_info";
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

