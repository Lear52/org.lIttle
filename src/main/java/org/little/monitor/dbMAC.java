package org.little.monitor;


import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util.db.commonDB;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;


public class dbMAC{
       private static final Logger    logger = LoggerFactory.getLogger(dbMAC.class);

 
 
       private static connection_pool db=null;
       private commonDB               cfg;
       private query                  q_select=null;
       private query                  q_update=null;
       private query                  q_insert=null;

       final private static String    table_name = " ADDR_MAC AD ";
       final private static String    querySelect = "SELECT  AD.ADDR_IP, AD.ADDR_MAC FROM "+table_name+" WHERE AD.ADDR_IP=? AND AD.ADDR_MAC=? ";
       final private static String    queryUpdate = "UPDATE "+table_name+"SET AD.ADDR_LAST=SYSDATE WHERE AD.ADDR_IP=? AND AD.ADDR_MAC=? ";
       final private static String    queryInsert = "INSERT INTO "+table_name+"(AD.ADDR_ID,AD.ADDR_IP,AD.ADDR_MAC,AD.ADDR_BEGIN,AD.ADDR_LAST) VALUES (PRJ_ADDR_MAC.NEXTVAL,?,?,SYSDATE,SYSDATE) ";

  
        public dbMAC(commonDB cfg){
               this.cfg=cfg;
        }
        
        //private synchronized void close() {
        //        if(db!=null){try {db.close();} catch (dbExcept e) {}} 
        //        db=null;
        //        logger.trace("close db");
        //}

   
        public void open() throws dbExcept{

                if(db==null){
                   logger.trace("begin init_db");
                   db=new connection_pool();
                   db.init(cfg.getDrv(),cfg.getURL(),cfg.getUser(),cfg.getPasswd());
                }       
                q_select = db.open(); if(q_select==null)throw new dbExcept(defMsg.db_open_null);
                q_update = db.open(); if(q_update==null)throw new dbExcept(defMsg.db_open_null);
                q_insert = db.open(); if(q_insert==null)throw new dbExcept(defMsg.db_open_null);
                //-------------------------------------------
                try{
                    q_select.creatPreSt(querySelect);
                    q_update.creatPreSt(queryUpdate);
                    q_insert.creatPreSt(queryInsert);
                } catch (dbExcept ex) {
                  close();
                  throw new dbExcept(" create pre statement query", ex);
                }
                logger.info("Start record mac to db");

       
        }
        public void close(){
                try { 
                    if(q_select==null)q_select.closePreSt();
                    if(q_update==null)q_update.closePreSt();
                    if(q_insert==null)q_insert.closePreSt();
                    db.close(q_select);
                    db.close(q_update);
                    db.close(q_insert);
                } catch (dbExcept ex){}
       
                logger.info("End record mac to db");
        }
        public void update(String addr,String mac) throws dbExcept{
                if(addr==null || mac==null){
                   logger.trace("No arg addr:"+addr+" mac:"+mac);
                   return;
                }
                try{
                    q_select.setString( 1, addr);
                    q_select.setString( 2, mac);
                    q_select.executeQuery();
                } catch (dbExcept ex) {
                  close();
                  throw new dbExcept(" select addr:"+addr+" mac:"+mac, ex);
                }
                if(q_select.isNextResult()){
                   try{
                       q_update.setString( 1, addr);
                       q_update.setString( 2, mac);
                       q_update.executeUpdate();
                   } catch (dbExcept ex) {
                     close();
                     throw new dbExcept(" update addr:"+addr+" mac:"+mac, ex);
                   }
                   logger.trace("update addr:"+addr+" mac:"+mac);
                }
                else{
                   try{
                       q_insert.setString( 1, addr);
                       q_insert.setString( 2, mac);
                       q_insert.executeQuery();
                   } catch (dbExcept ex) {
                     close();
                     throw new dbExcept("insert addr:"+addr+" mac:"+mac, ex);
                   }
                   logger.trace("insert addr:"+addr+" mac:"+mac);
                }

        }

  
}
