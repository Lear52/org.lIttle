package org.little.util.db;

import java.sql.SQLException;

import org.little.util.Except;
/** 
 * Класс  Basa_Except
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002-2021
 * @version 1.1
 */

public class dbExcept extends Except{
       final private static String CLASS_NAME="org.little.util.db.dbExcept";
       final private static int    CLASS_ID  =202;
             final private static long   serialVersionUID = 19690401L+CLASS_ID;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

             private int code;

              public dbExcept(){super();code=-1;}
              public dbExcept(String s){super(s);code=-1;}
              public dbExcept(String s,Exception e){super(s,e);code=-1;}
              public dbExcept(String s,SQLException e){
                     super(s,e);
                     code=e.getErrorCode();
              }
              public int getCode() {
                     return code; 
              }
               
              public dbExcept(Except e){super(e);code=-1;}
              
}


