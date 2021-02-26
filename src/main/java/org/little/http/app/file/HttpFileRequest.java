package org.little.http.app.file;

import org.little.http.handler.lHttpBuf;
import org.little.http.handler.lHttpRequest;
import org.little.util.Logger;
import org.little.util.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class HttpFileRequest extends lHttpRequest{
       private static final Logger   logger  = LoggerFactory.getLogger(HttpFileRequest.class);

       private HttpFileResponse       response;
       
       public HttpFileRequest(){
              clear();
              response=new HttpFileResponse();
              setResponse(response);
       }


       @Override
       public void clear(){
                 //response=null;             
              super.clear();
       }

       @Override
       public boolean HttpGet(ChannelHandlerContext ctx){
              String cmd;
              cmd  =getPath();
              logger.trace("set 0 cmd:"+cmd);
           
              if(cmd.startsWith("/post")){cmd="post" ; }                  // http://x.x.x.x:pppp/post                         - put file
              else                      
              if(cmd.startsWith("/env" )){cmd="env"  ; }                  // http://x.x.x.x:pppp/env                          - get test (print enviroment)
              else
              if(cmd.startsWith("/test" )){cmd="test"  ; }                  // http://x.x.x.x:pppp/env                          - get test (print enviroment)
              else{
                 cmd="file" ;                                             // http://x.x.x.x:pppp/filename.html                - get http file 
              }
              //is_correct=true;
              logger.trace("set 1 cmd:"+cmd);
                  
              if("file".equals(cmd)){
                   logger.trace("set 11 cmd:"+cmd+" response:"+response);

                   response.getFile(ctx,this);

                   logger.trace("set 12 cmd:"+cmd);

                   return RequestProcessOk;
              }
              if("test".equals(cmd)){
                  logger.trace("set 11 cmd:"+cmd+" response:"+response);

                  response.getTest1(ctx,this);

                  logger.trace("set 12 cmd:"+cmd);

                  return RequestProcessOk;
             }

              return RequestProcessBad;// no process
       }
       @Override
       public boolean HttpPost(ChannelHandlerContext ctx){
              return HttpUpload(ctx);
       }
       @Override
       public boolean HttpPut(ChannelHandlerContext ctx){
              return HttpUpload(ctx);
       }
       private boolean HttpUpload(ChannelHandlerContext ctx){
           
           lHttpBuf file=getBinBuffer().get(0); /**/
           file.getName();
           file.getBuf();
           response.getFile(ctx,this,"/redirect.html");
           //response.sevaUploadFile(ctx,this);
           logger.trace("HttpUpload:"+file.getName());

           return RequestProcessOk;
    }




}

