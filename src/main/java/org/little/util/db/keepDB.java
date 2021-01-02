/*
 * Created on 18.09.2012
 * Modification 17/10/2014
 *
 */
package org.little.util.db;

import org.little.util.Logger;
import org.little.util.tfork;
import org.little.util.cfg.commonPRJ;
/** 
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002-2021
 * @version 1.4
 */

public class keepDB  extends tfork{
       final private static String CLASS_NAME="org.little.util.db.keepDB";
       final private static int    CLASS_ID  =204;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);


       public keepDB(){}
                                          

       @Override
       public void run(){
              commonPRJ       c=null;
              query           q=null;
              connection_pool b=null;

              try{
                 c = commonPRJ.getInstance();
              }
              catch(Exception e){
                     log.error("get instance ex:"+e);
                     return;
              }
              while(super.isRun()){

                    try{
                        b = c.getDB();
                        q = b.open();
                        q.execute("select count(*) from dual");
                        try{
                            while(q.isNextResult()) {
                                  q.Result().getInt(1);
                                  log.trace("ping db q_id:"+q.getId());
                            } 
                        }catch(Exception ex1){}

                    }catch(dbExcept ex){
                        if(q!=null)log.error("ping db q_id:"+q.getId()+" ex:"+ex);
                        else       log.error("ping db q_id:? ex:"+ex);
                    }
                    finally{
                        b.close(q);
                    }
                    delay(60);
              }

              super.clear();
      };


       //------------------------------------------------
       public static void main(String[] args)  {
              keepDB db=new keepDB();
              db.fork();
              delay(600);
              db.shutdown();

       }

}

