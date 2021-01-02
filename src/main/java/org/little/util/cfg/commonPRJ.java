package org.little.util.cfg;

import java.util.ArrayList;
import java.util.Random;

import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.db.connection_pool;
import org.little.util.db.dbExcept;
import org.little.util.db.query;
 

/**
 * @author av
 *
 */
public class commonPRJ {
       final private static String CLASS_NAME="org.little.util.commonPRJ";
       final private static int    CLASS_ID  =103;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             private static Logger log=new Logger(CLASS_NAME);

        private static Object        lock = new Object();
        private static commonPRJ     com = null;
        private ArrayList<commonPRJ> instances;
        private connection_pool      db_pool;
        private iconfig              main_cfg;
        private Random               generator;
        private String               topic;


        public connection_pool getDB() {
                if(db_pool==null){


                }
                return db_pool;
        }

        public String getTopic() {
                return topic;
        }
        public iconfig getConfig() {
                return main_cfg;
        }
        public Random getRandom() {
                return generator;
        }

        public void init() throws Except {

            String _cfg_name=def.def_cfg_name;

            main_cfg = new config();

            try {
                main_cfg.open(_cfg_name);
            } 
            catch (Exception ex) {
                   log.error("error open "+def.def_cfg_name);
                   throw new Except("common.init()",ex);

            }
            try {
                 db_pool = new connection_pool();
                 db_pool.init(main_cfg);
            } 
            catch(dbExcept ex){
                 log.error("connection_pool.Init() Ex:"+ex);
                 throw ex;

            }
            //log.trace("common.init()");

        }
        public void init(String topic) throws Except {

            main_cfg = new config();

            try {
                main_cfg.open(com.main_cfg,topic);
            } 
            catch (Exception ex) {
                   log.error("error open "+def.def_cfg_name+" ("+topic+")");
                   throw new Except("common.init("+topic+")",ex);

            }
            try {
                 db_pool = new connection_pool();
                 db_pool.init(main_cfg);
            } 
            catch(dbExcept ex){
                 log.error("connection_pool.Init() Ex:"+ex);
                 throw ex;

            }
            //log.trace("common.init()");

        }
        void close() throws Except {
                if(db_pool!=null)db_pool.closeAll();
                com = null;
                generator = null;
        }

        public int IRandom(){
                   try{return generator.nextInt();} catch(Exception e){ return 0; }
        }

        public static commonPRJ getInstance() throws Except  {
               synchronized (lock) {
                     if(com != null) return com;
                      commonPRJ _com       = new commonPRJ();
                      //if (_com == null){
                      //    log.error("common.new()==null");
                      //    return null;
                      //}
                      _com.topic     = "main";
                      _com.generator = new Random();
                      _com.instances = new ArrayList<commonPRJ>(10);
                      _com.instances.add(com);
                      try {
                           _com.init();
                      } 
                      catch (Except ex) {
                             try {
                              _com.close();
                              com=null;
                             } 
                             catch (Except aa){
                                    log.error("common.init()");
                                    throw ex; 
                             }
                      }
                      com=_com;
                      return com;
               }
        }
        public static commonPRJ getInstance(String topic) throws Except  {

                if(com == null) getInstance();
                synchronized (lock) {
                   
                    for(int i=0;i<com.instances.size();i++){
                        commonPRJ c=com.instances.get(i);
                        if(c==null)continue;
                        if(topic.compareTo(c.getTopic())==0){
                           return c;
                        }
                   
                    }
                    commonPRJ local_com;
                   
                    local_com           = new commonPRJ();
                   
                    local_com.generator = com.generator;
                    local_com.topic     = topic;
                    local_com.instances =com.instances;
                    //log.trace("common.new() is ok");
                    try {
                           local_com.init(topic);
                   
                    } 
                    catch (Except ex) {
                           try {
                                local_com.close();
                                local_com=null;
                           } 
                           catch (Except aa) {}
                           log.error("common.init("+topic+")");
                           throw ex; 
                   
                    }
                    com.instances.add(local_com);
                    return local_com;
                }
        }

        public static void main(String[] args) throws Exception{
                commonPRJ       c;
                query           q;
                connection_pool b;

                c = commonPRJ.getInstance();
                b = c.getDB();
                q = b.open();
                q.commit();
                b.close(q);
                System.out.println("common("+c.getTopic()+") is ok");

                c = commonPRJ.getInstance("netmon_stend");
                b = c.getDB();
                q = b.open();
                q.commit();
                b.close(q);
                System.out.println("common("+c.getTopic()+") is ok");

                c = commonPRJ.getInstance("netmon_prod");
                b = c.getDB();
                q = b.open();
                q.commit();
                b.close(q);
                System.out.println("common("+c.getTopic()+") is ok");

                c = commonPRJ.getInstance("lear");
                b = c.getDB();
                q = b.open();
                q.commit();
                b.close(q);
                System.out.println("common("+c.getTopic()+") is ok");
        }
}
