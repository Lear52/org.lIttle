package org.little.rcmd.rsh;


import java.io.BufferedInputStream;
import java.util.ArrayList;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class rResponse  implements rCMD{
       private static Logger logger = LoggerFactory.getLogger(rResponse.class);

       private boolean               is_print;
       private String                response;
       private String                id;
       //private StringBuffer          txt;
       private ArrayList<String>     list_txt;
       private ArrayList<rCMD>       list_rcommand;

       public static final String    ok          ="ok";
       public static final String    error       ="error";
       public static final String    syntax_error="syntax_error";
       public static final String    info        ="info";
       public static final String    warn        ="warn";
       public static final String    text        ="text";

       
       
       public rResponse(String id,String response) {
              //this.txt=new StringBuffer();
              this.response     =response;
              this.id           =id;
              this.list_rcommand=new ArrayList<rCMD>();
              this.list_txt     =new ArrayList<String>();
              this.is_print     =false;
       }
       public rResponse(String id,String response,boolean _is_print) {
              //this.txt=new StringBuffer();
              this.response     =response;
              this.id           =id;
              this.list_rcommand=new ArrayList<rCMD>();
              this.list_txt     =new ArrayList<String>();
              this.is_print     =_is_print;
       }
       public String       getID  (){return id;}
       public String       getRes (){return response;}

       public void         appendBuf (String t){
    	      //txt.append(t).append('\n');
              list_txt.add(t);
       }

       public boolean      isPrint(){return is_print;}
       public void         setCMD(ArrayList<rCMD> l){list_rcommand=l;}

       @Override
       public String type() {return getClass().getName();}

       @Override
       public boolean run(rShell sh,BufferedInputStream buf_input){
              logger.debug("begin run cmd:"+id);
             
              for(int i=0;i<list_rcommand.size();i++){
                  rCMD cmd=list_rcommand.get(i);
                  if(cmd==null)continue;
                  boolean ret;
             
                  ret=cmd.run(sh,buf_input);
             
                  if(ret==false) {
                     logger.trace("cmd:"+cmd.toString()+" ret:"+ret);
                     logger.error("cmd:"+cmd.toString()+" ret:"+ret);
                     return ret;
                  }
              } 
              logger.debug("end run cmd:"+id);
              return true;
       }
       @Override
       public String[] print() {
              if(is_print){
                 String [] ret=new String [list_txt.size()];
                 ret=list_txt.toArray(ret);
                 return ret;
              }
              else return str_null;
       }
       @Override
       public byte[] getBuffer() {return null;}
       @Override
       public void setBuffer(byte[] buffer) {}


}
