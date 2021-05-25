package org.little.mailWeb.folder;
       
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.little.lmsg.lMessage;
import org.little.lmsg.lMessage2JSON;
import org.little.lmsg.lMessageX509;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;
import org.little.util.Except;

public class folderDB implements folderARH{
       private static final Logger    logger = LoggerFactory.getLogger(folderDB.class);
       private static connection_pool db=null;

       private String       queryInsertEml;
       private String       queryInsertX509;
       private String       queryInsertSerialR;
       private String       queryCreateEml;
       private String       queryCreateSeq;
       private String       queryCreateX509;                              
       private String       queryCreateSerialR;                              
       private String       querySelect;
       private String       querySelectID;
       private String       querySelectType;
       private String       querySelectX509;
       private String       querySelectX509ID;
       private String       querySelectX509Type;
       private String       querySelectX509SEARCHID;
       private String       querySelectX509ALARMID;
       private String       querySelectX509ALARM;
       private String       queryGetID;
       private String       queryUpdateRL;
       private String       queryUpdateFR;
       private commonX509DB cfg;

       public  folderDB(commonX509DB _cfg){
               queryInsertEml         ="ERROR";         
               queryInsertX509        ="ERROR";        
               queryInsertSerialR     ="ERROR";      
               queryCreateEml         ="ERROR";         
               queryCreateSeq         ="ERROR";         
               queryCreateX509        ="ERROR";        
               queryCreateSerialR     ="ERROR";      
               querySelect            ="ERROR";            
               querySelectID          ="ERROR";          
               querySelectType        ="ERROR";        
               querySelectX509        ="ERROR";        
               querySelectX509ID      ="ERROR";      
               querySelectX509Type    ="ERROR";    
               querySelectX509SEARCHID="ERROR";
               querySelectX509ALARMID ="ERROR";
               querySelectX509ALARM   ="ERROR";
               queryGetID             ="ERROR";             
               queryUpdateRL          ="ERROR";          
               queryUpdateFR          ="ERROR";          
               cfg=_cfg;
               logger.trace("clear sql object folderDB");
       }

       private boolean _create(String query,String name_obj,String msg){
           if(db==null){
               logger.error("db is not init");
               return false;
            }
            query q=null;
            try {
                 q=db.open();
                 q.execute(query);
                 //logger.trace("create "+msg+":"+name_obj);
                 //return true;
            } catch (dbExcept ex1) {   
               if(logger.isTrace()) {
                  logger.error("create "+msg+":"+name_obj+" ex:"+ex1);
                  return false;
               }
            } catch (Exception ex2) {
               if(logger.isTrace()) {
                  logger.error("create "+msg+":"+name_obj+" ex:"+ex2);
                  return false;
               }
            }
            finally{
                 db.close(q);
            }

            logger.info("create object folderDB msg:"+msg+" db_obj:"+name_obj+" sql:"+query);

            return true;   
       }
       @Override
       public  boolean create(){
               boolean ret1;
               boolean ret2;
               boolean ret3;
               boolean ret4;

               logger.info("create object folderDB");

               ret1=_create(queryCreateSeq    ,cfg.getSeq()        ,"seq"  );
               ret2=_create(queryCreateEml    ,cfg.getTableEml()   ,"table_eml");
               ret3=_create(queryCreateX509   ,cfg.getTableX509()  ,"table_x509");
               ret4=_create(queryCreateSerialR,cfg.getTableSerial(),"table_serial");

               logger.info("create object folderDB :"+(ret1&&ret2&&ret3&&ret4));

               return ret1&&ret2&&ret3&&ret4;
       }


       @Override
       public synchronized void open() {

           if(db==null){
              logger.trace("begin init_db");
              db=new connection_pool();
              db.init(cfg.getDrv(),cfg.getURL(),cfg.getUser(),cfg.getPasswd());
              logger.trace("init connection_pool db");
           }
           String next_id ;
           //String next_id0;

           next_id =  " NEXT VALUE FOR "+cfg.getSeq();
           //next_id0=" "+cfg.getSeq()+".NEXTVAL"+" ,";

           queryGetID="VALUES "+next_id;
                                     // 
           String fields_selectSerial="x509_ID,X509_SERIAL";
           //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                     // 1          2         3           4         5         6          7             8           9              10            11         12      
           String fields_select_eml   ="E.MAIL_UID,E.MAIL_ID,E.ADDR_FROM,E.ADDR_TO,E.SUBJECT,E.FILENAME,E.CREATE_DATE,E.SENT_DATE,E.RECEIVE_DATE,E.ATTACH_SIZE,E.X509_TXT,E.X509_ID";
                                     //            1       2         3       4       5        6           7         8            9           10       11        
           String fields_values_eml   ="  MAIL_UID,MAIL_ID,ADDR_FROM,ADDR_TO,SUBJECT,FILENAME,CREATE_DATE,SENT_DATE,RECEIVE_DATE,ATTACH_SIZE,X509_TXT,X509_ID";
           String fields_insert_eml  =next_id+
                                                 ",?      ,?        ,?      ,?      ,?       ,?          ,?        ,?           ,?          ,?       ,?";
           //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                     // 1         2           3                4                 5               6             7              8             9              10             11
           String fields_select_x509  ="X.X509_ID,X.X509_TYPE,X.X509_TYPE_FILE,X.X509_BEGIN_DATE,X.X509_END_DATE,X.X509_SERIAL,X.X509_SUBJECT,X.X509_ISSUER,X.X509_DATE_RL,X.X509_DATE_FR,X.X509_BIN";
                                     // 1      2         3              4               5             6           7            8           9
           String fields_values_x509 ="X509_ID,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER,X509_BIN";
           String fields_insert_x509  ="?,     ?        ,?             ,?              ,?            ,?          ,?           ,?          ,?";

           queryInsertEml         = "INSERT INTO "+cfg.getTableEml()   +" ( "+fields_values_eml   +" ) VALUES ("+fields_insert_eml+" ) ";
           queryInsertSerialR     = "INSERT INTO "+cfg.getTableSerial()+" ( "+fields_selectSerial +" ) VALUES (?,?) ";
           queryInsertX509        = "INSERT INTO "+cfg.getTableX509()  +" ( "+fields_values_x509  +" ) VALUES ("+fields_insert_x509+" ) ";
                                
           querySelect            = "SELECT "+fields_select_eml+","+fields_select_x509+" FROM "+cfg.getTableEml()+" E,"+cfg.getTableX509()+" X WHERE E.X509_ID=X.X509_ID ORDER BY E.CREATE_DATE ";
           querySelectID          = "SELECT "+fields_select_eml+","+fields_select_x509+" FROM "+cfg.getTableEml()+" E,"+cfg.getTableX509()+" X WHERE E.X509_ID=X.X509_ID AND E.MAIL_UID = ? ORDER BY E.CREATE_DATE ";
           querySelectType        = "SELECT "+fields_select_eml+","+fields_select_x509+" FROM "+cfg.getTableEml()+" E,"+cfg.getTableX509()+" X WHERE E.X509_ID=X.X509_ID AND X.X509_TYPE = ? ORDER BY E.CREATE_DATE ";

           querySelectX509        ="SELECT "+fields_select_x509 +" FROM "+cfg.getTableX509()+" X ORDER BY X.X509_BEGIN_DATE";
           querySelectX509Type    ="SELECT "+fields_select_x509 +" FROM "+cfg.getTableX509()+" X WHERE X.X509_TYPE = ? ORDER BY X.X509_BEGIN_DATE";
           querySelectX509ID      ="SELECT "+fields_select_x509 +" FROM "+cfg.getTableX509()+" X WHERE X.X509_ID = ? ORDER BY X.X509_BEGIN_DATE";
           querySelectX509SEARCHID="SELECT X.X509_ID FROM "+cfg.getTableX509()+" X "
           //                              1                 2                      3                       4                     5                   6                    7                   8               
                                   +"WHERE X.X509_TYPE=? and X.X509_TYPE_FILE=? and X.X509_BEGIN_DATE=? and X.X509_END_DATE=? and X.X509_SERIAL=? and X.X509_SUBJECT=? and X.X509_ISSUER=? and X.X509_BIN=?";

           // X.X509_DATE_RL IS NULL sertificat is OK
           // X.X509_DATE_FR IS NULL sertificat no send alarm
           //querySelectX509ALARMID ="SELECT X.X509_ID FROM "+cfg.getTableX509()+" X WHERE X.X509_DATE_RL IS NULL and X.X509_DATE_FR IS NULL and X.X509_TYPE='"+lMessageX509.X509_CERTIFICATE+"' and X.X509_END_DATE>=? ";
           querySelectX509ALARMID ="SELECT "+fields_select_x509+" FROM "+cfg.getTableX509()+" X WHERE X.X509_DATE_RL IS NULL and X.X509_DATE_FR IS NULL and X.X509_TYPE='"+lMessageX509.X509_CERTIFICATE+"' and X.X509_BEGIN_DATE<=? and X.X509_END_DATE>=? and X.X509_END_DATE<=? ";

           querySelectX509ALARM   ="SELECT "+fields_select_x509+" FROM "+cfg.getTableX509()+" X WHERE X.X509_DATE_RL IS NULL and X.X509_TYPE='"+lMessageX509.X509_CERTIFICATE+"' and X.X509_BEGIN_DATE<=? and X.X509_END_DATE>=? and X.X509_END_DATE<=? ";

           queryUpdateRL          = "UPDATE "+cfg.getTableX509()+" SET X509_DATE_RL=? WHERE X509_SERIAL = ? AND X509_TYPE='"+lMessageX509.X509_CERTIFICATE+"'";
                                  //
           queryUpdateFR          = "UPDATE "+cfg.getTableX509()+" SET X509_DATE_FR=? WHERE X509_ID = ? AND X509_TYPE='"+lMessageX509.X509_CERTIFICATE+"'";

           queryCreateSeq  = "CREATE SEQUENCE "+cfg.getSeq()+"  START WITH 1000 INCREMENT BY 1  "; //CREATE SEQUENCE ARH_SEQ START WITH 1000 INCREMENT BY 1  NOCYCLE    

           queryCreateEml  = "CREATE TABLE "   +cfg.getTableEml()+" ( "+
                         "MAIL_UID          INTEGER,"+      
                         "MAIL_ID           VARCHAR(512),"+ 
                         "ADDR_FROM         VARCHAR(512),"+ 
                         "ADDR_TO           VARCHAR(8192),"+ 
                         "SUBJECT           VARCHAR(2048),"+ 
                         "FILENAME          VARCHAR(512),"+ 
                         "CREATE_DATE       TIMESTAMP,"+         
                         "SENT_DATE         TIMESTAMP,"+         
                         "RECEIVE_DATE      TIMESTAMP,"+         
                         "ATTACH_SIZE       INTEGER,"+ 
                         "X509_TXT          VARCHAR(8192),"+
                         "X509_ID           INTEGER"+ 
                         ")";
           queryCreateX509  = "CREATE TABLE "+cfg.getTableX509()+" ( "+
                         "X509_ID           INTEGER,"+           // 1
                         "X509_TYPE         VARCHAR(64),"+       // 2
                         "X509_TYPE_FILE    VARCHAR(64),"+       // 3
                         "X509_BEGIN_DATE   TIMESTAMP,"+         // 4
                         "X509_END_DATE     TIMESTAMP,"+         // 5
                         "X509_SERIAL       VARCHAR(20480),"+    // 6
                         "X509_SUBJECT      VARCHAR(8192), "+    // 7
                         "X509_ISSUER       VARCHAR(8192), "+    // 8
                         "X509_BIN          VARCHAR(20480), "+   // 9
                         "X509_DATE_RL      TIMESTAMP, "+        // 10
                         "X509_DATE_FR      TIMESTAMP "+         // 11
                         ")";
           queryCreateSerialR  = "CREATE TABLE "+cfg.getTableSerial()+" ( "+
                         "X509_ID           INTEGER,"+ 
                         "X509_SERIAL       VARCHAR(64)"+
                         ")";

           logger.trace(queryCreateSeq);
           logger.trace(queryCreateEml);
           logger.trace(queryCreateX509);
           logger.trace(queryCreateSerialR);

       }

       @Override
       public synchronized void close() {
              if(db!=null){try {db.close();} catch (dbExcept e) {}} 
              db=null;
              logger.trace("close db");
       }
       @Override
       public ArrayList<lMessage> loadArray(String _type) {
               if(_type==null)return loadArray(querySelect    ,-1,null);
               else           return loadArray(querySelectType,-1,null);
       }
       @Override
       public lMessage loadArray(int _uid) {
              ArrayList<lMessage> list;

              if(_uid<0)list=loadArray(querySelect  ,-1  ,null);
              else      list=loadArray(querySelectID,_uid,null);

              if(list.size()>0)return list.get(0);

              logger.error("msg not find for uid:"+_uid);

              return new lMessage();
       }
       @Override
       public lMessage loadArrayX509(int _x509_id) {
           ArrayList<lMessage> list;

           if(_x509_id<0)list=loadArrayX509(querySelectX509  ,-1      ,null);
           else          list=loadArrayX509(querySelectX509ID,_x509_id,null);

           if(list.size()>0)return list.get(0);

           logger.error("msg not find for x509_id:"+_x509_id);
           return new lMessage();
       }
       @Override
       public JSONObject loadJSONX509(String _type) {
           //logger.trace("querySelectX509:"+querySelectX509);
           if(_type==null)return  loadJSONX509(querySelectX509     ,-1,null);
           else           return  loadJSONX509(querySelectX509Type ,-1,_type);
       }
       @Override
       public JSONObject loadJSONX509(int _uid) {
              if(_uid<0)return  loadJSONX509(querySelect   ,-1,null);
              else      return  loadJSONX509(querySelectID ,_uid,null);
       }
       @Override
       public JSONObject loadJSON(String _type) {
              if(_type==null)return  loadJSON(querySelect     ,-1,null);
              else           return  loadJSON(querySelectType ,-1,_type);
       }
       @Override
       public JSONObject loadJSON(int _uid) {
              if(_uid<0)return  loadJSON(querySelect   ,-1,null);
              else      return  loadJSON(querySelectID ,_uid,null);
       }
       @Override
       public void save(lMessage msg) {
              if(db==null){
                 logger.error("db is not init");
                 return;
              }
              else{
                 int    x509_id;
                 try{
                    logger.trace("begin save to db ");

                    x509_id=searchX509ID(msg);
                    if(x509_id<0) {
                       logger.trace("no search in db_x509 create new record");
                       x509_id=getNewX509ID();
                       msg.setX509ID(x509_id);
                       saveX509(msg);
                       saveSerialX509(msg);
                    }
                    else logger.trace("search in db_x509 X509ID:"+x509_id);

                    msg.setX509ID(x509_id);
                    saveEml(msg);
                 }
                 catch(dbExcept ex){
                      logger.error("save to db ex:"+ex);
                 }
                 catch(Exception ex1){
                       logger.error("save to db ex:"+new Except(ex1));
                 }
              }
              logger.info("-- "+msg.toString());
       }

       @Override
       public JSONObject loadJSONAlarm(Timestamp alarm,Timestamp curent) {
               JSONObject root=new JSONObject();
               if(db==null){
                  logger.error("db is not init");
                  return root;
               }
               else{
                  JSONArray list=new JSONArray();
                  query           q=null;
                  //logger.trace("db search alarm t1:"+alarm+" t2:"+curent+" sql:"+querySelectX509ALARM);
                  try{
                      // 
                       q=db.open();
                       q.creatPreSt(querySelectX509ALARM);
                       
                       q.setTimestamp( 1,curent);
                       q.setTimestamp( 2,curent);
                       q.setTimestamp( 3,alarm);
 
                       q.executeQuery();
                       int s=0;
                       while(q.isNextResult()) {
                            //lMessage msg=getAll(q);
                            lMessage msg=getX509(q);

                            logger.trace("alarm:"+alarm+ " begin:"+msg._getX509BeginDate()+" end:"+msg._getX509EndDate  ()+" SR:"+msg.getX509DateSR()+" FR:"+msg.getX509DateFR());  

                            JSONObject obj=lMessage2JSON.MSG2OBJ(msg);
                            list.put(s,obj);
                            s++;
                       } 
                       //logger.trace("db search alarm t1:"+alarm+" t2:"+curent+" size:"+s);
                       root.put("list",list);
                       root.put("size",s);
                  }catch(dbExcept ex){
                       if(q!=null){
                          logger.error("db q_id:"+q.getId()+" error sql:"+querySelectX509ALARM);
                          logger.error("db q_id:"+q.getId()+" ex:"+ex);
                       }
                       else logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
               }
               return root;
       }

       @Override
       public ArrayList<lMessage> loadArrayAlarm(Timestamp alarm,Timestamp curent) {
               ArrayList<lMessage> list=new ArrayList<lMessage>(100);
               if(db==null){
                  logger.error("db is not init");
                  return list;
               }
               else{
                  query q=null;
                  try{
                       q=db.open();
                       logger.trace("open db q_id:"+q.getId());
                       q.creatPreSt(querySelectX509ALARMID);
                       q.setTimestamp( 1,curent);
                       q.setTimestamp( 2,curent);
                       q.setTimestamp( 3,alarm);
                       q.executeQuery();
                       while(q.isNextResult()) {
                            lMessage msg=getX509(q);
                            list.add(msg);
                       } 
                  }catch(dbExcept ex){
                       if(q!=null){
                          logger.error("db q_id:"+q.getId()+" error sql:"+querySelectX509ALARMID);
                          logger.error("db q_id:"+q.getId()+" ex:"+ex);
                       }
                       else logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
               }
               return list;
       }
       @Override
       public void setSend(int _x509_id) {
           if(db==null){
               logger.error("db is not init");
               return ;
            }
            else{
               query  q=null;
               try{
                   q=db.open();
                   q.creatPreSt(queryUpdateFR);
                   q.setTimestamp( 1,new Timestamp(new Date().getTime())); /**/
                   q.setInt      ( 2,_x509_id); 
                   q.execute();
               }
               catch(dbExcept ex){
                     if(q!=null){
                        logger.error("db q_id:"+q.getId()+" error sql:"+queryUpdateFR);
                        logger.error("db q_id:"+q.getId()+" ex:"+ex);
                     }
                     else logger.error("db q_id:? ex:"+ex);
               }
               finally{
                    db.close(q);
               }
            }
       }

       private int setEml(query q,lMessage msg) throws dbExcept {
                int s=0;
                try {                                               
                      q.setString   ( 1+s,msg.getId            ()); 
                      q.setString   ( 2+s,msg.getFrom          ()); 
                      q.setString   ( 3+s,msg.getTOs           ()); 
                      q.setString   ( 4+s,msg.getSubject       ()); 
                      q.setString   ( 5+s,msg.getFilename      ()); 
                      q.setTimestamp( 6+s,msg._getCreateDate   ()); 
                      q.setTimestamp( 7+s,msg._getSentDate     ()); 
                      q.setTimestamp( 8+s,msg._getReceiveDate  ()); 
                      q.setInt      ( 9+s,msg.getSize          ()); 
                      q.setString   (10+s,msg.getBodyTxt       ()); 
                      q.setInt      (11+s,msg.getX509ID        ()); 
                                                              
                } catch (dbExcept ex1) {   
                        logger.error("setEml ex:"+ex1);
                        throw new dbExcept("setEml",ex1);
                }
                catch (Exception ex2) {
                        logger.error("setEml ex:"+ex2);
                        throw new dbExcept("setEml",ex2);
                }

                return 14+s;
       }
       private int _setX509(query q,lMessage msg) throws dbExcept {
           int s=0;
           try {                                               
                                            
                 q.setString   ( 1+s,msg.getX509Type      ()); 
                 q.setString   ( 2+s,msg.getX509TypeFile  ()); 
                 q.setTimestamp( 3+s,msg._getX509BeginDate()); 
                 q.setTimestamp( 4+s,msg._getX509EndDate  ()); 
                 q.setString   ( 5+s,msg.getX509Serials   ()); 
                 q.setString   ( 6+s,msg.getX509Subject   ()); 
                 q.setString   ( 7+s,msg.getX509Issuer    ()); 
                 q.setString   ( 8+s,msg.getBodyBin64     ()); 
                                                         
           } catch (dbExcept ex1) {   
                   logger.error("setX509 ex:"+ex1);
                   throw new dbExcept("setX509",ex1);
           }
           catch (Exception ex2) {
                   logger.error("setX509 ex:"+ex2);
                   throw new dbExcept("setX509",ex2);
           }

           return 9+s;
       }
       private int setX509(query q,lMessage msg) throws dbExcept {
           int s=0;
           try {                                               
                                            
                 q.setInt      ( 1+s,msg.getX509ID        ()); 
                 q.setString   ( 2+s,msg.getX509Type      ()); 
                 q.setString   ( 3+s,msg.getX509TypeFile  ()); 
                 q.setTimestamp( 4+s,msg._getX509BeginDate()); 
                 q.setTimestamp( 5+s,msg._getX509EndDate  ()); 
                 q.setString   ( 6+s,msg.getX509Serials   ()); 
                 q.setString   ( 7+s,msg.getX509Subject   ()); 
                 q.setString   ( 8+s,msg.getX509Issuer    ()); 
                 q.setString   ( 9+s,msg.getBodyBin64     ()); 
                                                         
           } catch (dbExcept ex1) {   
                   logger.error("setX509 ex:"+ex1);
                   throw new dbExcept("setX509",ex1);
           }
           catch (Exception ex2) {
                   logger.error("setX509 ex:"+ex2);
                   throw new dbExcept("setX509",ex2);
           }

           return 9+s;
       }
       private int setSerialN(query q,lMessage msg,int i) throws dbExcept {
           int s=0;
           try {                                               //  CREATE TABLE (
                 q.setInt      ( 1+s,msg.getX509ID     ()); //  MAIL_NUM          INTEGER,
                 q.setString   ( 2+s,msg.getX509SerialN(i)); //  X509_SERIAL       VARCHAR(4096), -- -> new table
                                                         
           } catch (dbExcept ex1) {   
                   logger.error("setSerial ex:"+ex1);
                   throw new dbExcept("setSerial",ex1);
           }
           catch (Exception ex2) {
                   logger.error("setSerial ex:"+ex2);
                   throw new dbExcept("setSerial",ex2);
           }

           return 2+s;
       }

       
       private lMessage getAll(query q) throws dbExcept{
                int s=0;
                lMessage msg=new lMessage();

                try {
                     msg.setUID           (q.Result().getInt      ( 1+s)); 
                     msg.setId            (q.Result().getString   ( 2+s)); 
                     msg.setFrom          (q.Result().getString   ( 3+s)); 
                     msg.setTO            (q.Result().getString   ( 4+s)); 
                     msg.setSubject       (q.Result().getString   ( 5+s)); 
                     msg.setFilename      (q.Result().getString   ( 6+s)); 
                     msg.setCreateDate    (q.Result().getTimestamp( 7+s)); 
                     msg.setSentDate      (q.Result().getTimestamp( 8+s)); 
                     msg.setReceiveDate   (q.Result().getTimestamp( 9+s)); 
                     msg.setSize          (q.Result().getInt      (10+s)); 
                     msg.setBodyTxt       (q.Result().getString   (11+s)); 
                     msg.setX509ID        (q.Result().getInt      (12+s)); 
                                                              
                     msg.setX509ID        (q.Result().getInt      (12+ 1+s)); 
                     msg.setX509Type      (q.Result().getString   (12+ 2+s)); 
                     msg.setX509TypeFile  (q.Result().getString   (12+ 3+s)); 
                     msg.setX509BeginDate (q.Result().getTimestamp(12+ 4+s)); 
                     msg.setX509EndDate   (q.Result().getTimestamp(12+ 5+s)); 
                     msg.setX509Serial    (q.Result().getString   (12+ 6+s)); 
                     msg.setX509Subject   (q.Result().getString   (12+ 7+s)); 
                     msg.setX509Issuer    (q.Result().getString   (12+ 8+s)); 
                     msg.setX509DateSR    (q.Result().getTimestamp(12+ 9+s)); 
                     msg.setX509DateFR    (q.Result().getTimestamp(12+10+s)); 
                     msg.setBodyBin64     (q.Result().getString   (12+11+s)); 
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }
                                                              

                return msg;
       }
        
       private lMessage getX509(query q) throws dbExcept{
                int s=0;
                lMessage msg=new lMessage();

                try {
                     msg.setX509ID        (q.Result().getInt      ( 1+s)); 
                     msg.setX509Type      (q.Result().getString   ( 2+s)); 
                     msg.setX509TypeFile  (q.Result().getString   ( 3+s)); 
                     msg.setX509BeginDate (q.Result().getTimestamp( 4+s)); 
                     msg.setX509EndDate   (q.Result().getTimestamp( 5+s)); 
                     msg.setX509Serial    (q.Result().getString   ( 6+s)); 
                     msg.setX509Subject   (q.Result().getString   ( 7+s)); 
                     msg.setX509Issuer    (q.Result().getString   ( 8+s)); 
                     msg.setX509DateSR    (q.Result().getTimestamp( 9+s)); 
                     msg.setX509DateFR    (q.Result().getTimestamp(10+s)); 
                     msg.setBodyBin64     (q.Result().getString   (11+s)); 
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }

                return msg;
       }
         
       
       private ArrayList<lMessage> loadArray(String _select,int _uid,String _type) {
               ArrayList<lMessage> list=new ArrayList<lMessage>(100);
               if(db==null){
                  logger.error("db is not init");
                  return list;
               }
               else{
                  query q=null;
                  try{
                       q=db.open();
                       logger.trace("open db q_id:"+q.getId());
              
                       q.creatPreSt(_select);
                       if(_uid>0     )q.setInt    ( 1,_uid);
                       if(_type!=null)q.setString ( 1,_type);
              
                       q.executeQuery();
                       while(q.isNextResult()) {
                            lMessage msg=getAll(q);
                            list.add(msg);
                            if(_uid>0)break;
                       } 
              
                  }catch(dbExcept ex){
                       if(q!=null){
                          logger.error("db q_id:"+q.getId()+" error sql:"+_select);
                          logger.error("db q_id:"+q.getId()+" ex:"+ex);
                       }
                       else logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
               }
               return list;
       }
       private ArrayList<lMessage> loadArrayX509(String _select,int _x509_id,String _type) {
               ArrayList<lMessage> list=new ArrayList<lMessage>(100);
               if(db==null){
                  logger.error("db is not init");
               }
               else{
                  query q=null;
                  try{
                      // 
                       q=db.open();
                       logger.trace("open db q_id:"+q.getId());
              
                       q.creatPreSt(_select);
                       if(_x509_id>0 )q.setInt    ( 1,_x509_id);
                       if(_type!=null)q.setString ( 1,_type);
              
                       q.executeQuery();
                       while(q.isNextResult()) {
                            lMessage msg=getX509(q);
                            list.add(msg);
                            if(_x509_id>0)break;
                       } 
              
                  }catch(dbExcept ex){
                       if(q!=null){
                          logger.error("db q_id:"+q.getId()+" error sql:"+_select);
                          logger.error("db q_id:"+q.getId()+" ex:"+ex);
                       }
                       else logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
               }
               return list;
       }

       private JSONObject loadJSON(String _select,int _uid,String _type) {
               JSONObject root=new JSONObject();
               if(db==null){
                  logger.error("db is not init");
                  return root;
               }
               else{
                  JSONArray list=new JSONArray();
                  query           q=null;
                  try{
                      // 
                       q=db.open();
                       q.creatPreSt(_select);
                       if(_uid>0)     q.setInt    ( 1,_uid);
                       if(_type!=null)q.setString ( 1,_type);
 
                       q.executeQuery();
                       int s=0;
                       while(q.isNextResult()) {
                            lMessage msg=getAll(q);
                            JSONObject obj=lMessage2JSON.MSG2OBJ(msg);
                            list.put(s,obj);
                            if(_uid>0)break;
                            s++;
                       } 
                       root.put("list",list);
                       root.put("size",s);
                  }catch(dbExcept ex){
                       if(q!=null){
                          logger.error("db q_id:"+q.getId()+" error sql:"+_select);
                          logger.error("db q_id:"+q.getId()+" ex:"+ex);
                       }
                       else logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
               }
               return root;
       }
       private JSONObject loadJSONX509(String _select,int _uid,String _type) {

               //logger.trace("select:"+_select+" _uid:"+ _uid+" type:"+ _type);

               JSONObject root=new JSONObject();
               if(db==null){
                  logger.error("db is not init");
                  return root;
               }
               else{
                  JSONArray list=new JSONArray();
                  query           q=null;
                  try{
                      // 
                       q=db.open();
                       q.creatPreSt(_select);
                       if(_uid>0)     q.setInt    ( 1,_uid);
                       if(_type!=null)q.setString ( 1,_type);
              
                       q.executeQuery();
                       int s=0;
                       while(q.isNextResult()) {
                            lMessage msg=getX509(q);
                            JSONObject obj=lMessage2JSON.MSG2OBJ(msg);
                            list.put(s,obj);
                            if(_uid>0)break;
                            s++;
                       } 
                       root.put("list",list);
                       root.put("size",s);
                  }catch(dbExcept ex){
                       if(q!=null){
                          logger.error("db q_id:"+q.getId()+" error sql:"+_select);
                          logger.error("db q_id:"+q.getId()+" ex:"+ex);
                       }
                       else logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
               }
               return root;
       }

       private int searchX509ID(lMessage msg)  throws dbExcept{
               query  q=null;
               try{
                   q=db.open();
                   q.creatPreSt(querySelectX509SEARCHID);
                   _setX509(q,msg);
                   q.executeQuery();
                   while(q.isNextResult()) {
                         try {
                              msg.setX509ID(q.Result().getInt(1));
                              logger.trace("find OK X509ID:"+msg.getX509ID());
                              break;
                         } catch (SQLException e) {
                              if(q!=null){
                                 logger.error("db q_id:"+q.getId()+" ex:"+new dbExcept ("get X509ID",e));
                              }
                              msg.setX509ID(-1);
                              logger.trace("find BAD X509ID:"+msg.getX509ID());
                              break;
                         } 
                   }
               }
               finally{
                    db.close(q);
               }
               logger.trace("find X509ID:"+msg.getX509ID());
               return msg.getX509ID();
       }
       private int getNewX509ID() throws dbExcept {
               query  q=null;
               int    x509_id=-1;
               try{
                   q=db.open();
                   q.creatPreSt(queryGetID);
                   q.executeQuery();
                   while(q.isNextResult()) {
                         try {
                             x509_id=q.Result().getInt(1);
                             break;
                         } catch (SQLException e) {
                             if(q!=null){
                                logger.error("db q_id:"+q.getId()+" ex:"+new dbExcept ("get X509ID",e));
                             }
                             logger.trace("create new X509ID:"+x509_id);
                             return x509_id;
                         } 
                   }
               }
               finally{
                    db.close(q);
               }
               logger.trace("get new X509ID:"+x509_id);
               return x509_id;
       }



       private void saveX509(lMessage msg) throws dbExcept {
               query  q=null;
               try{
                   q=db.open();
                   q.creatPreSt(queryInsertX509);
                   setX509(q,msg);
                   q.execute();
               }
               finally{
                    db.close(q);
               }
               //logger.trace("insert x509 to db q_id:"+q.getId()+" X509ID:"+msg.getX509ID());
               logger.trace("saveX509 ID:"+msg.getX509ID());

       }
       private void saveSerialX509(lMessage msg) throws dbExcept {
                        
               if(!lMessageX509.X509_CRL.equals(msg.getX509Type()))return;

               query  q1=null;
               query  q2=null;
               try{
                   q1=db.open();
                   q2=db.open();
                   q1.creatPreSt(queryInsertSerialR);
                   q2.creatPreSt(queryUpdateRL);
                   for(int i=0;i<msg.getX509SizeSerial();i++) {
                       setSerialN(q1,msg,i);
                       q1.execute();// insert in table

                       q2.setTimestamp(1,msg._getX509BeginDate()); 
                       q2.setString   (2,msg.getX509SerialN(i)   ); 
                       q2.execute();// update table with SR
                   }
               }
               finally{
                    db.close(q1);
                    db.close(q2);
               }
               //logger.trace("insert serial("+msg.getX509SizeSerial()+") to db");
               logger.trace("saveSerialX509 ID:"+msg.getX509ID());
       }
       private void saveEml(lMessage msg) throws dbExcept {
               query  q=null;
               try{
                   q=db.open();
                   q.creatPreSt(queryInsertEml);
                   setEml(q,msg);
                   q.execute();
                   logger.trace("save Eml to  db");
               }
               finally{
                    db.close(q);
               }
       }

       @Override
       public lMessage  loadSRL(int   _id   ) {



              return new lMessage();             
       }


}
