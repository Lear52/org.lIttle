package org.little.http.handler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import org.little.http.auth.HttpAuth;
import org.little.http.auth.HttpAuthResponse;
import org.little.http.auth.commonHttpAuth;
import org.little.util.Except;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
//import io.netty.handler.codec.http.HttpObject;
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
import io.netty.util.AttributeKey;


public class lHttpRequest{
       private static final Logger          logger  = LoggerFactory.getLogger(lHttpRequest.class);
       private static final HttpDataFactory factory = new DefaultHttpDataFactory(false); 

       public static final AttributeKey<lHttpRequest> ATTRIBUTE_KEY = AttributeKey.valueOf("lHttpRequest");

       protected HttpRequest            request;
       protected HttpData               partialContent;
       protected boolean                keepAlive;
       protected URI                    uri;
       protected HttpPostRequestDecoder decoder;
       protected Set<Cookie>            cookies;

       protected lHttpResponse          response;

       protected HttpAuth               http_auth;
       
       protected String                 path;
       protected String                 app;
       protected HashMap<String,String> app_arg;
       protected ArrayList<lHttpBuf>    bin_buffer;
       
       public static final boolean RequestProcessOk=true;
       public static final boolean RequestProcessBad=false;
       
       public lHttpRequest(){
              clear();
              request       =null;
              cookies       =null;
              http_auth      =commonHttpAuth.getInstatce(null);
              response      =new lHttpResponse();

              path          =null;
              app           =null;
              app_arg       =new HashMap<String,String>();
              bin_buffer    =new ArrayList<lHttpBuf>();
       }
       public void clear(){
              keepAlive     =false;
              decoder       =null;
              partialContent=null;
       }

       public String                  getPath        (){return path;}
       public String                  getApp         (){return app;}
       public HttpVersion             protocolVersion(){if(request==null)return null;else return request.protocolVersion();}
       public String                  getURI         (){if(request==null)return null;else return request.uri();}
       public boolean                 isKeepAlive    (){return keepAlive;}
       public HttpHeaders             getHeaders     (){if(request==null)return null;else return request.headers();}
       public HttpMethod              getMethod      (){if(request==null)return null;else return request.method();}
       
       public HashMap<String, String> getQuery       () {return app_arg;}
       public ArrayList<lHttpBuf>     getBinBuffer   () {return bin_buffer;}
       public lHttpResponse           getResponse    () {return response;}
       public void                    setResponse    (lHttpResponse response) {this.response = response;}
       public String                  getUser        () {return http_auth.getResponse().getUser();}

       public void processHttpRequest(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
           HttpAuthResponse ret_auth = null;

           setHttpReq(request);
           //--------------------------------------------------------------
           // 
           //--------------------------------------------------------------
           ret_auth = Auth();
          
           logger.trace("processHttpRequest lHttpRequest:"+this + " HttpRequest"+ request);
          
           if(ret_auth==null) {
              response.sendAuthRequired(ctx,request,ret_auth);
              return;
           }
           if(ret_auth.isAuth()==false) {
              response.sendAuthRequired(ctx,request,ret_auth);
              return;
           }
           //--------------------------------------------------------------
           //
           //--------------------------------------------------------------
           //--------------------------------------------------------------------------------------
           if(HttpMethod.GET.equals(request.method())) {
              boolean ret=HttpGet(ctx);
              if(ret==RequestProcessBad)response.sendOk(ctx,this," ");
              return;                          
           }
           else
           if(HttpMethod.POST.equals(request.method()) || HttpMethod.PUT.equals(request.method())) {
              if(createPostRequestDecoder()==RequestProcessBad){
                 String txt= "createPostRequestDecoder==false";
                 logger.error(txt);
                 response.sendError(ctx,this,txt);
                 return;
              }
           }
           else{
              response.sendOk(ctx,this," ");
           }

       }
       //public boolean isWaitContent(){return decoder!=null;}
       public int addContent(ChannelHandlerContext ctx,HttpContent chunk){
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
              //
              boolean ret=false;
              if(HttpMethod.PUT.equals(request.method()))ret=HttpPut(ctx);
              if(HttpMethod.POST.equals(request.method()))ret=HttpPost(ctx);
              if(ret==false) {
                      response.sendOk(ctx,this," "); 
              }
              //
              clearContent();
              return 0;
           }
           return 1;
       }
       public void clearContent() {
           request = null;
           // destroy the decoder to release all resources
           if(decoder!=null)decoder.destroy();
           decoder = null;
       }
       
       //public int decodeChunk(HttpObject msg){
       //    if(msg instanceof HttpContent) {
       //       HttpContent chunk = (HttpContent) msg;
       //       return decodeChunk(chunk);
       //    }
       //    return 1;
       //}
       
       public boolean HttpGet(ChannelHandlerContext ctx){return RequestProcessBad;}
       public boolean HttpPost(ChannelHandlerContext ctx){return RequestProcessBad;}
       public boolean HttpPut(ChannelHandlerContext ctx){return RequestProcessBad;}
       //-------------------------------------------------------------------------------------------------
       //-------------------------------------------------------------------------------------------------
       //-------------------------------------------------------------------------------------------------


       private HttpAuthResponse  Auth(){
              logger.trace("begin auth");
              if(request==null){
                 logger.error("request==null");
                 return null;
              }

              HttpAuthResponse result_auth = http_auth.authParse(request);

              if(result_auth!=null){
                 if(result_auth.isAuth()){
                    logger.trace("auth:"+result_auth.isAuth()+" user:"+result_auth.getUser());
                 }
                 else logger.trace("auth:"+result_auth.isAuth()+" user:null");
              }
              else logger.trace("auth:null");

              logger.trace("end auth:"+result_auth.isAuth()+" user:"+result_auth.getUser());
              return result_auth;
       }
       protected boolean setHttpReq(HttpRequest _request){

                      clearContent();
                      clear();
                      request=_request;
                      keepAlive = HttpUtil.isKeepAlive(request);

                      String value = getHeaders().get(HttpHeaderNames.COOKIE);
                      if (value == null)  cookies = Collections.emptySet();
                      else                cookies = ServerCookieDecoder.STRICT.decode(value);

                      try{
                          uri = new URI(request.uri());
                          path=uri.getPath();
                          StringTokenizer parser = new StringTokenizer(path,"/",false);
                          app=path;
                          while(parser.hasMoreTokens()!=false){
                                app=parser.nextToken();
                          }
                          String  query =uri.getQuery();
                          logger.trace("path:"+path+" app:"+app+" query:"+query);
                          if(query!=null){
                             parser = new StringTokenizer(query,"&",false);
                             while(parser.hasMoreTokens()!=false){
                                  String p=parser.nextToken();
                                  StringTokenizer _parser=new StringTokenizer(p,"=",false);
                                  String field="";
                                  String field_data="";
                                  if(_parser.hasMoreTokens()!=false)field=_parser.nextToken();
                                  if(_parser.hasMoreTokens()!=false)field_data=_parser.nextToken();
                                  logger.trace("app:"+app+" field:"+field+" field_data:"+field_data);
                                  app_arg.put(field,field_data);
                             } 
                          }
                      }
                      catch(Exception e){
                            Except ex=new Except("parse URI",e);
                            logger.error(ex);
                            clear();
                            return RequestProcessBad;
                      }

                      return RequestProcessOk;
       }
       
       protected boolean createPostRequestDecoder(){
            try {
                decoder = new HttpPostRequestDecoder(factory, request);
            } 
            catch (ErrorDataDecoderException e) {
                  Except ex=new Except("create HttpPostRequestDecoder ",e);
                  logger.error(ex);
                  return RequestProcessBad;
            }
            return RequestProcessOk;
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
                           f.toString();
                           
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
                  try { 
                       String    field     ="";
                       String    field_data="";
                       field     =attribute.getName();
                       field_data=attribute.getString();  
                       app_arg.put(field,field_data);
                  } 
                  catch(IOException e1) {
                        Except ex=new Except("get attribute",e1);
                        logger.error(ex);
                        return;
                  }
               }
               else {
                      if(data.getHttpDataType() == HttpDataType.FileUpload) {
                         FileUpload fileUpload = (FileUpload) data;
                         String     f_name        = fileUpload.getFilename();
                         String     f_mime     = fileUpload.getContentType();

                         if(fileUpload.isCompleted()) {
                            if(fileUpload.length() < 1000000) {
                               try {
                                    byte[] bin_buf=fileUpload.get();
                                    if(bin_buf.length!=fileUpload.length()) logger.error("compare false "+bin_buf.length+"!="+fileUpload.length());
                                    bin_buffer.add(new lHttpBuf(f_name,f_mime,bin_buf));
                                    
                                   
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

       //-----------------------------------------------------------------------------------------------------------------
       //private void responseOk(ChannelHandlerContext ctx) throws Exception {
       //           response.sendOk(ctx,this," ");; 
       //}



}

