package org.little.mailWeb;

import java.util.ArrayList;
             
import org.json.JSONArray;
import org.json.JSONObject;
import org.little.lmsg.lMessage;
import org.little.lmsg.lMessage2JSON;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;

public class logDB implements logKeyArh{
       private static final Logger    logger = LoggerFactory.getLogger(logDB.class);
       private static connection_pool db=null;
       private static String          queryInsert;
       private static String          queryCreate;
       private static String          querySelect;
       private static String          querySelect0;

       private void init_db(commonDB cfg){
               if(db!=null)return;
               logger.trace("begin init_db");

               db=new connection_pool();
               db.init(cfg.getDrv(),cfg.getURL(),cfg.getUser(),cfg.getPasswd());

               logger.trace("init connection_pool db");

               String fields_select0="ADDR_FROM,SUBJECT,FILENAME,ADDR_TO,CREATE_DATE,SENT_DATE,RECEIVE_DATE,DEL_DATE,ANS_DATE,ATTACH_SIZE,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER";
               String fields_select="MAIL_NUM,MAIL_UID,ADDR_FROM,MAIL_ID,SUBJECT,FILENAME,ADDR_TO,CREATE_DATE,SENT_DATE,RECEIVE_DATE,DEL_DATE,ANS_DATE,MIME,ATTACH_SIZE,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER,X509_TXT,X509_BIN";
                                   //1       2        3          4       5       6        7       8           9         10           11       12       13   14         15         16             17              18            19          20           21          22       23
               String fields_insert="?      ,?       ,?         ,?      ,?      ,?       ,?      ,?          ,?        ,?           ,?       ,?       ,?   ,?         ,?         ,?             ,?              ,?            ,?          ,?           ,?          ,?       ,? ";
               queryInsert  = "INSERT INTO "+cfg.getTable()+" ( "+fields_select+" ) VALUES ("+fields_insert+" ) ";
               querySelect  = "SELECT "+fields_select+" FROM "+cfg.getTable()+" ORDER BY CREATE_DATE ";
               querySelect0 = "SELECT "+fields_select0+" FROM "+cfg.getTable()+" ORDER BY CREATE_DATE ";
               queryCreate  = "CREATE TABLE "+cfg.getTable()+" ( "+
                             "MAIL_NUM          INTEGER,"+      
                             "MAIL_UID          INTEGER,"+      
                             "ADDR_FROM         VARCHAR(512),"+ 
                             "MAIL_ID           VARCHAR(512),"+ 
                             "SUBJECT           VARCHAR(2048),"+ 
                             "FILENAME          VARCHAR(512),"+ 
                             "ADDR_TO           VARCHAR(8192),"+ 
                             "CREATE_DATE       DATE,"+         
                             "SENT_DATE         DATE,"+         
                             "RECEIVE_DATE      DATE,"+         
                             "DEL_DATE          DATE,"+         
                             "ANS_DATE          DATE,"+         
                             "MIME              VARCHAR(256),"+ 
                             "ATTACH_SIZE       INTEGER,"+ 
                             "X509_TYPE         VARCHAR(64),"+ 
                             "X509_TYPE_FILE    VARCHAR(64),"+  
                             "X509_BEGIN_DATE   DATE,"+         
                             "X509_END_DATE     DATE,"+         
                             "X509_SERIAL       VARCHAR(8192),"+
                             "X509_SUBJECT      VARCHAR(8192), "+
                             "X509_ISSUER       VARCHAR(8192), "+
                             "X509_TXT          VARCHAR(8192),"+
                             "X509_BIN          VARCHAR(8192) "+
                             ")";

              query q=null;
              try {
                 q=db.open();
                 logger.trace("open db q_id:"+q.getId());
                 q.execute(queryCreate);
                 logger.trace("create tabl:"+cfg.getTable());

              } catch (dbExcept ex1) {   
                 logger.error("create table ex:"+ex1);
                 logger.trace("tabl:"+cfg.getTable());
              }
              catch (Exception ex2) {
                 logger.error("create table  ex:"+ex2);
              }
              finally{
                   db.close(q);
              }

       }

       public logDB() {}

       @Override
       public synchronized void open(commonDB cfg) {
              init_db(cfg);

       }

       @Override
       public synchronized void close() {
              if(db!=null){try {db.close();} catch (dbExcept e) {}} 
              db=null;
              logger.trace("close db");
       }

       private int setAll(query q,lMessage msg) throws dbExcept {
                int s=0;
                try {                                              //  CREATE TABLE (
                      q.setInt      ( 1+s,msg.getNum           ()); //  MAIL_NUM          INTEGER,
                      q.setInt      ( 2+s,msg.getUID           ()); //  MAIL_UID          INTEGER,
                      q.setString   ( 3+s,msg.getFrom          ()); //  ADDR_FROM         VARCHAR(128),
                      q.setString   ( 4+s,msg.getId            ()); //  MAIL_ID           VARCHAR(128),
                      q.setString   ( 5+s,msg.getSubject       ()); //  SUBJECT           VARCHAR(128),
                      q.setString   ( 6+s,msg.getFilename      ()); //  FILENAME          VARCHAR(128),
                      q.setString   ( 7+s,msg.getTOs           ()); //  ADDR_TO           VARCHAR(256), -- -> new table
                      q.setDate     ( 8+s,msg._getCreateDate   ()); //  CREATE_DATE       DATE,
                      q.setDate     ( 9+s,msg._getSentDate     ()); //  SENT_DATE         DATE,
                      q.setDate     (10+s,msg._getReceiveDate  ()); //  RECEIVE_DATE      DATE,
                      q.setDate     (11+s,msg._getDelDate      ()); //  DEL_DATE          DATE,
                      q.setDate     (12+s,msg._getAnswerDate   ()); //  ANS_DATE          DATE,
                      q.setString   (13+s,msg.getMime          ()); //  MIME              VARCHAR(128),
                      q.setInt      (14+s,msg.getSize          ()); //  ATTACH_SIZE       INTEGER,
                                                 
                      q.setString   (15+s,msg.getX509Type      ()); //  X509_TYPE         VARCHAR(32),
                      q.setString   (16+s,msg.getX509TypeFile  ()); //  X509_TYPE_FILE    VARCHAR(32),
                      q.setDate     (17+s,msg._getX509BeginDate()); //  X509_BEGIN_DATE   DATE,
                      q.setDate     (18+s,msg._getX509EndDate  ()); //  X509_END_DATE     DATE,
                      q.setString   (19+s,msg.getX509Serial    ()); //  X509_SERIAL       VARCHAR(4096), -- -> new table
                      q.setString   (20+s,msg.getX509Subject   ()); //  X509_SUBJECT      VARCHAR(256),
                      q.setString   (21+s,msg.getX509Issuer    ()); //  X509_ISSUER       VARCHAR(256),
                                                              
                      q.setString   (22+s,msg.getBodyTxt       ()); //  X509_TXT          VARCHAR(4096), -- -> new table
                      q.setString   (23+s,msg.getBodyBin64     ()); //  X509_BIN          VARCHAR(4096)  -- -> new table
                                                              
                } catch (dbExcept ex1) {   
                        logger.error("setAll ex:"+ex1);
                        throw new dbExcept("setAll",ex1);
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }

                return 23+s;
        }
        private lMessage getAll(query q) throws dbExcept{
                int s=0;
                lMessage msg=new lMessage();

                try {
                     msg.setNum           (q.Result().getInt      ( 1+s)); 
                     msg.setUID           (q.Result().getInt      ( 2+s)); 
                     msg.setFrom          (q.Result().getString   ( 3+s)); 
                     msg.setId            (q.Result().getString   ( 4+s)); 
                     msg.setSubject       (q.Result().getString   ( 5+s)); 
                     msg.setFilename      (q.Result().getString   ( 6+s)); 
                     msg.setTO            (q.Result().getString   ( 7+s)); 
                     msg.setCreateDate    (q.Result().getDate     ( 8+s)); 
                     msg.setSentDate      (q.Result().getDate     ( 9+s)); 
                     msg.setReceiveDate   (q.Result().getDate     (10+s)); 
                     msg.setDelDate       (q.Result().getDate     (11+s)); 
                     msg.setAnswerDate    (q.Result().getDate     (12+s)); 
                     msg.setMime          (q.Result().getString   (13+s)); 
                     msg.setSize          (q.Result().getInt      (14+s)); 
                                                              
                     msg.setX509Type      (q.Result().getString   (15+s)); 
                     msg.setX509TypeFile  (q.Result().getString   (16+s)); 
                     msg.setX509BeginDate (q.Result().getDate     (17+s)); 
                     msg.setX509EndDate   (q.Result().getDate     (18+s)); 
                     msg.setX509Serial    (q.Result().getString   (19+s)); 
                     msg.setX509Subject   (q.Result().getString   (20+s)); 
                     msg.setX509Issuer    (q.Result().getString   (21+s)); 
                                                              
                     msg.setBodyTxt       (q.Result().getString   (22+s)); 
                     msg.setBodyBin64     (q.Result().getString   (23+s)); 
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }
                                                              

                return msg;
        }
        private lMessage get0All(query q) throws dbExcept{
                int s=0;
                lMessage msg=new lMessage();

                try {
                     msg.setFrom          (q.Result().getString   ( 1+s)); 
                     msg.setSubject       (q.Result().getString   ( 2+s)); 
                     msg.setFilename      (q.Result().getString   ( 3+s)); 
                     msg.setTO            (q.Result().getString   ( 4+s)); 
                     msg.setCreateDate    (q.Result().getDate     ( 5+s)); 
                     msg.setSentDate      (q.Result().getDate     ( 6+s)); 
                     msg.setReceiveDate   (q.Result().getDate     ( 7+s)); 
                     msg.setDelDate       (q.Result().getDate     ( 8+s)); 
                     msg.setAnswerDate    (q.Result().getDate     ( 9+s)); 
                     msg.setSize          (q.Result().getInt      (10+s)); 
                     msg.setX509Type      (q.Result().getString   (11+s)); 
                     msg.setX509TypeFile  (q.Result().getString   (12+s)); 
                     msg.setX509BeginDate (q.Result().getDate     (13+s)); 
                     msg.setX509EndDate   (q.Result().getDate     (14+s)); 
                     msg.setX509Serial    (q.Result().getString   (15+s)); 
                     msg.setX509Subject   (q.Result().getString   (16+s)); 
                     msg.setX509Issuer    (q.Result().getString   (17+s)); 
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }
                                                              

                return msg;
        }

        @Override
        public ArrayList<lMessage> loadArrey() {
              ArrayList<lMessage> list=new ArrayList<lMessage>(100);
              if(db==null){
                 logger.error("db is not init");
              }
              else{
                 query           q=null;
                 try{
                     // 
                      q=db.open();
                      logger.trace("open db q_id:"+q.getId());

                      q.creatPreSt(querySelect);

                      q.executeQuery();
                      while(q.isNextResult()) {
                           lMessage msg=getAll(q);
                           list.add(msg);
                      } 

                 }catch(dbExcept ex){
                      if(q!=null){
                         logger.error("db q_id:"+q.getId()+" error sql:"+queryInsert);
                         logger.error("db q_id:"+q.getId()+" ex:"+ex);
                      }
                      else       logger.error("db q_id:? ex:"+ex);
                 }
                 finally{
                      db.close(q);
                 }
              }
              return list;
       }
       @Override
       public JSONObject loadJSON() {
              JSONObject root=new JSONObject();
              if(db==null){
                 logger.error("db is not init");
              }
              else{
                 JSONArray list=new JSONArray();
                 query           q=null;
                 try{
                     // 
                      q=db.open();
                      logger.trace("open db q_id:"+q.getId());

                      q.creatPreSt(querySelect0);

                      q.executeQuery();
                      int s=0;
                      while(q.isNextResult()) {
                           lMessage msg=get0All(q);
                           lMessage2JSON.MSG2OBJ(msg);
                           s++;
                      } 
                      root.put("list",list);
                      root.put("size",s);
                 }catch(dbExcept ex){
                      if(q!=null){
                         logger.error("db q_id:"+q.getId()+" error sql:"+queryInsert);
                         logger.error("db q_id:"+q.getId()+" ex:"+ex);
                      }
                      else       logger.error("db q_id:? ex:"+ex);
                 }
                 finally{
                      db.close(q);
                 }
              }
              return root;
       }
       @Override
       public void print(lMessage msg) {
              if(db==null){
                 logger.error("db is not init");
              }
              else{
                 query           q=null;
                 try{
                     // 
                      q=db.open();
                      logger.trace("open db q_id:"+q.getId());

                      q.creatPreSt(queryInsert);

                      setAll(q,msg);

                      q.execute();
                      //q.executeQuery();
                
                      logger.trace("insert to  db q_id:"+q.getId());
                
                 }catch(dbExcept ex){
                      if(q!=null){
                         logger.error("db q_id:"+q.getId()+" error sql:"+queryInsert);
                         logger.error("db q_id:"+q.getId()+" ex:"+ex);
                      }
                      else       logger.error("db q_id:? ex:"+ex);
                 }
                 finally{
                      db.close(q);
                 }
              }
              logger.info("-- "+msg.toString());
       }


       public static void main(String args[]){
              logger.info("START");

       }
}
