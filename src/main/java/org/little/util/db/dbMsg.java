package org.little.util.db;

/** 
 * class dbMsg - 
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002-2014
 * @version 1.3
 */
public class dbMsg{

       private final static String CLASS_NAME="org.little.util.db.dbMsg";
       private final static int    CLASS_ID  =203;
       public        static String getClassName(){return CLASS_NAME;}
       public        static int    getClassId(){return CLASS_ID;}

       public  final static String error_load_res="Can`t load resourse";
       public  final static String error_init_db="Can`t init basa";
       public  final static String error_set_conn="Set connection";
       public  final static String error_creat_query="creat query";
       public  final static String error_load_drv="Can`t load driver";
       public  final static String error_close_con="Can`t close connection";
       public  final static String error_close_db_connection="close DB connection";

       public  final static String create_new_connection="Create new db connection";
       public  final static String load_db_driver="Load DB driver";
       public  final static String get_db_connection="Get DB connection";
       public  final static String init_pool_connection="Init DB pool connection";
       public  final static String close_db_connection="Close DB connection";
       public  final static String a_close_db_connection="A'R close DB connection";

       public static final String  instance_null        = "common.getInstance()==null";
       public static final String  db_null              = "common.getInstance().getDB()==null";
       public static final String  db_open_null         = "common.getInstance().getDB().open()==null";
       public static final String  error_DB_get         = "Error DB get record ";
       public static final String  error_DB_set         = "Error DB set record ";


}
