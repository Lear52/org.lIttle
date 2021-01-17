package org.little.util;

/** 
 * class Except
 *
 * 
 * @author <b>Andrey Shadrin</b>, Copyright &#169; 2002 - 2021
 * @version 1.6
 */
public class Except extends Exception{
       final private static String CLASS_NAME="org.little.util.Except";
       final private static int    CLASS_ID  =104;
       final private static long   serialVersionUID = 19690401L+CLASS_ID;
             public  static String getClassName(){return CLASS_NAME;}
             public  static int    getClassId(){return CLASS_ID;}
//-----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------
       /**
        * 
        */
       String msg;
       /**
        * parent.getMessage()/
       */
       String extent_msg;
       /**
        * 
       */
       String stack_msg;
       
       public        Except(){msg=new String("");stack_msg=new String("");}
       public        Except(String s){set(s);}
       public        Except(String s,Throwable cause){set(s,cause);}
       public        Except(String s,Exception e){set(s,e);}
       public        Except(Exception e){set(e);}
       
       public Except(Throwable cause){set("",cause);}
       public void   set(String m){msg=m;extent_msg=null;getStack();}
       public void   set(String m,Throwable e){
                     msg=m;
                     extent_msg=e.getMessage();
                     stack_msg=getStack(e);
       
       }
       public void   set(String m,Exception e){
                     msg=m;
                     extent_msg=e.getMessage();
                     stack_msg=getStack(e);
       
       }
       public void   set(Exception e){
                     msg="";
                     extent_msg=e.getMessage();
                     stack_msg=getStack(e);
       }
       //-----------------------------------------------------------------------------
       //
       //-----------------------------------------------------------------------------
       public String getMsg(){return msg==null?"":msg;}

       public String getExtMsg(){return extent_msg==null?"":extent_msg;}

       public String getStackMsg(){return stack_msg==null?"":stack_msg;}

       public String getMessage(){ return toString();}
       
       /**
        * 
       */
       public String toString(){
               String s = getClass().getName();
               if(extent_msg != null) return msg+" ("+s+": " + extent_msg+") \n"+stack_msg;
               else                   return msg+" ("+s+") \n"+stack_msg;
       }
       /**
        * 
       */
       public String getStack(){
       
              StackTraceElement [] st = getStackTrace();
              StringBuffer      buf=new StringBuffer(10240);
       
              //if(buf==null)return null;
       
              for(int i=0;i<st.length;i++){
                  buf.append(st[i].toString());
                  buf.append("\n");
              }
       
              stack_msg=buf.toString();
       
              return stack_msg;
       
       }
       public static String _getStack(){
       
              StackTraceElement [] st = (new Exception()).getStackTrace();
              StringBuffer      buf=new StringBuffer(10240);
       
              //if(buf==null)return null;
       
              for(int i=0;i<st.length;i++){
                  buf.append(st[i].toString());
                  buf.append("\n");
              }
       
      
              return buf.toString();
       
       }
       /**
        * �������� ���� ����������
       */

       public static String getStack(Throwable e){
       
              StackTraceElement [] st = e.getStackTrace();
              StringBuffer      buf=new StringBuffer(10240);
       
              //if(buf==null)return null;
       
              for(int i=0;i<st.length;i++){
                  buf.append(st[i].toString());
                  buf.append("\n");
              }
              return buf.toString();
       
       }
       public static String getStack(Exception e){
       
              StackTraceElement [] st = e.getStackTrace();
              StringBuffer      buf=new StringBuffer(10240);
       
              //if(buf==null)return null;
       
              for(int i=0;i<st.length;i++){
                  buf.append(st[i].toString());
                  buf.append("\n");
              }
              return buf.toString();
       
       }
       public static String printException(Exception e){
       
              StackTraceElement [] st = e.getStackTrace();
              StringBuffer      buf=new StringBuffer(10240);
       
              //if(buf==null)return null;
              buf.append(e.toString());
              buf.append("\n");
       
              for(int i=0;i<st.length;i++){
                  buf.append(st[i].toString());
                  buf.append("\n");
              }
              return buf.toString();
       
       }
       
       
 //-----------------------------------------------------------------------------
 //
 //-----------------------------------------------------------------------------
}
 
 
 
 
 
 