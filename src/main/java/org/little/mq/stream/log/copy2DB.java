package org.little.mq.stream.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.cfg.commonPRJ;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;




public class copy2DB{

       final private static String CLASS_NAME="prj0.stream.log.copy2DB";
       final private static int    CLASS_ID  =1603;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

             private long      tm;
             private String    msg_id;
             private String    corr_id;
             private String    type;


       public copy2DB(){clear();}

       public void clear(){
              tm     =0;    
              msg_id =null;  
              corr_id =null;  
              type   =null;      
       }

       public String    getMsgID()               {return msg_id;         }
       public void      setMsgID(String _msg_id) {if(_msg_id==null)_msg_id="NULL";if(_msg_id.length()==0)_msg_id="NULL";this.msg_id = _msg_id;  }
       public String    getCorrID()              {return corr_id;        }
       public void      setCorrID(String _msg_id){if(_msg_id==null)_msg_id="NULL";if(_msg_id.length()==0)_msg_id="NULL";this.corr_id = _msg_id; }
       public String    getType()                {return type;           }
       public void      setType(String _type)    {if(_type==null)_type="NULL";if(_type.length()==0)_type="NULL";this.type = _type;      }
       public long      getTime()                {return tm;             }
       public void      setTime(long _tm)        {this.tm = _tm;         }


       public void      setString(String str){
    	                StringTokenizer parser_line;
                        parser_line = new StringTokenizer(str, " ",false);
                        //----------------------------------------------------------
                        int count=1;
                        while(parser_line.hasMoreTokens()) {
                              String s;
                              s=parser_line.nextToken();
                              switch(count){
                              case  1: {
                                       long t=0;
                                       try{t=Long.parseLong(s);}catch(Exception e){t=0;}
                                       setTime(t);
                                       }
                                       break;
                              case  2: setMsgID(s);  break;
                              case  3: setCorrID(s); break;
                              case  4: setType(s);   break;
                              }
                              count++;
                        }
                        //----------------------------------------------------------
       }
       public int setQ(query q) throws dbExcept {
                int s=1;
                try {
                     q.setLong     ( s,getTime  ());s++;//1
                     q.setString   ( s,getMsgID ());s++;//2
                     q.setString   ( s,getCorrID());s++;//3
                     q.setString   ( s,getType  ());s++;//4

                     Timestamp _t=new Timestamp(tm);
                     q.setTimestamp(s,_t);s++;//5

                } catch (dbExcept e) {
                        throw new dbExcept(getClassName()+".setQ(q,"+s+")",e);
                }
                return 5+s;
       }
       static void initDB(){
              connection_pool b;
              query           q;

              try {
                  b=commonPRJ.getInstance().getDB();
              }
              catch (Except e) {
                  log.error("Error open DB ex:"+e);
                  return;
              }
              if(b==null){
                  log.error("Error open DB");
                  return;
              }

              log.trace("open DB");

              try {
                  q=b.open();        
              }
              catch (dbExcept e) {
                  log.error("Error open DB ex:"+e);
                  return;
              }

              try {
                   q.execute("DROP TABLE COPY_LOG");
                   log.trace("drop table COPY_LOG");
              }catch(dbExcept e) {log.error("Error drop table ex:"+e); }
              String s="CREATE TABLE COPY_LOG ("
                      +"TIME_ID  NUMBER,"
                      +"MSG_ID   VARCHAR2(256 CHAR),"
                      +"CORR_ID  VARCHAR2(256 CHAR),"
                      +"MSG_TYPE VARCHAR2(256 CHAR),"
                      +"DT       TIMESTAMP          "
                      +")";

              try {
                   q.execute(s);
                   log.trace("create table");
              }catch(dbExcept e) {log.error("Error create table ex:"+e);  }
              try {
                   q.execute("DROP VIEW VCOPY_LOG");
                   log.trace("drop view VCOPY_LOG");
              }catch(dbExcept e) {log.error("Error drop view ex:"+e); }
              try {
                   q.execute("CREATE VIEW VCOPY_LOG(TM,DT,ID,CID,TP1,TP2) AS SELECT A.TIME_ID,(B.TIME_ID-A.TIME_ID),B.MSG_ID,B.CORR_ID,A.MSG_TYPE,B.MSG_TYPE FROM COPY_LOG A,COPY_LOG B WHERE A.MSG_ID=B.CORR_ID");
                   log.trace("create view");
              }catch(dbExcept e) {log.error("Error create view ex:"+e); }

              log.trace("init DB");
              try {
                  q.closePreSt();
                  b.close(q);
              }
              catch (dbExcept e) {
                  log.error("Error close DB ex:"+e);
                  return;
              }

              log.trace("close DB");

       }
       static void clearDB(){
              connection_pool b;
              query           q;

              try {
                  b=commonPRJ.getInstance().getDB();
              }
              catch (Except e) {
                  log.error("Error open DB ex:"+e);
                  return;
              }
              if(b==null){
                  log.error("Error open DB");
                  return;
              }

              log.trace("open DB");

              try {
                  q=b.open();        
              }
              catch (dbExcept e) {
                  log.error("Error open DB ex:"+e);
                  return;
              }

              try {
                   q.execute("TRUNCATE TABLE COPY_LOG");
                   log.trace("clear table");
              }catch(dbExcept e) {log.error("Error drop view ex:"+e); }

              log.trace("clear DB");
              try {
                  q.closePreSt();
                  b.close(q);
              }
              catch (dbExcept e) {
                  log.error("Error close DB ex:"+e);
                  return;
              }

              log.trace("close DB");

       }

       static void load(String filename){
              File            f;
              FileReader      f_r;
              BufferedReader  in;
              connection_pool b;
              query           q;

             
              log.trace("begin load");
              try {
                  f  =new File(filename);
                  f_r=new FileReader(f);
                  in =new BufferedReader(f_r,102400);
              }
              catch (Exception e) {
                  log.error("Error open file:"+filename+" ex:"+e);
                  return;
              }
              log.trace("Open:"+filename);
              //--------------------------------------------------------------
              try {
                  b=commonPRJ.getInstance().getDB();
              }
              catch (Except e) {
                  log.error("Error open DB ex:"+e);
                  return;
              }
              finally {
                  try {in.close();} catch (IOException e1) {}
                  try {f_r.close();} catch (IOException e1) {}
              }
              if(b==null){
                  log.error("Error open DB");
                  return;
              }
              log.trace("get instance DB");
              try {
                  q=b.open();                       //1       2      3       4      
                  q.creatPreSt("INSERT INTO COPY_LOG (TIME_ID,MSG_ID,CORR_ID,MSG_TYPE,DT) VALUES(?,?,?,?,?) ");
              }
              catch (Except e) {
                  log.error("Error open DB ex:"+e);
                  try {in.close();} catch (IOException e1) {}
                  return;
              }
              log.trace("open DB");

              log.trace("begin parse");

              //--------------------------------------------------------------
              try{
                  String str;
                  StringTokenizer parser_line;
                  int count=0;
                  int count1=1;
                  q.commit(false);
                  while((str = in.readLine()) != null) {
                         parser_line = new StringTokenizer(str, "\n\r");
                         //----------------------------------------------------------
                         while(parser_line.hasMoreTokens()) {
                               String s;
                               s=parser_line.nextToken();
             
                               copy2DB obj=new copy2DB();
//System.out.println(s);
                               obj.setString(s);

                               obj.setQ(q);
                               q.executeQuery();
                               if(count>=1000){q.commit();count=0;}
                               else count++;
                       
                               System.out.print(count1+"                             \r");
                               //System.out.println(count1+" "+obj.toString());
                               
                               count1++;
                         }
                         //----------------------------------------------------------
                  }
                  q.commit();
                  System.out.println("\nstop");
                  in.close();
                  f_r.close();
              } 
              catch (Exception e){
                  log.error("Error  ex:"+e);
                  try {in.close();} catch (IOException e1) {}
              }

              log.trace("end parse");

              try {
                  q.closePreSt();
                  b.close(q);
              }
              catch (Except e) {
                  log.error("Error close DB ex:"+e);
                  return;
              }
              log.trace("close DB");

       }
       public String toString(){
              return getTime  ()+":"+
                     getMsgID ()+":"+
                     getCorrID()+":"+
                     getType  ()+":";
       }

       public static void help() {
                     System.out.println(copy2DB.getClassName()+" -init | -clear | filename");
       }

       public static void main(String[] args) {
              log.trace("start log parser");
                     if(args.length<1){
                        help();
                        return;
                     }
              log.trace("cmd:"+args[0]);
                     if(args[0].equals("-help"))help();
                     else
                     if(args[0].equals("-init"))copy2DB.initDB();
                     else
                     if(args[0].equals("-clear"))copy2DB.clearDB();
                     else copy2DB.load(args[0]);
              log.trace("stop log parser");
       }
}
/*
TIME_ID,MSG_ID,CORR_ID,MSG_TYPE

*/
