package org.little.http.internal;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import org.little.http.auth.HttpAuth;
import org.little.http.auth.HttpAuthResponse;
import org.little.store.lMessage;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;


public class HttpX509Request{
       private static final Logger          logger  = LoggerFactory.getLogger(HttpX509Request.class);
       private static final HttpDataFactory factory = new DefaultHttpDataFactory(false); 

       protected HttpRequest            request;
       protected HttpData               partialContent;
       protected String                 cmd;
       protected boolean                is_correct;
       protected boolean                keepAlive;
       protected URI                    uri;
       protected HttpPostRequestDecoder decoder;

       protected Set<Cookie>            cookies;



       protected String                 store;
       protected String                 folder;
       protected String                 msg;
       protected HttpAuth               auth;


       protected lMessage              upload_msg;

       public HttpX509Request(){
               clear();
               request       =null;
               cookies       =null;
               auth=HttpAuth.getInstatce();
       }

       private void r_clear(){
               folder        =null;
               cmd           =null;   
               msg           =null;   
               is_correct    =false;

       }
       private void clear(){
               r_clear();
               store         =null; 
               keepAlive     =false;
               decoder       =null;
               partialContent=null;
               upload_msg    =new lMessage();

       }

       public String      getStore       (){return store ;}
       public String      getFolder      (){return folder;}
       public String      getCmd         (){return cmd   ;}
       public String      getPath        (){if(uri==null)return null;else return uri.getPath();}
       public String      getMsg         (){return msg   ;}
       public boolean     isCorrect      (){return is_correct;}
       public HttpVersion protocolVersion(){if(request==null)return null;else return request.protocolVersion();}
       public String      getURI         (){if(request==null)return null;else return request.uri();}
       public boolean     isKeepAlive    (){return keepAlive;}
       public HttpHeaders getHeaders     (){if(request==null)return null;else return request.headers();}
       public HttpMethod  getMethod      (){if(request==null)return null;else return request.method();}

       public lMessage   getUploadMsg   (){return upload_msg;}

       public HttpAuthResponse  Auth(){
              logger.trace("begin auth");

              if(request==null){
                 logger.error("request==null");
                 return null;
              }

              HttpAuthResponse ret_auth = auth.authParse(request);

              if(ret_auth!=null){
                 if(ret_auth.isAuth()){
                    store=ret_auth.getUser();
                    logger.trace("auth:"+ret_auth.isAuth()+" store:"+store);
                 }
                 else logger.trace("auth:"+ret_auth.isAuth()+" store:null");
              }
              else logger.trace("auth:null");

              logger.trace("end auth:"+ret_auth.isAuth()+" store:"+store);
              return ret_auth;
       }
       //public HttpAuth   getAuth        (){return auth;}

       public boolean setHttpReq(HttpRequest _request){

                      clearDecoder();
                      clear();
                      request=_request;
                      keepAlive = HttpUtil.isKeepAlive(request);

                      String value = getHeaders().get(HttpHeaderNames.COOKIE);
                      if (value == null)  cookies = Collections.emptySet();
                      else                cookies = ServerCookieDecoder.STRICT.decode(value);

                      try{uri = new URI(request.uri());
                      }
                      catch(Exception e){
                            Except ex=new Except("parse URI",e);
                            logger.error(ex);
                            clear();
                            return false;
                      }

                      return true;
       }

       public boolean isDecoder(){return decoder!=null;}

       public boolean createPostRequestDecoder(){
            try {
                decoder = new HttpPostRequestDecoder(factory, request);
            } 
            catch (ErrorDataDecoderException e) {
                  Except ex=new Except("create HttpPostRequestDecoder ",e);
                  logger.error(ex);
                  return false;
            }
            return true;
       }

       public int decodeChunk(HttpObject msg){
              if(msg instanceof HttpContent) {
                 HttpContent chunk = (HttpContent) msg;
                 return decodeChunk(chunk);
              }
              return 1;
       }

       public int decodeChunk(HttpContent chunk){
              if (decoder == null) return -1;
              try {
                    decoder.offer(chunk);
              } 
              catch(ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    return -1;
              }
              readDataByChunk();
              if(chunk instanceof LastHttpContent) {
                 logger.trace("LastHttpContent");
                 clearDecoder();
                 return 0;
              }
              return 1;
       }

       private void readDataByChunk() {
               logger.trace("readDataByChunk");
               try {
                  while(decoder.hasNext()) {
                        InterfaceHttpData data = decoder.next();
                        if(data != null) {
                          // check if current HttpData is a FileUpload and previously set as partial
                          if(partialContent == data) {
                             logger.trace(" 100% (FinalSize: " + partialContent.length() + ")");
                             partialContent = null;
                          }
                          // new value
                          writeData(data);
                        
                      }
                  }
                  
                  InterfaceHttpData data1 = decoder.currentPartialHttpData();
                  if(data1 != null) {
                     if(partialContent == null) {
                        partialContent = (HttpData) data1;
                        if(partialContent instanceof FileUpload) {
                           FileUpload f=(FileUpload)partialContent;
                           
                        }
                        else
                        if(partialContent instanceof Attribute) {
                        }
                        else {
                        }
                     }
                  }
                  
              } 
              catch (EndOfDataDecoderException e1) {
                    Except ex=new Except("EndOfDataDecoderException END OF CONTENT CHUNK BY CHUNK",e1);
                    logger.error(ex);
                  // end
              }
              
       }
       private void writeData(InterfaceHttpData data) {

               if(data.getHttpDataType() == HttpDataType.Attribute) {

                  Attribute attribute = (Attribute) data;
                  //lMessage  m         = getUploadMsg();
                  String    field=null;
                  String    arg  =null;

                  try { 
                       field=attribute.getName();
                       arg  =attribute.getString();  
                  } 
                  catch(IOException e1) {
                        Except ex=new Except("get attribute",e1);
                        logger.error(ex);
                        return;
                  }

                  if("to".equals(field)){upload_msg.addTO(arg);}
                  else
                  if("subject".equals(field)){upload_msg.setSubject(arg);}

               }
               else {
                      if(data.getHttpDataType() == HttpDataType.FileUpload) {
                         FileUpload fileUpload = (FileUpload) data;
                         String     f_n        = fileUpload.getFilename();
                         String     f_mime     = fileUpload.getContentType();
                         //lMessage   m          = getUploadMsg();

                         upload_msg.setFilename(f_n);
                         upload_msg.setMime    (f_mime);
                         upload_msg.setSentDate();
                         upload_msg.setFrom(store);
                         //logger.trace("HttpDataType.FileUpload");
                         //logger.trace("\tContent len="+fileUpload.length()+" ---------------------------------------------------");

                         if(fileUpload.isCompleted()) {
                            if(fileUpload.length() < 1000000) {
                               try {
                                    byte [] bin_buf=fileUpload.get();
                                    upload_msg.setBodyBin(bin_buf);
                                    if(bin_buf.length!=fileUpload.length()) logger.error("compare false "+bin_buf.length+"!="+fileUpload.length());

                                    //String s =new String(bin_buf);
                                    //logger.trace(s);
                                    //logger.trace("-------------------------------------------------------------------------------");
                               } 
                               catch(IOException e1) {
                                     Except ex=new Except("get file upload",e1);
                                     logger.error(ex);
                               }
                            } 
                            else {
                            	logger.error("\tFile too long to be printed out:" + fileUpload.length() + "\r\n");
                            }
                         } 
                         else {
                               logger.error("\r\nFile to be continued but should not!\r\n");
                         }
                      }
                      else logger.error("error data:"+data.getHttpDataType()+"\r\n");
               }

       }
       public void clearDecoder() {
              request = null;
              // destroy the decoder to release all resources
              if(decoder!=null)decoder.destroy();
              decoder = null;
       }


       public boolean parsePath(){

              r_clear();

              try{
                  //logger.trace("set 0 cmd:"+cmd+" store:"+store);
                  cmd=getPath();
                  logger.trace("set 0 cmd:"+cmd+" store:"+store);
                 
                  if(cmd.startsWith("/get" )){cmd="get"  ;is_correct=true;}                   // get http://x.x.x.x:pppp/get?folder=name&msg=num  - get file(foldername, num_msg)
                  else
                  if(cmd.startsWith("/list")){cmd="list" ;folder="inbox";is_correct=true;}    // get http://x.x.x.x:pppp/list?folder=name         - get list file
                  else
                  if(cmd.startsWith("/del" )){cmd="del"  ; is_correct=true;}                  // get http://x.x.x.x:pppp/del?folder=name&msg=num  - del file(foldername, num_msg) 
                  else
                  if(cmd.startsWith("/user")){cmd="user" ; is_correct=true;}                  // get http://x.x.x.x:pppp/user                     - list user
                  else
                  if(cmd.startsWith("/post")){cmd="post" ; is_correct=true;}                  // http://x.x.x.x:pppp/post                         - put file(foldername=outbox)
                  else                      
                  if(cmd.startsWith("/env" )){cmd="env"  ; is_correct=true;}                  // http://x.x.x.x:pppp/env                          - get test (print enviroment)
                  else{
                     cmd="file" ;                                                             // http://x.x.x.x:pppp/filename.html                - get http file 
                     is_correct=true;
                     return true;
                  }
                  is_correct=true;
                 
                  StringTokenizer parser=null;
                  String          query =uri.getQuery();
                 
                  logger.trace("set 1 cmd:"+cmd+ " is_correct:"+is_correct+ " query:"+query);
                 
                  if(query!=null){
                     parser = new StringTokenizer(query,"&",true);
                     while(parser.hasMoreTokens()!=false){
                           String p=parser.nextToken();
                  //logger.trace("p:"+p);
                           if(p.startsWith("folder=")){folder=p.substring("folder=".length());}
                           if(p.startsWith("msg="   )){msg   =p.substring("msg=".length());}
                     }
                  }
                  logger.trace("set 2 cmd:"+cmd+ " is_correct:"+is_correct+" store:"+store+ " folder:"+folder+" msg:"+msg);
              }
              catch(NoSuchElementException e){
                    Except ex=new Except("parse cmd",e);
                    logger.error(ex);
                    return false;
              }
              logger.trace("set 3 cmd:"+cmd+ " is_correct:"+is_correct+" store:"+store+ " folder:"+folder+" msg:"+msg);

              if(cmd.equals("get")){
                 //if(store==null){clear(); is_correct=false;return false;}
                 if(folder==null){clear();is_correct=false;return false;}
                 if(msg   ==null){clear();is_correct=false;return false;}
              }
              if(cmd.equals("list")){
                 //if(store==null){clear(); is_correct=false;return false;}
                 if(folder==null){clear();is_correct=false;return false;}
              }

              is_correct=true;

              logger.trace("set 4 cmd:"+cmd+ " is_correct:"+is_correct+" store:"+store+ " folder:"+folder+" msg:"+msg);

              return true;
       }
       public String toString(){
             return "cmd="+cmd+" store="+store+" folder="+folder+" msg="+msg+" is_correct:"+is_correct;
       }




}

