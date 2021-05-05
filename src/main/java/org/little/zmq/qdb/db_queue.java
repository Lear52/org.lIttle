/*
 * Created on 24.12.2020
 * Modification 24/12/2020
 *
 */
package org.little.zmq.qdb;

import java.util.ArrayList;
import java.util.Date;

//import org.little.util.Logger;
import org.little.util.db.dbExcept;
import org.little.util.db.query;
import org.little.util.db.sequence;

//------------------------------------------------
public class db_queue{
       final private static String CLASS_NAME="org.little.qdb.db_queue";
       final private static int    CLASS_ID  =204;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       private String name;
       private String last_id;
	   private ArrayList<String> ids;
       private query    q;
       private sequence sq;
       

       public String getLast_id() {return last_id;}

       public void open(query _q,sequence _sq,String _name){
    	   name=_name;
    	   q=_q;
    	   sq=_sq;
    	   ids=new ArrayList<String>(1000);
           try {
               q.creatPreSt("select id from QUEUE where QUEUE_NAME=?");
           } catch (dbExcept e) {
               e.printStackTrace();
           }
           int s=0;
           try {
                q.setString( 1+s,name);
           } 
           catch (dbExcept e) {
                 e.printStackTrace();
           }
           try {
               q.executeQuery();
               while(q.isNextResult()) {
                   String id=q.Result().getString(1);  
            	   ids.add(id);
            	   last_id=id;
               } 
           } catch (Exception e) {
                 e.printStackTrace();
           }


       }
       public void close(){
    	   name=null;
    	   q=null;
    	   sq=null;
    	   ids=null;
    	   last_id=null;

       }

       public void put(byte [] buffer){
           try {
               //                               1  2          3       4        5      6      7           1 2 3 4 5 6 7
               q.creatPreSt("INSERT INTO QUEUE (ID,QUEUE_NAME,USER_ID,CREATE_T,IS_DEL,IS_GET,MSG) VALUES(?,?,?,?,?,?,?)");
           } catch (dbExcept e) {
               e.printStackTrace();
           }
           int s=0;
           try {
                q.setString( 1+s,"LOCALHOST"+System.currentTimeMillis()+""+sq.get());
                q.setString( 2+s,name);
                q.setInt   ( 3+s,1);
                q.setDate  ( 4+s,new java.sql.Date((new Date()).getTime()));
                q.setString( 5+s,"N");
                q.setString( 6+s,"N");
                q.setBLOB  ( 7+s,buffer);
           } catch (dbExcept e) {
                 e.printStackTrace();
           }
           try {
                q.execute();
           } catch (dbExcept e) {
                e.printStackTrace();
           }
    	   
       }
       public byte [] get(){
           String id=ids.get(0);
           try {
               q.creatPreSt("select ID,QUEUE_NAME,USER_ID,CREATE_T,IS_DEL,IS_GET,MSG from QUEUE where ID=?");
           } catch (dbExcept e) {
               e.printStackTrace();
           }
           int s=0;
           try {
                q.setString( 1+s,id);
           } 
           catch (dbExcept e) {
                 e.printStackTrace();
           }
           try {
               q.executeQuery();
               while(q.isNextResult()) {
                   id=q.Result().getString(1);
                   id=q.Result().getString(1);

                   ids.remove(0);                   
                   return null;
               } 
           } catch (Exception e) {
                 e.printStackTrace();
           }
           
           
    	   return null;
       }


}

