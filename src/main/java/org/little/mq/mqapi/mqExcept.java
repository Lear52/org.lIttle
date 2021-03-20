package org.little.mq.mqapi;

import org.little.util.Except;

import com.ibm.mq.MQException;
//import prj0.util.Logger;
/** 
 * Класс mqExcept
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2017
 * @version 1.0
 */


public class mqExcept extends Except{
       final private static String CLASS_NAME="prj0.stream.mq.mqExcept";
       final private static int    CLASS_ID  =1703;
             final private static long   serialVersionUID = 19690401L+CLASS_ID;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

             private int code;

             public mqExcept(){super();code=-1;}
             public mqExcept(String s){super(s);code=-1;}
             public mqExcept(String s,Exception e){super(s,e);code=-1;}
             public mqExcept(String s,MQException e){
                    super(s,e);
                    code=e.getReason();
             }
             public int getCode() {
                    return code; 
             }
             
             public String getError() { 
                    return def.getRC(getCode());
             }
             public mqExcept(Except e){super(e);code=-1;}
              
}


