/*       
 * Created on 18.09.2012
 * Modification 17/10/2014
 *
 */
package org.little.util.run;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.little.util.Logger;

//------------------------------------------------
/**
 * @author av
 * класс пула потоков
 * 
 */
public class scheduler extends tfork{
       final private static String CLASS_NAME="org.little.util.sheduler";
       final private static int    CLASS_ID  =114;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       static private Object      lock = new Object();
       static private int         count_scheduler=0;

       private int                id_scheduler   =0;
       private ArrayList<task>    list_task     = null;
       // Число задач
       private int                n_thread      = 0;
       // потоки
       private ThreadPoolExecutor runner        = null;

       private int                process_sleep = 1;

       public scheduler(){
              init(10,10);
       }
       public scheduler(int _n_task){
              init(_n_task,_n_task);
       }
       public scheduler(int _n_task,int _n_thread){
              init(_n_task,_n_thread);
       }
 
       public void init(int _n_task,int _n_thread) {
              // контейнер под ссылки на задачи
              list_task     = new ArrayList<task>(_n_task);
              n_thread      = _n_thread;
              // пул thread
              runner        = new ThreadPoolExecutor(n_thread, n_thread,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
              synchronized (lock) {
                 count_scheduler++;
                 id_scheduler  =count_scheduler;
              }
              init();
              log.trace("init scheduler_id:"+id_scheduler);
       }
       @Override
       public void clear() {
              super.clear();
              if(runner!=null)runner.shutdown();
              runner=null;
              if(list_task!=null)list_task.clear();
              list_task=null;
              id_scheduler=0;
       }
                                          
       /**
        * добавление новой задачи в список
        */
       public void add(task t) {
              if(list_task==null)return;
              list_task.add(t);
               log.trace("add task for scheduler_id:"+id_scheduler+" identification:" +t.getIdentification()+" task_period:"+t.getPeriod());
       }

       public task getZomby() {

              for(int i=0;i<list_task.size();i++){
                  task t=(task)list_task.get(i);
                  if(t==null)continue;
                  if(t.isCorrectTimeExecuted()==false){
                      log.error("run task.work() state:Zomby identification:" +t.getIdentification()+" time:"+t.getTimeExecuted()/1000+" (sec)");
                     //return t;
                   }
               }
               return null;

       }
       /**
        * получение очередной задачи
        */
       public task get() {
              long start=System.currentTimeMillis();
               task next_task=null;

              for(int i=0;i<list_task.size();i++){
                  task t=(task)list_task.get(i);
                  if(t==null)return next_task;
                  if(t.state()==task.VIRGIN){
                      // нашли не активную задачу
                     if(start>t.nextExecution()){
                         //время запуска пришло
                         //start=t.nextExecution();
                         //
                         return t;
                         
                      }

                   }
               }
               return null;

       }

       @Override
       public void run(){

              for(;;){
                 if(is_run==false)break;
                 task t;

                 synchronized (list_task){
                    t=get();
                    if(t!=null){
                       t.EXECUTED();
                    }
                 }

                 if(t!=null){
                    runner.execute(t);
                    log.trace("run task for scheduler_id:"+id_scheduler+" identification:" +t.getIdentification()+" task_period:"+t.getPeriod());
                 }
                 else{
                    t=getZomby();
                    if(t!=null){

                    }
                    delay(process_sleep);
                    continue;
                 }
                        

              }

              for(int i=0;i<list_task.size();i++){list_task.get(i).KILL();}

              runner.shutdown(); 

              clear();
              list_task=null;
              runner   =null;

              log.trace("stop all task for scheduler_id:"+id_scheduler);
      };


       //------------------------------------------------
       public static void main(String[] args)  {


               class test_task extends task{
              
                     public test_task(){super(0,15);}
              
                     @Override
                     public void work(){
                            System.out.println("run task:"+getId()+" ms:"+nextExecution());
                            log.trace("run test task.work() " +getId()+" ms:"+nextExecution());
                     };
              
               }

               scheduler runner = new scheduler(10);
               System.out.println("0");

               test_task t1=new test_task();
               t1.setId(1);
               runner.add(t1);
               System.out.println("1");
               test_task t2=new test_task();
               t2.setId(2);
               runner.add(t2);
               System.out.println("2");
               test_task t3=new test_task();
               t3.setId(3);
               runner.add(t3);
               System.out.println("3");

               runner.run();

               System.out.println("ok");
       }

}

