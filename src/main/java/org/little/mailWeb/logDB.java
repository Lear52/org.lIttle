package org.little.mailWeb;
             
import org.little.lmsg.lMessage;
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;

public class logDB implements logKeyArh{
       private static final Logger logger = LoggerFactory.getLogger(logDB.class);
       private static connection_pool db=null;
       private static String queryInsert;
       private static String queryCreate;

       private void init_db(commonDB cfg){
               if(db!=null)return;
               db=new connection_pool();
               db.init(cfg.getDrv(),cfg.getURL(),cfg.getUser(),cfg.getPasswd());

               String fields_select="NUM,UID,FROM,ID,SUBJECT,FILENAME,TO,CREATE_DATE,SENT_DATE,RECEIVE_DATE,DEL_DATE,ANS_DATE,MIME,UID,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER,X509_TXT,X509_BIN";
               String fields_insert="?  ,?  ,?   ,? ,?      ,?       ,? ,?          ,?        ,?           ,?       ,?       ,?   ,?  ,?        ,?             ,?              ,?            ,?          ,?           ,?          ,?       ,? ";
               queryInsert = "INSERT INTO "+cfg.getTable()+" ( "+fields_select+" ) VALUES ("+fields_insert+" ) ";
               queryCreate = "CTEATE TABLE "+cfg.getTable()+" ( "+
                             "NUM               INTEGER,"+      
                             "UID               INTEGER,"+      
                             "FROM              VARCHAR(128),"+ 
                             "ID                VARCHAR(128),"+ 
                             "SUBJECT           VARCHAR(128),"+ 
                             "FILENAME          VARCHAR(128),"+ 
                             "TO                VARCHAR(256),"+ 
                             "CREATE_DATE       DATE,"+         
                             "SENT_DATE         DATE,"+         
                             "RECEIVE_DATE      DATE,"+         
                             "DEL_DATE          DATE,"+         
                             "ANS_DATE          DATE,"+         
                             "MIME              VARCHAR(128),"+ 
                             "UID               INTEGER,"+      
                             "X509_TYPE         VARCHAR(32),"+ 
                             "X509_TYPE_FILE    VARCHAR(32),"+  
                             "X509_BEGIN_DATE   DATE,"+         
                             "X509_END_DATE     DATE,"+         
                             "X509_SERIAL       VARCHAR(4096),"+
                             "X509_SUBJECT      VARCHAR(256), "+
                             "X509_ISSUER       VARCHAR(256), "+
                             "X509_TXT          VARCHAR(4096),"+
                             "X509_BIN          VARCHAR(4096) "+
                             ")";

              query q=null;
              try {
                 q=db.open();
                 logger.trace("open db q_id:"+q.getId());
                 q.execute(queryCreate);

              } catch (dbExcept ex1) {   
                      logger.error("create table ex:"+ex1);
              }
              catch (Exception ex2) {
                      logger.error("create table  ex:"+ex2);
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
       }

       private int setAll(query q,lMessage msg) throws dbExcept {
                int s=0;
                try {                                              //  CTEATE TABLE (
                      q.setInt      ( 1+s,msg.getNum          ()); //  NUM               INTEGER,
                      q.setInt      ( 2+s,msg.getUID          ()); //  UID               INTEGER,
                      q.setString   ( 3+s,msg.getFrom         ()); //  FROM              VARCHAR(128),
                      q.setString   ( 4+s,msg.getId           ()); //  ID                VARCHAR(128),
                      q.setString   ( 5+s,msg.getSubject      ()); //  SUBJECT           VARCHAR(128),
                      q.setString   ( 6+s,msg.getFilename     ()); //  FILENAME          VARCHAR(128),
                      q.setString   ( 7+s,msg.getTOs          ()); //  TO                VARCHAR(256), -- -> new table
                      q.setDate     ( 8+s,msg._getCreateDate   ()); //  CREATE_DATE       DATE,
                      q.setDate     ( 9+s,msg._getSentDate     ()); //  SENT_DATE         DATE,
                      q.setDate     (10+s,msg._getReceiveDate  ()); //  RECEIVE_DATE      DATE,
                      q.setDate     (11+s,msg._getDelDate      ()); //  DEL_DATE          DATE,
                      q.setDate     (12+s,msg._getAnswerDate   ()); //  ANS_DATE          DATE,
                      q.setString   (13+s,msg.getMime         ()); //  MIME              VARCHAR(128),
                      q.setInt      (14+s,msg.getSize         ()); //  UID               INTEGER,
                                                 
                      q.setString   (15+s,msg.getX509Type     ()); //  X509_TYPE         VARCHAR(32),
                      q.setString   (16+s,msg.getX509TypeFile ()); //  X509_TYPE_FILE    VARCHAR(32),
                      q.setDate     (17+s,msg._getX509BeginDate()); //  X509_BEGIN_DATE   DATE,
                      q.setDate     (18+s,msg._getX509EndDate  ()); //  X509_END_DATE     DATE,
                      q.setString   (19+s,msg.getX509Serial   ()); //  X509_SERIAL       VARCHAR(4096), -- -> new table
                      q.setString   (20+s,msg.getX509Subject  ()); //  X509_SUBJECT      VARCHAR(256),
                      q.setString   (21+s,msg.getX509Issuer   ()); //  X509_ISSUER       VARCHAR(256),
                                                 
                      q.setString   (22+s,msg.getBodyTxt      ()); //  X509_TXT          VARCHAR(4096), -- -> new table
                      q.setString   (23+s,msg.getBodyBin64    ()); //  X509_BIN          VARCHAR(4096)  -- -> new table

                } catch (dbExcept ex1) {   
                        logger.error("setAll ex:"+ex1);
                        throw new dbExcept("setAll",ex1);
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }

                return 20+s;
        }

       public void print(lMessage msg) {
              query           q=null;
              try{
                  // 
                   q=db.open();
                   logger.trace("open db q_id:"+q.getId());
                   q.creatPreSt(queryInsert);
                   setAll(q,msg);
                   q.executeQuery();

              }catch(dbExcept ex){
                   if(q!=null)logger.error("db q_id:"+q.getId()+" ex:"+ex);
                   else       logger.error("db q_id:? ex:"+ex);
              }
              finally{
                   db.close(q);
              }
              logger.info("-- "+msg.toString());
       }


       public static void main(String args[]){
              logger.info("START");

       }
}
