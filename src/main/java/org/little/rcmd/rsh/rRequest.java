package org.little.rcmd.rsh;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.little.util.Logger;
import org.little.util.LoggerFactory;
import org.little.util._ByteBuilder;


public class rRequest implements rCMD {
       private static Logger logger = LoggerFactory.getLogger(rRequest.class);

       private sequences seq;
       private String    request;
       private rResponse response;
       private int       index;
       private String    command_id;

       public static final String    response_ok          ="ok";
       public static final String    response_error       ="error";
       public static final String    response_syntax_error="syntax_error";
       public static final String    response_info        ="info";
       public static final String    response_warn        ="warn";
       public static final String    response_text        ="text";

       
       
       public rRequest(String _command_id,int index,String request,rResponse _response) {

              this.request=request;
              //this.response=response;
              this.response=_response;
              this.command_id=_command_id;
              this.index=index;

              seq=new sequences();

              if(response!=null)seq.add(response_ok,response.getID(),response.getRes());
              seq.add(response_error       ,"","Error:");
              seq.add(response_syntax_error,"","Syntax error:");
              seq.add(response_info        ,"","Info:");
              seq.add(response_warn        ,"","Warning:");
/*
adm@DionisNX# show crypto disec conn
[#!]NAME         ID    SRC             DST             SN         LOC   REM   A B
adm@DionisNX#

adm@DionisNX# show crypto ike conns

adm@DionisNX#
*/


       }
       
       private boolean sendRequest(rShell sh)  {
              if(request==null){
                 logger.trace("comand:"+command_id+" index:"+index+" request is null");
                 return true;
              }
              try {
                  if(request.length()>0)sh.getOUT().write(request.getBytes());
                  else logger.trace("request is 0");
                  
                  sh.getOUT().write("\r\n".getBytes());
                  sh.getOUT().flush();
              } catch (IOException e) {
                  logger.error("comand:"+command_id+" index:"+index+" send request:"+toString()+" ex:"+e);
                  return false;
              }
              logger.trace("comand:"+command_id+"send request:"+request+" index:"+index);

              rlog.print("send request:"+request+"");
            
              return true;
       }
       private String getString(BufferedInputStream buf_input){
              StringBuilder buf=new StringBuilder();
              int c;
              try {
                    while((c=buf_input.read()) >= 0){
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

        @Override
        public String type() {return getClass().getName();}

        @Override
        public String[] print(){if(response==null) return str_null;return response.print();}

        public boolean run(rShell sh,BufferedInputStream buf_input){
               sendRequest(sh);
               if(response==null){
                  logger.trace("no wait response is null");
                  return true;
               }

               logger.trace("wait response:"+response.getRes() +" id:"+response.getID());

//System.out.println("\n------------------------------------");

               int c=0;
               _ByteBuilder buf_out=new _ByteBuilder(1024);
               boolean is_print=true;
               try {
                   while((c=buf_input.read()) >= 0){
                          byte _b=(byte)c;
                          char _c=(char)c;

//System.out.print(_c);

                          rlog.print(_c);

                          if(_b=='\r' || _b=='\n') {
                             if(buf_out.length()==0)continue;
                             byte [] buf_byte=buf_out.getBytes();
                             String str_out=new String(buf_byte,Charset.forName("UTF-8"));
                             response.appendBuf(str_out);
                             rlog.print(str_out);
                             buf_out.reset();
                             continue;
                          }
                          
                          if(_b==27){is_print=false;continue;}
                          if(is_print==false){
                             if(_b=='='){is_print=true;continue;}
                             if(_b=='>'){is_print=true;continue;}
                             if(_b>='a' && _b<='z'){is_print=true;continue;}
                             if(_b>='A' && _b<='Z'){is_print=true;continue;}
                          }
                          
                          if(is_print){
                             buf_out.append(_b);
                             sequence s=seq.put(_b);
                             if(s!=null){
                                //---------------------------------------------------------------------
                                if(response_ok.equals(s.getType())){
                                   // received the expected response
                                   if(buf_out.length()!=0){
                                      byte [] buf_byte=buf_out.getBytes();
                                      String str_out=new String(buf_byte,Charset.forName("UTF-8"));
                                      response.appendBuf(str_out);
                                      rlog.print(str_out);
                                   }
                                   logger.trace("response type:"+s.getType()+" id:"+s.getID()+" Ok");
                                   return true;
                                }
                                else
                                if(response_warn.equals(s.getType())){
                                    // received warn response
                                   String str=getString(buf_input);// get string to end
                                   logger.trace("run:"+toString()+" for response type:"+s.getType()+" ret:"+str);
                                   continue;
                                }
                                else
                                if(response_info.equals(s.getType())){
                                   // received info response
                                   String str=getString(buf_input);// get string to end
                                   logger.trace("run:"+toString()+" for response type:"+s.getType()+" ret:"+str);
                                   continue;
                                }
                                else
                                if(response_error.equals(s.getType())){
                                   // received error response
                                   String str=getString(buf_input);// get string to end
                                   logger.trace("run:"+toString()+" for response type:"+s.getType()+" ret:"+str);
                                   continue;
                                }
                                else
                                if(response_syntax_error.equals(s.getType())){
                                   // received syntax error response
                                   String str=getString(buf_input);// get string to end
                                   logger.trace("run:"+toString()+" for response type:"+s.getType()+" ret:"+str);
                                   continue;
                                }
                                //---------------------------------------------------------------------
                             }
                          }/*end is_print*/
                   }/*end while*/

               } catch (IOException e) {
                    if(buf_out.length()!=0){
                       byte [] buf_byte=buf_out.getBytes();
                       String str_out=new String(buf_byte,Charset.forName("UTF-8"));
                       response.appendBuf(str_out);
                       rlog.print(str_out);
                    }
                    logger.error("get response "+toString()+" ex:"+e);
                    return false;
               }
               
               return true;
        }
        //@Override
/*
        public boolean run2(rShell sh,BufferedInputStream buf_input)  {
               StringBuilder str_out=new StringBuilder();
               sendRequest(sh);
               if(response==null)return true;
//System.out.println("send request");
               try {
                    String str_line=null;
                    while((str_line=getStringLine(buf_input))!=null){
                          Reader input =new StringReader(str_line);
                          int c;
System.out.println("get line:"+str_line);
                          while((c=input.read()) >= 0){
                                char _c=(char)c;
                                
 System.out.print(_c);
                         
                                str_out.append(_c);
                                sequence s=seq.put(_c);
                                if(s==null)continue;
                         
                                if(response_ok.equals(s.getType())){
System.out.println("run  cmd:"+s.getType()+" id:"+s.getID()+" Ok");
                                   // received the expected response
                                   response.getBuf().append(str_out);
                                   logger.trace("run  cmd:"+s.getType()+" id:"+s.getID()+" Ok");
                                   return true;
                                }
                          }
                          str_out.append('\n');
                          seq.reset();
                    }
                            
               } catch (IOException e) {
                    logger.error("sendRequest "+toString()+" ex:"+e);
                    return false;
               }
               return true;
        }
*/
/*
        //@Override
        public boolean run1(rShell sh,BufferedInputStream buf_input)  {
               StringBuilder str_out=new StringBuilder();
               sendRequest(sh);
               
               int c;
               if(response==null)return true;
               try {
                    while((c=buf_input.read()) >= 0){
                          char _c=(char)c;
                          

                          rlog.print(_c);
                          str_out.append(_c);
                          sequence s=seq.put(_c);
                          if(s==null)continue;

                          if(response_ok.equals(s.getType())){
                             // received the expected response
                             response.getBuf().append(str_out);
                             logger.trace("run  cmd:"+s.getType()+" id:"+s.getID()+" Ok");
                             return true;
                          }
                          else
                          if(response_warn.equals(s.getType())){
                              // received warn response
                             String str=getString(buf_input);// get string to end
                             str_out.setLength(0);
                             str_out.trimToSize();
                             logger.trace("run:"+toString()+" for:"+s.getType()+" ret:"+str);
                             continue;
                          }
                          else
                          if(response_info.equals(s.getType())){
                             // received info response
                             String str=getString(buf_input);// get string to end
                             str_out.setLength(0);
                             str_out.trimToSize();
                             logger.trace("run:"+toString()+" for:"+s.getType()+" ret:"+str);
                             continue;
                          }
                          else
                          if(response_error.equals(s.getType())){
                             // received error response
                             String str=getString(buf_input);// get string to end
                             str_out.setLength(0);
                             str_out.trimToSize();
                             logger.trace("run:"+toString()+" for:"+s.getType()+" ret:"+str);
                             continue;
                          }
                          else
                          if(response_syntax_error.equals(s.getType())){
                             // received syntax error response
                             String str=getString(buf_input);// get string to end
                             str_out.setLength(0);
                             str_out.trimToSize();
                             logger.trace("run:"+toString()+" for:"+s.getType()+" ret:"+str);
                             continue;
                          }
                    }
                            
               } catch (IOException e) {
                    logger.error("sendRequest "+toString()+" ex:"+e);
                    return false;
               }
               return true;
        }
        
*/        
        public String toString(){
               return "rcmd:"+command_id+" index:"+index+" request:"+request+" "+"response:"+response;
        }  

        @Override
 	public byte[] getBuffer() {
 	    // TODO Auto-generated method stub
 		      return null;
 	}
 	@Override
 	public void setBuffer(byte[] buffer) {
 	    // TODO Auto-generated method stub
 		
        }

        public static void main(String[] arg){
            boolean ret;
            rShell sh=new rShell();
            
            rRequest cmd0=new rRequest("",1,null,new rResponse("01","#"));
            rRequest cmd1=new rRequest("",2,"configure terminal",new rResponse("02","(config)#"));
            rRequest cmd2=new rRequest("",3,"exit",null);
               
            if(arg.length>0)sh.setHost(arg[0]);
            else            sh.setHost("127.0.0.1");
            
            if(arg.length>1)sh.setUser(arg[1]);
            else            sh.setUser("root");
            
            if(arg.length>2)sh.setPasswd(arg[2]);
            else sh.setPasswd("biglear14");
            
            ret=sh.open();
            System.out.println("open connection:"+ret); 

            BufferedInputStream buf_input = new BufferedInputStream(sh.getIN());

            System.out.println("----------------"); 
            ret=cmd0.run(sh,buf_input);            
            System.out.println("----------------"+ret); 
            ret=cmd1.run(sh,buf_input);            
            System.out.println("----------------"+ret); 
            ret=cmd2.run(sh,buf_input);            
            System.out.println("----------------"+ret); 

            sh.close();
            System.out.println("close connection"); 
            
            
        }

}
