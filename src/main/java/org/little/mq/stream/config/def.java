package org.little.mq.stream.config;

public class def{
       final private static String CLASS_NAME="org.little.mq.stream.config.def";
       final private static int    CLASS_ID  =1804;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);
                                                    
       public static final String CNFG_TAG_CNFG_ENTRY_POINT         = "STREAM_CONFIGURATION";
       public static final String CNFG_TAG_INTERFACES               = "INTERFACES";
       public static final String CNFG_TAG_INTERFACE                = "INT";
       public static final String CNFG_TAG_SOURCE_QUEUE_NAME        = "SOURCE_QUEUE_NAME";
       public static final String CNFG_TAG_TARGET_QUEUE_NAME        = "TARGET_QUEUE_NAME";
       public static final String CNFG_TAG_TARGET_QM_NAME           = "TARGET_QM_NAME";
       public static final String CNFG_TAG_ROUTE                    = "ROUTE";
       public static final String CNFG_TAG_DEFAULT_ROUTE            = "DEFAULT_ROUTE";
       public static final String CNFG_TAG_POINT                    = "POINT";
       public static final String CNFG_TAG_NEGATION                 = "NEGATION";
       public static final String CNFG_TAG_REPLY_QM_NAME            = "REPLY_QM_NAME";
       public static final String CNFG_TAG_REPLY_QUEUE_NAME         = "REPLY_QUEUE_NAME";
       public static final String CNFG_TAG_REPORT                   = "REPORT";
       public static final String CNFG_TAG_INTERFACE_NAME           = "name";
       public static final String CNFG_TAG_INTERFACE_DESCRIPTION    = "description";
       public static final String CNFG_TAG_ERROR_QUEUE_NAME         = "ERROR_QUEUE_NAME";
       public static final String CNFG_TAG_LOG_PATH                 = "LOG_PATH";
       public static final String CNFG_TAG_LOG_LEVEL                = "LOG_LEVEL";
       public static final String CNFG_TAG_LOG_FORMAT               = "LOG_FORMAT";
       public static final String CNFG_TAG_LAST                     = "LAST";
       public static final String CNFG_TAG_EXPIRY                   = "EXPIRY";
       public static final String CNFG_TAG_YES                      = "YES";
       public static final String CNFG_TAG_LOG_ROUTE                = "ROUTE_LOG";
      
       public static final String CNFG_TAG_LOG_LEVEL_ALL            = "ALL";
       public static final String CNFG_TAG_LOG_LEVEL_MQ_HEADER      = "MQ_HEADER";
       public static final String CNFG_TAG_LOG_LEVEL_XML_HEADER     = "XML_HEADER";
       public static final String CNFG_TAG_LOG_LEVEL_DISABLED       = "DISABLED";

       public static final int    CNFG_TAG_LOG_LEVEL_ALL_INT        = 0;
       public static final int    CNFG_TAG_LOG_LEVEL_MQ_HEADER_INT  = 1;
       public static final int    CNFG_TAG_LOG_LEVEL_XML_HEADER_INT = 2;
       public static final int    CNFG_TAG_LOG_LEVEL_DISABLED_INT   = 3;
       public static final int    CNFG_TAG_LOG_LEVEL_DEFAULT_INT    = CNFG_TAG_LOG_LEVEL_ALL_INT;
      
       //public static final char   CNFG_ANY_CHAR                     = '*';
       //public static final char   CNFG_CHAR_FOR_HASH                = '~';

}                                                   
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    
                                                    