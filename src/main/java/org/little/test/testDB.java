package org.little.test;
             
import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;
 
public class testDB{
       private static final Logger logger = LoggerFactory.getLogger(testDB.class);

       public static void main(String args[]){
              connection_pool db=null;
              db=new connection_pool();

              db.init("org.apache.derby.jdbc.ClientDriver","jdbc:derby://localhost:1527/xe;create=true","av","123");
              String queryCreate = "CREATE TABLE arh_key ( "+
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
                 logger.trace("create table arh_key db q_id:"+q.getId());


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
}
