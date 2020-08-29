package org.little.util;

public class LoggerFactory{

       final private static String CLASS_NAME="org.little.util.LoggerFactory";
       final private static int    CLASS_ID  =112;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
            

       public static Logger getLogger(Class<? extends Object> c){
           return new Logger(c);
       }
    
       public static Logger getLogger(String name){
           return new Logger(name);
       }
    
}
