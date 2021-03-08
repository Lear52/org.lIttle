/*
 * Created on 18.08.2014
 *
 */
package org.little.util.run;

/**
 * @author av
 * базовый класс для записей в справочниках
 * 
 */
public class task   implements Runnable{
       private final static String CLASS_NAME="org.little.util.task";
       private final static int    CLASS_ID  =117;
       public        static String getClassName(){return CLASS_NAME;}
       public        static int    getClassId(){return CLASS_ID;}
            // private static Logger log=new Logger(CLASS_NAME);

       public  final static  int   VIRGIN   = 0;
       public  final static  int   KILL     = 1;
       public  final static  int   EXECUTED = 2;

       private               int   state;
                            
       private               long  startExecutionTime;
       private               long  stopExecutionTime;
       private               long  nextExecutionTime;
                            
       private               int   period;

       /**
        * системный идентификатор 
        */
       private               int   id;


       public void clear() {
               id                 = 0;
               startExecutionTime = 0;
               stopExecutionTime  = 0;
               nextExecutionTime  = 0;
               state              = VIRGIN;
               period             = 0;
       }

       public task(int delay) {
              clear();
              startExecutionTime=System.currentTimeMillis();
              nextExecutionTime=startExecutionTime;
              setPeriod(delay);

       }

       public task(int start,int delay) {
              clear();
              startExecutionTime=System.currentTimeMillis()+start*1000L;
              nextExecutionTime=startExecutionTime;
              setPeriod(delay);
       }


       @Override
       public void run(){

              EXECUTED();

              work();

              VIRGIN();
       };

       public void work(){};

       public void KILL(){
                   state=KILL;
       }
       public void VIRGIN(){
                   state=VIRGIN;
                   stopExecutionTime=System.currentTimeMillis();
                   nextExecutionTime=stopExecutionTime+period*1000L;
       };
       public void EXECUTED(){
                   state             = EXECUTED;
                   startExecutionTime= System.currentTimeMillis();
                   stopExecutionTime = 0;
                   nextExecutionTime = System.currentTimeMillis()+period*1000L;
       };

       public long    getTimeExecuted() {return state==EXECUTED?System.currentTimeMillis()-startExecutionTime:0;}

       public boolean isCorrectTimeExecuted() {return getTimeExecuted()<period*1000L;}

       public int  getPeriod() {return period;}

       public void setPeriod(int p) {period=p;}
       /**
        * @return int
        */
       public int getId() {return id;        }
       /**
        * @param _id
        */
       public void setId(int _id) {id = _id;        }

       public void setStart(int start) {
              nextExecutionTime=System.currentTimeMillis()+start*1000L;
       }
       
       public int state(){return state;}

       public long nextExecution(){return nextExecutionTime;}

       public String getIdentification() {return ""+id;}

}


