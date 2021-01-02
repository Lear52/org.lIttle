package org.little.util.string;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.ArrayList;

/** 
 * Класс StringParser работы со строками
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2003-2020
 * @version 2.0
 */

public class stringParser{
       final private static String CLASS_NAME="org.little.util.stringParser";
       final private static int    CLASS_ID  =115;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
             //private static Logger log=new Logger(CLASS_NAME);

       private StringTokenizer parser;
       private String          token;

       public stringParser(String string,String _token){
              parser = new StringTokenizer(string, _token,true);
              token  =_token;
       }

       public String get(){
              String ss=null;
              try{
                  while(parser.hasMoreTokens()){
                        ss=parser.nextToken();
                        char c=ss.charAt(0);
                        if(token.indexOf(c)>=0)continue;
                        return ss;
                  }
              } 
              catch(NoSuchElementException e){
                        return ss;
              }
              return null;
       }

       public ArrayList<CharSequence> getList(){
              ArrayList<CharSequence> list=new ArrayList<CharSequence>();
              String s;
              while((s=get())!=null)list.add(s);
              return list;
       }


}
