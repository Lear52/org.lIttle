package org.little.util;

public class Logger{

       final private static String CLASS_NAME="org.little.util.Logger";
       final private static int    CLASS_ID  =111;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);
             //org.apache.log4j.
       private org.slf4j.Logger mainLogger;
       private org.slf4j.Logger errorLogger;
       private String           classNamePrefix;

       public Logger(String name){
              mainLogger      = org.slf4j.LoggerFactory.getLogger(name);
              errorLogger     = org.slf4j.LoggerFactory.getLogger((new StringBuilder()).append("error.").append(name).toString());
              classNamePrefix = (new StringBuilder()).append(name).append(": ").toString();
       }

       public Logger(Class<? extends Object> c){
              mainLogger      = org.slf4j.LoggerFactory.getLogger(c);
              errorLogger     = org.slf4j.LoggerFactory.getLogger((new StringBuilder()).append("error.").append(c.getName()).toString());
              classNamePrefix = (new StringBuilder()).append(c.getSimpleName()).append(": ").toString();
       }

       public static Logger getLogger(Class<? extends Object> c){
           return new Logger(c);
       }
    
       public static Logger getLogger(String name){
           return new Logger(name);
       }
    
       synchronized
       public void debug(Object message){
           mainLogger.debug((new StringBuilder()).append(classNamePrefix).append(message).toString());
       }
    
       synchronized
       public void debug(Object message, Throwable t){
           mainLogger.debug((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
       }
    
       synchronized
       public void error(Object message) {
           mainLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString());
           errorLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString());
       }
    
       synchronized
       public void error(Object message, Throwable t) {
           mainLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
           errorLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
       }
    
       synchronized
       public void fatal(Object message){
           mainLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString());
           errorLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString());
       }
    
       synchronized
       public void fatal(Object message, Throwable t){
           mainLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
           errorLogger.error((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
       }
    
       synchronized
       public void trace(Object message) {
           mainLogger.trace((new StringBuilder()).append(classNamePrefix).append(message).toString());
       }
    
       synchronized
       public void trace(Object message, Throwable t){
           mainLogger.trace((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
       }
    
       synchronized
       public void info(Object message){
           mainLogger.info((new StringBuilder()).append(classNamePrefix).append(message).toString());
       }
    
       public void info(Object message, Throwable t){
           mainLogger.info((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
       }
    
       synchronized
       public void warn(Object message){
           mainLogger.warn((new StringBuilder()).append(classNamePrefix).append(message).toString());
       }
    
       synchronized
       public void warn(Object message, Throwable t){
           mainLogger.warn((new StringBuilder()).append(classNamePrefix).append(message).toString(), t);
       }

       public boolean isTrace() {
             return mainLogger.isTraceEnabled();
       }


    public static void main(String args[]){
           Logger LOG = LoggerFactory.getLogger(Logger.class);

           LOG.info("INFO");
           LOG.trace("TRACE");
           LOG.error("ERROR");
    }

}
