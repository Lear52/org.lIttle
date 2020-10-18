package org.little.rcmd.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

public class rCommand implements rCMD {
       private static Logger logger = LoggerFactory.getLogger(rCommand.class);

       private sequences seq;
       private String    request;
       private String    response;
       private rShell    sh;
       private int       index;
       private String    name;

       public rCommand(rShell sh,String name,int index,String request,String response) {

              this.request=request;
              this.response=response;
              this.sh=sh;
              this.name=name;
              this.index=index;

              seq=new sequences();
              seq.add("ok"          ,response);
              seq.add("error"       ,"Error:");
              seq.add("syntax_error","Syntax error:");
              seq.add("info"        ,"Info:");
              seq.add("warn"        ,"Warning:");
/*
adm@DionisNX# show crypto disec conn
[#!]NAME         ID    SRC             DST             SN         LOC   REM   A B
adm@DionisNX#

adm@DionisNX# show crypto ike conns

adm@DionisNX#
*/


       }
       protected boolean sendRequest()  {
              if(request==null){
                 logger.trace("request is null");
                 return true;
              }
              try {
                  if(request.length()>0)sh.getOUT().write(request.getBytes());
                  else logger.trace("request is 0");
                  
                  sh.getOUT().write("\r\n".getBytes());
                  sh.getOUT().flush();
              } catch (IOException e) {
                  logger.error("sendRequest "+toString()+" ex:"+e);
                  return false;
              }

              rlog.print("send:"+request+"!\n");
            
              return true;
       }
       protected String getString(BufferedInputStream bufin){
              StringBuilder buf=new StringBuilder();
              int c;
              try {
                    while((c=bufin.read()) >= 0){
                          char _c=(char)c;
                          rlog.print(_c);
                          if(_c=='\r' || _c=='\n')break;
                          buf.append(_c);
                    }
                            
              } catch (IOException e) {
                 logger.error("getString "+toString()+" ex:"+e);
              }
            
              return buf.toString();
        }
 
        public boolean run()  {
            BufferedInputStream bufin = new BufferedInputStream(sh.getIN());
            return run(bufin);
        }
        public boolean run(BufferedInputStream bufin)  {
               
               sendRequest();
               
               int c;
               if(response==null)return true;
               try {
                    while((c=bufin.read()) >= 0){
                          char _c=(char)c;
                          byte _b=(byte)c;

                          rlog.print(_c);
        
                          sequence s=seq.put(_b);
                          if(s==null)continue;

                          rlog.print("find response");

                          if("ok".equals(s.getName())){
                             logger.trace("run:"+toString()+" for:"+s.getName()+" ret:true");
                             return true;
                          }
                          else
                          if("warn".equals(s.getName())){
                             logger.trace("run:"+toString()+" for:"+s.getName()+" ret:"+getString(bufin));
                             continue;
                          }
                          else
                          if("info".equals(s.getName())){
                             logger.trace("run:"+toString()+" for:"+s.getName()+" ret:"+getString(bufin));
                             continue;
                          }
                          else{ 
                             logger.trace("run:"+toString()+" for:"+s.getName()+" ret:false");

                             rlog.print(">:"+s.getName()+"\n");

                             String t=getString(bufin);

                             rlog.print("text:"+t+"\n");

                             return false;
                          }
                    }
                            
               } catch (IOException e) {
                    logger.error("sendRequest "+toString()+" ex:"+e);
                    return false;
               }
               return true;
        }
        
        
        public String toString(){
               return "rcmd:"+name+" index:"+index+" request:"+request+" "+"response:"+response;
        }  


        public static void main(String[] arg){
            boolean ret;
            rShell sh=new rShell();
            
            rCommand cmd0=new rCommand(sh,"",1,null,"# ");
            rCommand cmd1=new rCommand(sh,"",2,"configure terminal","(config)# ");
            rCommand cmd2=new rCommand(sh,"",3,"exit",null);
               
            if(arg.length>0)sh.setHost(arg[0]);
            else            sh.setHost("127.0.0.1");
            
            if(arg.length>1)sh.setUser(arg[1]);
            else            sh.setUser("root");
            
            if(arg.length>2)sh.setPasswd(arg[2]);
            else sh.setPasswd("biglear14");
            
            ret=sh.open();
            System.out.println("open connection:"+ret); 

            BufferedInputStream bufin = new BufferedInputStream(sh.getIN());

            System.out.println("----------------"); 
            ret=cmd0.run(bufin);            
            System.out.println("----------------"+ret); 
            ret=cmd1.run(bufin);            
            System.out.println("----------------"+ret); 
            ret=cmd2.run(bufin);            
            System.out.println("----------------"+ret); 

            sh.close();
            System.out.println("close connection"); 
            
            
        }

}
