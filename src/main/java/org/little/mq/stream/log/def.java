package org.little.mq.stream.log;

public class def {
       final private static String CLASS_NAME="prj0.stream.log.def";
       final private static int    CLASS_ID  =1603;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}

  public static final int              LOG_BEAT                 = 60000;
  public static final java.lang.String ERROR_LOG_FILE_NAME      = "ERR_";
  public static final java.lang.String OUTPUT_LOG_FILE_NAME     = "OUT_";
  public static final java.lang.String DEFAULT_DATE_FORMAT      = "yyyyMMdd";
  public static final char             WIN_SEPARATOR            = '\\';
  public static final char             UNIX_SEPARATOR           = '/';
  public static final int              LOG_ROTATION_PERIOD_DAYS = 1;

}
