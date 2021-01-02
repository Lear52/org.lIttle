package org.little.util.cfg;

/** 
 * Класс def - содержит константы и параметры запросов
 *
 * 
 * @author Andrey Shadrin, Copyright &#169; 2002-2017
 * @version 1.2 
 */ 
public final class def{
       private final static String CLASS_NAME="prj0.util.def";
       private final static int    CLASS_ID  =101;
       public        static String getClassName(){return CLASS_NAME;}
       public        static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       //public  final static String def_log_name         = "prj0";
       public  final static String def_cfg_name         = "config_prj0";
       public  final static String def_res_db_drv       = "db.drv";
       public  final static String def_res_db_url       = "db.url";
       public  final static String def_res_db_username  = "db.username";
       public  final static String def_res_db_passwd    = "db.passwd";
       public  final static String def_res_sesion_timeout = "session.timeout";
                    
                    
       public  final static String word_token=" \t\n\r.,:;\"'!?()[]{}-+/*<>&#$%~_=№";
                    
       public  final static String instance_null         = "common.getInstance()==null";
       public  final static String error_get_record      = "error prj0.util.record.getAll() ";
       public  final static String error_set_record      = "error prj0.util.record.setAll() ";
       public  final static String error_DB_get_guide    = "error prj0.util.guide.getAll() ";
       public  final static String error_DB_set_guide    = "error prj0.util.guide.setAll() ";
       public  static final String db_null               = "common.getInstance().getDB()==null";
       public  static final String db_open_null          = "common.getInstance().getDB().open()==null";
       public  static final String error_DB_get          = "Error DB get record ";
       public  static final String error_DB_set          = "Error DB set record ";
                    
       public  final static int    id_def                = 0;
       public  final static int    user_def              = 1;
       public  final static int    group_def             = 0;
       public  final static int    user_admin            = 1;
       public  final static int    group_all             = 0;
       public  final static int    group_admin           = 1;
       public  final static int    group_def_mode        = 0;
       public  final static int    all_def_mode        = 0;
       
};
