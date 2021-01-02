package org.little.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;

import org.little.util.Logger;
import org.little.util.cfg.iconfig;

/** 
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002-2021
 * @version 1.4
 */

public class connection_pool {
       final private static String CLASS_NAME="org.little.util.db.connection_pool";
       final private static int    CLASS_ID  =201;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);
        //-----------------------------------------------------------------------------
        //
        //-----------------------------------------------------------------------------
        private static Object lock = new Object();

        private final  static  int max_timeout = 15 * 60 * 1000;//15 �����

        private static boolean       is_load = false;

        private static String        db_drv;
        private static String        db_url;
        private static String        username;
        private static String        passwd;

        private Vector<query>        connections;
        //ArrayList connections;

        //-----------------------------------------------------------------------------
        //
        //-----------------------------------------------------------------------------
        private static connection_pool   db_pool;
        
        public static connection_pool getDB() {
            if(db_pool==null){
               db_pool = new connection_pool();
            }
            return db_pool;
        }
        public static void closeDB(){
            if(db_pool!=null)
            try {db_pool.closeAll();} catch (dbExcept e) {}
            db_pool=null;
        }
        
        /** 
         * 
         */
        public connection_pool() {
               is_load = false;
               connections = new Vector<query>(10);
               db_drv=null;
               db_url=null;
               username=null;
               passwd=null;
        }
        public void init(iconfig cfg) throws dbExcept{

                log.trace("connection_pool.init() is_load:"+is_load);

                synchronized (lock) {
                      is_load   = false;
                     
                      db_drv    = null;
                      db_url    = null;
                      username  = null;
                      passwd    = null;
                      String cfg_topic = cfg.getTopic();
                      try {
                           db_drv   = cfg.get(org.little.util.cfg.def.def_res_db_drv);
                           db_url   = cfg.get(org.little.util.cfg.def.def_res_db_url);
                           username = cfg.get(org.little.util.cfg.def.def_res_db_username);
                           passwd   = cfg.get(org.little.util.cfg.def.def_res_db_passwd);
                      } catch (Exception e) {
                              String s="";
                              if(db_drv==null)  s=s+" db_drv==null ";
                              if(db_url==null)  s=s+" db_url==null ";
                              if(username==null)s=s+" username==null ";
                              if(passwd==null)  s=s+" passwd==null ";
                              log.error(dbMsg.error_load_res+" topic:"+cfg_topic+s);
                      }
                }
                log.trace("connection_pool.init() ok");

        }
        /** 
         * 
         */
        public static void init(String _db_drv,String _db_url,String _username,String  _passwd){

                log.trace("connection_pool.init() is_load:"+is_load);

                synchronized (lock) {
                      is_load   = false;
                      db_drv   = _db_drv  ;
                      db_url   = _db_url  ;
                      username = _username;
                      passwd   = _passwd  ;
                }
                log.trace("connection_pool.init() ok");

        }

        private void _init() throws dbExcept{

                log.trace("connection_pool._init() is_load:"+is_load);

                synchronized (lock) {

                   if(is_load)return;

                   if(db_drv == null || db_url == null || username == null || passwd == null) {
                      String s="";
                      if(db_drv==null)  s=s+" db_drv==null ";
                      if(db_url==null)  s=s+" db_url==null ";
                      if(username==null)s=s+" username==null ";
                      if(passwd==null)  s=s+" passwd==null ";

                      dbExcept ex = new dbExcept(dbMsg.error_init_db+s);
                      log.error(dbMsg.error_init_db+" ex:0"+ex);
                      throw ex;
                   }

                   try {
                        Class.forName(db_drv);
                   } catch (Exception e) {
                        dbExcept ex = new dbExcept(dbMsg.error_load_drv,e);
                        log.error(dbMsg.error_load_drv+" ex:"+ex);
                        throw ex;
                   }

                   is_load = true;

                   log.trace(dbMsg.load_db_driver+" driver:"+db_drv);
                   
                   log.trace(dbMsg.init_pool_connection+" db_drv:"+db_drv+" db_url:"+db_url+" username:"+username);
                }

        }
        /** 
         * 
         */
        protected query add() throws dbExcept {
                query q;

                try {
                        q = new query();
                } catch (Exception e) {
                        dbExcept ex = new dbExcept(dbMsg.error_creat_query, e);
                        log.error(dbMsg.error_creat_query+" ex:"+ex);
                        throw ex;
                }
                //------------------------------------------------------------------
                try {
                    //oracle.jdbc.Const.TRACE=true;
                    q.con = DriverManager.getConnection(db_url, username, passwd);
                } 
                catch (Exception e) {
                    dbExcept ex = new dbExcept(dbMsg.error_set_conn,e);
                    log.error(dbMsg.error_set_conn+" url:"+db_url+" util:"+username+" pswd:"+passwd+" ex:"+ex);
                    throw ex;
                }
                q.creatSt();
                q.info.start();

                //connect(q);
                //--------------------------------------------------------------------
                int s;
                synchronized (connections) {
                        connections.addElement(q);
                        s=connections.size();
                }
                q.setId(s);

                log.trace(dbMsg.create_new_connection+"id:"+s+" size pool:"+s);

                log.trace("create connection query_id:"+q.getId());

                return q;
        }


        public query[] openArray(int size) throws dbExcept{
               return null;
        }
        /**
         * 
         */
        public query open() throws dbExcept{

                _init();
                //---------------------------------------
                query q=null;
                //---------------------------------------
                // 
                //---------------------------------------
                synchronized (connections) {
                        for (int i = 0; i < connections.size(); i++) {
                                query cur_q = (query) connections.elementAt(i);
                                if(cur_q == null){
                                   log.trace(dbMsg.a_close_db_connection+" ("+i+")");
                                   connections.removeElementAt(i);
                                   i=0;
                                   continue;
                                }
                                Connection con=cur_q.con;
                                if(con == null) {
                                   log.trace(dbMsg.a_close_db_connection+" id:"+cur_q.getId()+" ("+i+")");
                                   cur_q.clear();
                                   connections.removeElementAt(i);
                                   i=0;
                                   continue;
                                }
                                boolean is_close;
                                //
                                try{is_close = con.isClosed();} 
                                catch (Exception e) {
                                      is_close = true;
                                      log.trace(dbMsg.a_close_db_connection+" id:"+cur_q.getId()+" ("+i+") ex:"+e);
                                }

                                if(is_close){ 
                                      log.trace(dbMsg.close_db_connection+" id:"+cur_q.getId()+" ("+i+")");
                                      cur_q.clear();
                                      connections.removeElementAt(i);
                                      i=0;
                                      continue;
                                }
                                
                                if(cur_q.info.isStop()){
                                   if(q==null){
                                      cur_q.info.restart();
                                      log.trace(dbMsg.get_db_connection+" id:"+cur_q.getId()+" ("+i+")");
                                      q=cur_q;
                                      continue;
                                   }
                                   else{
                                      long d=System.currentTimeMillis()-cur_q.info.getStop();
                                      if(d>max_timeout){
                                         log.trace(dbMsg.close_db_connection+" id:"+cur_q.getId()+" ("+i+")");
                                         cur_q.clear();
                                         connections.removeElementAt(i);
                                         continue;
                                      }

                                   }
                                }

                        }
                }
                if(q!=null){
                   log.trace("open connection query_id:"+q.getId());
                   return q;
                }
                //---------------------------------------
                // 
                //---------------------------------------
                log.trace("connection pool is empty ");

                return add();
        }
        /** 
         * 
         * 
         */
        /**
          Close all pool
        */
        public synchronized void closeAll() throws dbExcept {
                close();
        }

        /**
          Close all pool
        */
        public synchronized void close() throws dbExcept {
                query _q;
                synchronized (connections) {

                     if (connections != null){
                             for (int i = 0; i < connections.size(); i++) {
                                     _q = (query) connections.elementAt(i);
                                     if (_q != null) {
                                             try {
                                                     if (_q.con != null){
                                                         _q.con.close();
                                                     }
                                             } 
                                             catch (Exception e) {
                                               log.error(dbMsg.error_close_db_connection+" ("+i+") ex:"+e);
                                             }
                                     }
                             }
                     }
                }
                connections = null;

        }
        /** 
         * 
         * 
         */
        public synchronized void close(query q) {
                if(q != null){
                   log.trace("close connection query_id:"+q.getId());
                   q.info.stop();
                }
        }
        public synchronized void close(query [] q) {

        }
        /**
         * 
         */
        public int count_connection() {
                if (connections == null) return 0;
                return connections.size();
        }
        public query_info getInfo(int i){
                if (connections == null)   return null;
                if (i > connections.size())return null;
                query q;
                q = (query) connections.elementAt(i);
                if (q == null) return null;
                return q.info;
        }

        //-----------------------------------------------------------------------------
        //
        //-----------------------------------------------------------------------------
        public static void main(String[] args) throws  dbExcept {
               connection_pool b=null;
               query           q=null;

               String _db_drv   ="oracle.jdbc.driver.OracleDriver";
               String _db_url   ="db.url=jdbc:oracle:thin:@10.93.128.14:1521:xe";
               String _username ="prj02";
               String  _passwd  ="prj02";

               connection_pool.init(_db_drv,_db_url,_username,_passwd);

               b=connection_pool.getDB();

               try{
                  // 
                   q=b.open();
                   System.out.println("open db q_id:"+q.getId());
                   q.execute("select count(*) from dual ");
                   System.out.println("execute db q_id:"+q.getId());
                   while(q.isNextResult()) {
                         System.out.println("isNextResult db q_id:"+q.getId());
                         try{q.Result().getInt(1);}catch(Exception ex1){}
                         System.out.println("Result db q_id:"+q.getId());
                   } 
                   System.out.println("ping db q_id:"+q.getId());
                   org.little.util.tfork.delay(120);

               }catch(dbExcept ex){
                   if(q!=null)System.out.println("db q_id:"+q.getId()+" ex:"+ex);
                   else       System.out.println("db q_id:? ex:"+ex);
               }
               finally{
                   b.close(q);
               }
               System.out.println("DB:ok!");

        }


}
