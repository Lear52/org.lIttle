package org.little.mailWeb;
             
import org.little.lmsg.lMessage;
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

       private void init_db(commonDB cfg){
               if(db!=null)return;
               logger.trace("begin init_db");

               db=new connection_pool();
               db.init(cfg.getDrv(),cfg.getURL(),cfg.getUser(),cfg.getPasswd());

               logger.trace("init connection_pool db");

               String fields_select="MAIL_NUM,MAIL_UID,ADDR_FROM,MAIL_ID,SUBJECT,FILENAME,ADDR_TO,CREATE_DATE,SENT_DATE,RECEIVE_DATE,DEL_DATE,ANS_DATE,MIME,ATTACH_SIZE,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER,X509_TXT,X509_BIN";
                                   //1       2        3          4       5       6        7       8           9         10           11       12       13   14         15         16             17              18            19          20           21          22       23
               String fields_insert="?      ,?       ,?         ,?      ,?      ,?       ,?      ,?          ,?        ,?           ,?       ,?       ,?   ,?         ,?         ,?             ,?              ,?            ,?          ,?           ,?          ,?       ,? ";
               queryInsert = "INSERT INTO "+cfg.getTable()+" ( "+fields_select+" ) VALUES ("+fields_insert+" ) ";
               queryCreate = "CREATE TABLE "+cfg.getTable()+" ( "+
                             "MAIL_NUM          INTEGER,"+      
                             "MAIL_UID          INTEGER,"+      
                             "ADDR_FROM         VARCHAR(128 CHAR),"+ 
                             "MAIL_ID           VARCHAR(128 CHAR),"+ 
                             "SUBJECT           VARCHAR(1024 CHAR),"+ 
                             "FILENAME          VARCHAR(256 CHAR),"+ 
                             "ADDR_TO           VARCHAR(1024 CHAR),"+ 
                             "CREATE_DATE       DATE,"+         
                             "SENT_DATE         DATE,"+         
                             "RECEIVE_DATE      DATE,"+         
                             "DEL_DATE          DATE,"+         
                             "ANS_DATE          DATE,"+         
                             "MIME              VARCHAR(128 CHAR),"+ 
                             "ATTACH_SIZE       INTEGER,"+ 
                             "X509_TYPE         VARCHAR(32 CHAR),"+ 
                             "X509_TYPE_FILE    VARCHAR(32 CHAR),"+  
                             "X509_BEGIN_DATE   DATE,"+         
                             "X509_END_DATE     DATE,"+         
                             "X509_SERIAL       VARCHAR(4096 CHAR),"+
                             "X509_SUBJECT      VARCHAR(1024 CHAR), "+
                             "X509_ISSUER       VARCHAR(1024 CHAR), "+
                             "X509_TXT          VARCHAR(4096 CHAR),"+
                             "X509_BIN          VARCHAR(4096 CHAR) "+
                             ")";

              query q=null;
              try {
                 q=db.open();
                 logger.trace("open db q_id:"+q.getId());
                 q.execute(queryCreate);
                 logger.trace("create tabl:"+cfg.getTable());

              } catch (dbExcept ex1) {   
                      //logger.error("create table ex:"+ex1);
                 logger.trace("tabl:"+cfg.getTable());
              }
              catch (Exception ex2) {
                      //logger.error("create table  ex:"+ex2);
              }
              finally{
                   db.close(q);
              }

       }

       public logDB() {}

       public synchronized void open(commonDB cfg) {
              init_db(cfg);

       }

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
