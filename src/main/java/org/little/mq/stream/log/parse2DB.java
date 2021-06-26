package org.little.mq.stream.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.cfg.commonPRJ;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;



public class parse2DB{

       final private static String CLASS_NAME="prj0.stream.log.parse2DB";
       final private static int    CLASS_ID  =1603;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

             private int       id;
             private String    stream;
             private Timestamp time;
             private String    msg_id;
             private String    to;
             private String    from;
             private String    qm_name;
             private String    q_name;
             private int       size;
             private String    mq_id;


       public parse2DB(){clear();}

       public void clear(){
              id     =0;    
              stream =null;  
              time   =null;    
              msg_id =null;  
              to     =null;      
              from   =null;    
              qm_name=null; 
              q_name =null;  
              size   =0;    
              mq_id  =null;   
       }

       public String    getStream()               {return stream;         }
       public void      setStream(String stream)  {this.stream = stream;  }
       public Timestamp getTime()                 {return time;           }
       public String    getTimeS()                {return time.toString();}
       public void      setTime(Timestamp time)   {this.time = time;      }
       public void      setTime(String s_time)    {
              // 2017-08-27 08:55:19
              try{
                  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                  setTime(new Timestamp(df.parse(s_time).getTime()));
              }
              catch (Exception ex){
                  setTime("1900-01-01 00:00:00");
              }
       }
       public int       getID()                   {return id;             }
       public void      setID(int _id)            {this.id = _id;         }
       public String    getMsgID()                {return msg_id;         }
       public void      setMsgID(String msg_id)   {this.msg_id = msg_id;  }
       public String    getTo()                   {return to;             }
       public void      setTo(String to)          {this.to = to;          }
       public String    getFrom()                 {return from;           }
       public void      setFrom(String from)      {this.from = from;      }
       public String    getQMName()               {return qm_name;        }
       public void      setQMName(String qm_name) {this.qm_name = qm_name;}
       public String    getQName()                {return q_name;         }
       public void      setQName(String q_name)   {this.q_name = q_name;  }
       public int       getSize()                 {return size;           }
       public void      setSize(int size)         {this.size = size;      }
       public void      setSize(String _s)        {int s;try { s=Integer.parseInt(_s, 10);} catch (Exception e) {s=0;}; setSize(s);}
       public String    getMQID()                 {return mq_id;          }
       public void      setMQID(String mq_id)     {this.mq_id = mq_id;    }


       public void      setString(String str){
    	                StringTokenizer parser_line;
                        parser_line = new StringTokenizer(str, ";");
                        //----------------------------------------------------------
                        int count=1;
                        while(parser_line.hasMoreTokens()) {
                              String s;
                              s=parser_line.nextToken();
//Stream:FROM.RTGS;2017-08-27 08:55:19;guid:5ef0435a-09bf-4a78-9c01-70f94d3631d6;500400199966;555555500066;KCOI_22;TO.OITU;3438;414d5120514d5f636320202020202020599776a721e08d02

                              switch(count){
                              case  1: {StringTokenizer p_line = new StringTokenizer(s, ":");
                                        s=p_line.nextToken();
                                        s=p_line.nextToken();
                                        setStream(s);
                              }
                              case  2: setTime (s);  break;
                              case  3: setMsgID(s);  break;
                              case  4: setTo(s);     break;
                              case  5: setFrom(s);   break;
                              case  6: setQMName(s); break;
                              case  7: setQName(s);  break;
                              case  8: setSize(s);   break;
                              case  9: setMQID(s);   break;
                              }
                              count++;
                        }
                        //----------------------------------------------------------
       }
       public int setQ(query q) throws dbExcept {
                int s=1;
                try {
                     //q.setInt      ( s,getID    ());s++;//1
                     q.setString   ( s,getStream());s++;//2
                     q.setTimestamp( s,getTime  ());s++;//3
                     q.setString   ( s,getMsgID ());s++;//4
                     q.setString   ( s,getTo    ());s++;//5
                     q.setString   ( s,getFrom  ());s++;//6
                     q.setString   ( s,getQMName());s++;//7
                     q.setString   ( s,getQName ());s++;//8
                     q.setInt      ( s,getSize  ());s++;//9
                     q.setString   ( s,getMQID  ());s++;//10
                } catch (dbExcept e) {
                        throw new dbExcept(getClassName()+".setID(q,"+s+")",e);
                }
                return 9+s;
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
                   q.execute("DROP VIEW VSTREAM_LOG");
                   log.trace("drop view");
              }catch(dbExcept e) {log.error("Error drop view ex:"+e); }
              try {
                   q.execute("CREATE VIEW VSTREAM_LOG(TM,CN,SM,ST) AS SELECT TO_CHAR(a.xtime,'YYYY-MM-DD HH24:Mi') ,count(a.xtime) ,SUM(a.xsize),a.stream FROM stream_log a group by a.stream,TO_CHAR(a.xtime,'YYYY-MM-DD HH24:Mi')");
                   log.trace("create view");
              }catch(dbExcept e) {log.error("Error create view ex:"+e); }
              try {
                   q.execute("DROP sequence STREAM_LOG_SEQ");
                   log.trace("drop sequence");
              }catch(dbExcept e) {log.error("Error drop sequence ex:"+e); }
              try {
                   q.execute("create sequence STREAM_LOG_SEQ increment by 1 start with 1000 nomaxvalue nocycle nocache");
                   log.trace("create sequence");
              }catch(dbExcept e) {log.error("Error create sequence ex:"+e); }
              try {
                   q.execute("DROP TABLE STREAM_LOG");
                   log.trace("drop table");
              }catch(dbExcept e) {log.error("Error drop table ex:"+e); }
              String s="CREATE TABLE STREAM_LOG ("
                      +"STR_ID   NUMBER,"
                      +"STREAM   VARCHAR2(256 CHAR),"
                      +"XTIME    TIMESTAMP,"
                      +"MSG_ID   VARCHAR2(256 CHAR),"
                      +"MSG_TO   VARCHAR2(256 CHAR),"
                      +"MSG_FROM VARCHAR2(256 CHAR),"
                      +"QMNAME   VARCHAR2(256 CHAR),"
                      +"QNAME    VARCHAR2(256 CHAR),"
                      +"XSIZE    NUMBER,"
                      +"MQID     VARCHAR2(256 CHAR)"
                      +")";

              try {
                   q.execute(s);
                   log.trace("create table");
              }catch(dbExcept e) {log.error("Error create table ex:"+e);  }

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
                   q.execute("TRUNCATE TABLE STREAM_LOG");
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

             
              log.trace("open DB");
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
                  if(in!=null)try {in.close();} catch (IOException e1) {}
                  return;
              }
              if(in!=null)try {in.close();} catch (IOException e1) {}
              if(b==null){
                  log.error("Error open DB");
                  return;
              }
              log.trace("open DB");
              try {
                  q=b.open();                        //   1    2      3     4      5      6        7      8     9      10          1 2 3 4 5 6 7 8 9 10
                  q.creatPreSt("INSERT INTO STREAM_LOG (STR_ID,STREAM,XTIME,MSG_ID,MSG_TO,MSG_FROM,QMNAME,QNAME,XSIZE,MQID) VALUES(STREAM_LOG_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,? ) ");
              }
              catch (Except e) {
                  log.error("Error open DB ex:"+e);
                  try {in.close();} catch (IOException e1) {}
                  return;
              }

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
             
                               parse2DB obj=new parse2DB();
                               obj.setID(count1);
                               obj.setString(s);

                               obj.setQ(q);
                               q.executeQuery();
                               if(count>=10000){q.commit();count=0;}
                               else count++;
                       
                               System.out.print(count1+"                             \r");
                               
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
              return getID    ()+":"+
                     getStream()+":"+
                     getTimeS ()+":"+
                     getTo    ()+":"+
                     getFrom  ()+":"+
                     getQMName()+":"+
                     getQName ()+":"+
                     getSize  ()+":"+
                     getMQID  ();
       }


       public static void main(String[] args) {
              log.trace("start log parser");
                     if(args.length<1){
                        System.out.println(parse2DB.getClassName()+" -init | -clear | filename");
                        return;
                     }
              log.trace("cmd:"+args[0]);
                     if(args[0].equals("-init"))parse2DB.initDB();
                     else
                     if(args[0].equals("-clear"))parse2DB.clearDB();
                     else parse2DB.load(args[0]);
              log.trace("stop log parser");
       }
}
/*
DROP TABLE STREAM_LOG ;
CREATE TABLE STREAM_LOG (
STR_ID   NUMBER,
STREAM   VARCHAR2(256 CHAR),
XTIME    TIMESTAMP,
MSG_ID   VARCHAR2(256 CHAR),
MSG_TO   VARCHAR2(256 CHAR),
MSG_FROM VARCHAR2(256 CHAR),
QMNAME   VARCHAR2(256 CHAR),
QNAME    VARCHAR2(256 CHAR),
XSIZE    NUMBER,
MQID     VARCHAR2(256 CHAR)
);
DROP sequence STREAM_LOG_SEQ;
create sequence STREAM_LOG_SEQ increment by 1 start with 1000 nomaxvalue nocycle nocache;

CREATE VIEW VSTREAM_LOG(TM,CN,SM,ST) AS SELECT TO_CHAR(a.xtime,'YYYY-MM-DD HH24:Mi') ,count(a.xtime) ,SUM(a.xsize),MAX(a.stream) FROM stream_log a group by TO_CHAR(a.xtime,'YYYY-MM-DD HH24:Mi');


*/
