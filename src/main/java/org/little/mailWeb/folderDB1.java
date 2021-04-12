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

public class folderDB1 implements folderARH{
       private static final Logger    logger = LoggerFactory.getLogger(folderDB1.class);
       private static connection_pool db=null;

       private String                 queryInsert;
       private String                 queryCreate;
       private String                 querySelect;
       private String                 querySelect0;
       private String                 querySelectID;
       private String                 querySelectID0;
       private String                 querySelectType;
       private String                 querySelectType0;
       //private String                 querySelectX509;

       private String                 queryCreateSeq;
                                     
       private commonDB               cfg;

       public  folderDB1(commonDB _cfg){cfg=_cfg;}

       private boolean create(){
               boolean ret1=false;
               boolean ret2=false;

               if(db==null){
                  logger.error("db is not init");
                  return false;
               }
               query q=null;
               try {
                    q=db.open();
                    q.execute(queryCreateSeq);
                    ret1=true;
                    logger.trace("create sec:"+cfg.getSeq());
               } catch (dbExcept ex1) {   
                  if(logger.isTrace()) {
                     logger.error("create seq:"+cfg.getSeq()+" ex:"+ex1);
                  }
               } catch (Exception ex2) {
                  if(logger.isTrace()) {
                     logger.error("create seq:"+cfg.getSeq()+" ex:"+ex2);
                  }
               }
               finally{
                    db.close(q);
               }
               try {
                    q=db.open();
                    q.execute(queryCreate);
                    ret2=true;
                    logger.trace("create tabl:"+cfg.getTableEml());
               } catch (dbExcept ex1) {   
                  if(logger.isTrace()) {
                     logger.error("create table:"+cfg.getTableEml()+" ex:"+ex1);
                  }
               } catch (Exception ex2) {
                  if(logger.isTrace()) {
                     logger.error("create table:"+cfg.getTableEml()+" ex:"+ex2);
                  }
               }
               finally{
                    db.close(q);
               }

               return ret1&&ret2;
       }


       @Override
       public synchronized void open() {
           if(db!=null)return;

           logger.trace("begin init_db");

           db=new connection_pool();
           db.init(cfg.getDrv(),cfg.getURL(),cfg.getUser(),cfg.getPasswd());

           logger.trace("init connection_pool db");

           String next_id =  " NEXT VALUE for "+cfg.getSeq()+" ,";
           //String next_id0=" "+cfg.getSeq()+".NEXTVAL"+" ,";

           //String fields_selectX509="MAIL_UID,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER";
           String fields_select0="MAIL_UID,ADDR_FROM,SUBJECT,FILENAME,ADDR_TO,CREATE_DATE,SENT_DATE,RECEIVE_DATE,DEL_DATE,ANS_DATE,ATTACH_SIZE,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER";
           String fields_select ="MAIL_UID,MAIL_NUM,ADDR_FROM,MAIL_ID,SUBJECT,FILENAME,ADDR_TO,CREATE_DATE,SENT_DATE,RECEIVE_DATE,DEL_DATE,ANS_DATE,MIME,ATTACH_SIZE,X509_TYPE,X509_TYPE_FILE,X509_BEGIN_DATE,X509_END_DATE,X509_SERIAL,X509_SUBJECT,X509_ISSUER,X509_TXT,X509_BIN";
                                         //1       2        3          4       5       6        7       8           9         10           11       12       13   14         15         16             17              18            19          20           21          22       
           String fields_insert=next_id+
                                          "?      ,?       ,?         ,?      ,?      ,?       ,?      ,?          ,?        ,?           ,?       ,?       ,?   ,?         ,?         ,?             ,?              ,?            ,?          ,?           ,?          ,?";

           queryInsert      = "INSERT INTO "+cfg.getTableEml()+" ( "+fields_select+" ) VALUES ("+fields_insert+" ) ";
           querySelect      = "SELECT MAIL_UID,"+fields_select+" FROM "+cfg.getTableEml()+" ORDER BY CREATE_DATE ";
           querySelect0     = "SELECT "+fields_select0+" FROM "+cfg.getTableEml()+" ORDER BY CREATE_DATE ";
           querySelectID    = "SELECT "+fields_select +" FROM "+cfg.getTableEml()+" WHERE MAIL_UID  = ? ";
           querySelectID0   = "SELECT "+fields_select0+" FROM "+cfg.getTableEml()+" WHERE MAIL_UID  = ? ";
           querySelectType  = "SELECT "+fields_select +" FROM "+cfg.getTableEml()+" WHERE X509_TYPE = ? ORDER BY CREATE_DATE";
           querySelectType0 = "SELECT "+fields_select0+" FROM "+cfg.getTableEml()+" WHERE X509_TYPE = ? ORDER BY CREATE_DATE";

           //querySelectX509="SELECT "+fields_selectX509 +" FROM "+cfg.getTableEml()+"  ORDER BY CREATE_DATE";

           queryCreateSeq  = "CREATE SEQUENCE "+cfg.getSeq()+"  START WITH 1000 INCREMENT BY 1  "; //CREATE SEQUENCE ARH_SEQ START WITH 1000 INCREMENT BY 1  NOCYCLE    
           queryCreate     = "CREATE TABLE "+cfg.getTableEml()+" ( "+
                         "MAIL_UID          INTEGER,"+      
                         "MAIL_NUM          INTEGER,"+      
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
           create();

       }

       @Override
       public synchronized void close() {
              if(db!=null){try {db.close();} catch (dbExcept e) {}} 
              db=null;
              logger.trace("close db");
       }

       private int setAll(query q,lMessage msg) throws dbExcept {
                int s=0;
                try {                                               //  CREATE TABLE (
                      //q.setInt      ( 2+s,msg.getUID         ()); //  MAIL_UID          INTEGER,
                      q.setInt      ( 1+s,msg.getNum           ()); //  MAIL_NUM          INTEGER,
                      q.setString   ( 2+s,msg.getFrom          ()); //  ADDR_FROM         VARCHAR(128),
                      q.setString   ( 3+s,msg.getId            ()); //  MAIL_ID           VARCHAR(128),
                      q.setString   ( 4+s,msg.getSubject       ()); //  SUBJECT           VARCHAR(128),
                      q.setString   ( 5+s,msg.getFilename      ()); //  FILENAME          VARCHAR(128),
                      q.setString   ( 6+s,msg.getTOs           ()); //  ADDR_TO           VARCHAR(256), -- -> new table
                      q.setDate     ( 7+s,msg._getCreateDate   ()); //  CREATE_DATE       DATE,
                      q.setDate     ( 8+s,msg._getSentDate     ()); //  SENT_DATE         DATE,
                      q.setDate     ( 9+s,msg._getReceiveDate  ()); //  RECEIVE_DATE      DATE,
                      q.setDate     (10+s,msg._getDelDate      ()); //  DEL_DATE          DATE,
                      q.setDate     (11+s,msg._getAnswerDate   ()); //  ANS_DATE          DATE,
                      q.setString   (12+s,msg.getMime          ()); //  MIME              VARCHAR(128),
                      q.setInt      (13+s,msg.getSize          ()); //  ATTACH_SIZE       INTEGER,
                                                 
                      q.setString   (14+s,msg.getX509Type      ()); //  X509_TYPE         VARCHAR(32),
                      q.setString   (15+s,msg.getX509TypeFile  ()); //  X509_TYPE_FILE    VARCHAR(32),
                      q.setDate     (16+s,msg._getX509BeginDate()); //  X509_BEGIN_DATE   DATE,
                      q.setDate     (17+s,msg._getX509EndDate  ()); //  X509_END_DATE     DATE,
                      q.setString   (18+s,msg.getX509Serials   ()); //  X509_SERIAL       VARCHAR(4096), -- -> new table
                      q.setString   (19+s,msg.getX509Subject   ()); //  X509_SUBJECT      VARCHAR(256),
                      q.setString   (20+s,msg.getX509Issuer    ()); //  X509_ISSUER       VARCHAR(256),
                                                              
                      q.setString   (21+s,msg.getBodyTxt       ()); //  X509_TXT          VARCHAR(4096), -- -> new table or clob
                      q.setString   (22+s,msg.getBodyBin64     ()); //  X509_BIN          VARCHAR(4096)  -- -> new table or blob
                                                              
                } catch (dbExcept ex1) {   
                        logger.error("setAll ex:"+ex1);
                        throw new dbExcept("setAll",ex1);
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }

                return 22+s;
        }
        private lMessage getAll(query q) throws dbExcept{
                int s=0;
                lMessage msg=new lMessage();

                try {
                     msg.setUID           (q.Result().getInt      ( 1+s)); 
                     msg.setNum           (q.Result().getInt      ( 2+s)); 
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
                     msg.setUID           (q.Result().getInt      ( 1+s)); 
                     msg.setFrom          (q.Result().getString   ( 2+s)); 
                     msg.setSubject       (q.Result().getString   ( 3+s)); 
                     msg.setFilename      (q.Result().getString   ( 4+s)); 
                     msg.setTO            (q.Result().getString   ( 5+s)); 
                     msg.setCreateDate    (q.Result().getDate     ( 6+s)); 
                     msg.setSentDate      (q.Result().getDate     ( 7+s)); 
                     msg.setReceiveDate   (q.Result().getDate     ( 8+s)); 
                     msg.setDelDate       (q.Result().getDate     ( 9+s)); 
                     msg.setAnswerDate    (q.Result().getDate     (10+s)); 
                     msg.setSize          (q.Result().getInt      (11+s)); 
                     msg.setX509Type      (q.Result().getString   (12+s)); 
                     msg.setX509TypeFile  (q.Result().getString   (13+s)); 
                     msg.setX509BeginDate (q.Result().getDate     (14+s)); 
                     msg.setX509EndDate   (q.Result().getDate     (15+s)); 
                     msg.setX509Serial    (q.Result().getString   (16+s)); 
                     msg.setX509Subject   (q.Result().getString   (17+s)); 
                     msg.setX509Issuer    (q.Result().getString   (18+s)); 
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }
                                                              

                return msg;
        }
        /*
        private lMessage getX509(query q) throws dbExcept{
                int s=0;
                lMessage msg=new lMessage();

                try {
                     msg.setUID           (q.Result().getInt      (1+s)); 
                     msg.setX509Type      (q.Result().getString   (2+s)); 
                     msg.setX509TypeFile  (q.Result().getString   (3+s)); 
                     msg.setX509BeginDate (q.Result().getDate     (4+s)); 
                     msg.setX509EndDate   (q.Result().getDate     (5+s)); 
                     msg.setX509Serial    (q.Result().getString   (6+s)); 
                     msg.setX509Subject   (q.Result().getString   (7+s)); 
                     msg.setX509Issuer    (q.Result().getString   (8+s)); 
                }
                catch (Exception ex2) {
                        logger.error("setAll ex:"+ex2);
                        throw new dbExcept("setAll",ex2);
                }
                                                              

                return msg;
        }
        */
        @Override
        public ArrayList<lMessage> loadArray(String _type) {
               if(_type==null)return loadArray(querySelect,-1,null);
               else           return loadArray(querySelectType,-1,null);
        }
    	@Override
    	public lMessage loadArray(int _uid) {
               ArrayList<lMessage> list;

               if(_uid<0)list=loadArray(querySelect  ,-1  ,null);
               else      list=loadArray(querySelectID,_uid,null);

               if(list.size()>0)return list.get(0);

               return new lMessage();
        }
       
        private ArrayList<lMessage> loadArray(String _select,int _uid,String _type) {
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
       @Override
       public JSONObject loadJSON(String _type) {
              if(_type==null)return  loadJSON(querySelect0   ,-1,null);
              else           return  loadJSON(querySelectType0,-1,_type);
       }
       @Override
       public JSONObject loadJSON(int _uid) {
              if(_uid<0)return  loadJSON(querySelect0 ,-1,null);
              else      return  loadJSON(querySelectID0,_uid,null);
       }

       private JSONObject loadJSON(String _select,int _uid,String _type) {
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
              
                       q.creatPreSt(_select);
                       if(_uid>0)    q.setInt    ( 1,_uid);
                       if(_type!=null)q.setString ( 1,_type);
 
                       q.executeQuery();
                       int s=0;
                       while(q.isNextResult()) {
                            lMessage msg=get0All(q);
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
                       else       logger.error("db q_id:? ex:"+ex);
                  }
                  finally{
                       db.close(q);
                  }
               }
               return root;
       }
       @Override
       public void save(lMessage msg) {
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
       @Override
       public JSONObject loadJSONX509(String _type) {return new JSONObject();}
       @Override
       public JSONObject loadJSONX509(int _uid) {return new JSONObject();}
   	   @Override
   	   public lMessage loadArrayX509(int _x509_id) {return new lMessage();}


       public static void main(String args[]){
              logger.info("START");

       }
}
