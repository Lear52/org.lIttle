/*
 * Created on 24.12.2020
 * Modification 24/12/2020
 *
 */
package org.little.zmq.qdb;

import java.sql.ResultSet;
import java.util.Date;

import org.little.util.Logger;
import org.little.util.cfg.commonPRJ;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;
import org.little.util.db.sequence;

//------------------------------------------------
public class qdb{
       final private static String CLASS_NAME="org.little.qdb.qdb";
       final private static int    CLASS_ID  =204;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

       public void put(query q,sequence sq,byte [] buffer){
              try {
                  //                               1  2          3       4        5      6      7           1 2 3 4 5 6 7
                  q.creatPreSt("INSERT INTO QUEUE (ID,QUEUE_NAME,USER_ID,CREATE_T,IS_DEL,IS_GET,MSG) VALUES(?,?,?,?,?,?,?)");
              } catch (dbExcept e) {
                  e.printStackTrace();
              }
              //System.out.println("prepared");
              int s=0;
              try {
                   q.setString( 1+s,"LOCALHOST"+System.currentTimeMillis()+""+sq.get());
                   q.setString( 2+s,"QUEUE_1");
                   q.setInt   ( 3+s,1);
                   q.setDate  ( 4+s,new java.sql.Date((new Date()).getTime()));
                   q.setString( 5+s,"N");
                   q.setString( 6+s,"N");
                   q.setBLOB  ( 7+s,buffer);
              } catch (dbExcept e) {
                      //throw new dbExcept("arhHW1C.getAll",e);
                    e.printStackTrace();
              }
              try {
                   //q.executeQuery();
                   q.execute();
              } catch (dbExcept e) {
                    e.printStackTrace();
              }
              //System.out.println("exec");

       }
       public byte [] get(query q){
              try {
                  q.creatPreSt("");
              } catch (dbExcept e) {
                  e.printStackTrace();
              }
              int s=0;
              try {
                   q.setString( 1+s,"QUEUE_1");
              } catch (dbExcept e) {
                      //throw new dbExcept("arhHW1C.getAll",e);
                    e.printStackTrace();
              }
              try {
                   ResultSet rq=q.executeQuery();
                   rq.getBlob(1);
              } catch (Exception e) {
                    e.printStackTrace();
              }

              return null;
       }
       public void init(query q){
              try {
                   q.execute("CREATE SCHEMA PRJ02");
                   q.execute("CREATE TABLE QUEUE("
                              +"ID         VARCHAR(128), "
                              +"QUEUE_NAME VARCHAR(128),"
                              +"USER_ID    INT, "
                              +"CREATE_T   TIMESTAMP,"
                              +"IS_DEL     CHAR(1),"
                              +"IS_GET     CHAR(1),"
                              +"MSG        BLOB"
                              +")"
                              );
                   q.execute("CREATE TABLE DUAL(VOL CHAR(1) )" );
                   q.execute("INSERT INTO  DUAL(VOL) VALUES('Y')" );
                   q.execute("create sequence MSG_ID_SEQ increment by 1 start with 1000");
                 } catch (dbExcept e) {
                            e.printStackTrace();
                 }

       }
                                          

       public void run(){
              commonPRJ          c=null;
              query           q1=null;
              query           q2=null;
              connection_pool b=null;
              sequence        sq;;      

              try{
                 c = commonPRJ.getInstance();
              }
              catch(Exception e){
                     log.error("get instance ex:"+e);
                     return;
              }

              StringBuilder buf=new StringBuilder();
              for(int i=0;i<40;i++)buf.append("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
              byte [] buffer=buf.toString().getBytes();


              try{
                  System.out.println("start:"+new Date()+" buffer.length:"+buffer.length);
                  b = c.getDB();
                  q1 = b.open();
                  q2 = b.open();
                  System.out.println("open:"+new Date());
                  sq=new sequence("MSG_ID_SEQ",q2);
                  init(q1);
                  System.out.println("init:"+new Date());
                  final int count=1000000;
                  long st=System.currentTimeMillis();
                  for(int i=0;i<count;i++)put(q1,sq,buffer);
                  long so=System.currentTimeMillis();
                 
                  System.out.println("put:"+new Date());
                  System.out.println("tps:"+(long)(count*1000)/(so-st));

              }catch(dbExcept ex){
                  log.error("db ex:"+ex);
              }
              finally{
                  b.close(q1);
                  b.close(q2);
              }

              /*
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
              */
      };


       //------------------------------------------------
       public static void main(String[] args)  {
              qdb db=new qdb();
              db.run();
              //delay(600);
              //db.shutdown();

       }

}

